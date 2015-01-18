package com.vish.geolookup;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;




public class GenerateLookupCache {

  public static void main(String[] args) {
    
    try {

      //most extreme points of 48 contiguous US states
      double startLat = 24.00;
      double startLon = -125.00;
      double endLat = 50.00;
      double endLon = -66.00;
      
      //casted to integer to optimize processing 
      int iStartLat = (int) startLat*100;
      int iStartLon = (int) startLon*100;
      int iEndLat = (int) endLat*100;
      int iEndLon = (int) endLon*100;

      //intervals for each thread
      //TODO calculate interval using startlat and endLat
      int startLatInterval = (int) startLat*100;
      int endLatInterval = (int) (startLat+2.60)*100;
      String threadNum;
      //array to store threads
      RunnableGeoPt[] geoPtArr = new RunnableGeoPt[10];
      //creates 10 threads
      for (int i = 0; i < geoPtArr.length; i++) {
        threadNum = Integer.toString(i);
        geoPtArr[i] = new RunnableGeoPt(threadNum, startLatInterval, iStartLon, 
            endLatInterval, iEndLon, new DefaultHttpClient());
        startLatInterval = endLatInterval+1;
        endLatInterval = endLatInterval+260;
      }
      
      //runs each thread
      for (RunnableGeoPt pt : geoPtArr) {
        pt.start();
      }

      //TODO implement wait for completion and exit
      try {
          Thread.sleep(100000);                 //1000 milliseconds is one second.
      } catch(InterruptedException ex) {
          Thread.currentThread().interrupt();
      }     
      
    } catch (Exception e) {

      e.printStackTrace();
    }

  }
  
  //generates text file for each thread
  //csv files -> 2 values for each record:
  //1: encoded Lat/Lon in 32-bit int/ 16-bit msb -> lat, 16-bit lsb -> lon
  //2: encoded corresponding zipcode index along with negative flags for lat/lon
  //16-bit lsb contains zipcode index and 16th bit contains negative flag for lon and 17th for lat

  //ex: 306786429,106358
  //lat = 16-bit msb of 306786429 >> 16 = 4681 -> 46.81
  //lon = 16-bit lsb of 306786429 & (2^16)-1 = 12413 -> 124.13 (negative denoted by flag)

 
  public static void generateGeoPtTxtFile(int startLat, int startLon, int endLat, int endLon, DefaultHttpClient httpClient, HttpPost postRequest, String fileName) {
    FileWriter writer = null;
    try {
      writer = new FileWriter(fileName);
    } catch (IOException e) {
      System.out.println("Could not create path");
    }
    
    int esSuccessCount = 0;
    int esFailureCount = 0;
    
    
    //populates file with a encoded lat/lons and encoded indexes
    int lat = startLat;
    int lon = startLon;
    while (lat <= endLat) {
      //iterates through longitude range for each latitude
      lon = startLon;
      while (lon <= endLon) {
        //processes current latitude and longitude to find the closest ZIP area in a radius of 20km
        NearestGeoPoint pt = processPoint(lat, lon, "20km", postRequest, httpClient);
        if (pt != null) {
          esSuccessCount++;

          //encoded lat/lon key
          Integer key = (lat << 16) + lon;

          //index of lat/lon that corresponds to ZIP area
          Integer index = Integer.valueOf(pt.getIndex());
          
          try {
            //writes key and index values to file
            writer.append(key+",");
            writer.append(String.valueOf(index));
            writer.append("\n");
          } catch(IOException e) {
            System.out.println("Could not add record to text");
          }
        } else {
          esFailureCount++;
        }
        //increments longitude
        lon++;
      }
      //increments latitude
      lat++;
    }

    System.out.println("Success count: " + esSuccessCount);
    System.out.println("Failure count: " + esFailureCount);

    try {
      //closes writer after processing points and writing all keys and indexes for intervals
      writer.flush();
      writer.close();
    } catch(IOException e) {
      System.out.println("Could not close writer");
    }
  }
  
  //method to obtain nearest ZIP area given a latitude and longitude and radius
  private static NearestGeoPoint processPoint(int lat, int lon, String distanceKM, HttpPost postRequest, DefaultHttpClient httpClient) {
    NearestGeoPoint pt;
    
    try {
      //passes in arguments to a queryRequest object that is mapped to a JSON string
      String latStr = Double.toString(((double) lat)/100);
      String lonStr = Double.toString(((double) lon)/100);
      QueryRequest queryRequest = createQueryRequest(latStr, lonStr, distanceKM);
      ObjectMapper readMapper = new ObjectMapper();
      String query_body = readMapper.writeValueAsString(queryRequest);
      
      //executes JSON string and gets information about nearest point from elasticsearch
      StringEntity input = new StringEntity(query_body);
      input.setContentType("application/json");
      postRequest.setEntity(input);
  
      HttpResponse response = httpClient.execute(postRequest);
  
      if (response.getStatusLine().getStatusCode() != 200) {
        throw new RuntimeException("Failed : HTTP error code : "
            + response.getStatusLine().getStatusCode());
      }
  
      BufferedReader br = new BufferedReader(new InputStreamReader(
          (response.getEntity().getContent())));
  
      String output;
      
      //JSON representation of nearest point
      StringBuffer sb = new StringBuffer();
      
      while ((output = br.readLine()) != null) {
        sb.append(output);
      }
      
      //maps JSON representation to object locationResponse whose attributes correspond to JSON fields
      ObjectMapper mapper = new ObjectMapper();
      LocationResponse locationResponse = mapper.readValue(sb.toString(), LocationResponse.class);
      
      //creates nearest point object from locationResponse
      pt = createNearestPoint(locationResponse);
    } catch (Exception e) {
      return null;
    }
    return pt;
  }
  
  private static NearestGeoPoint createNearestPoint(LocationResponse locationResponse) {
    NearestGeoPoint pt;
    try {
      //gets all components of locationResponse object(java representation of JSON string)
      String lat = locationResponse.getHits().getHits().get(0).get_source().getLocation().getLat();
      String lon = locationResponse.getHits().getHits().get(0).get_source().getLocation().getLon();
      String zip = locationResponse.getHits().getHits().get(0).get_source().getZipcode();
      String city = locationResponse.getHits().getHits().get(0).get_source().getCity();
      String state = locationResponse.getHits().getHits().get(0).get_source().getState();
      String index = locationResponse.getHits().getHits().get(0).get_source().getIndex();
      //returns point with above fields
      pt = new NearestGeoPoint(new Location(lat, lon), state, zip, city, index);
    } catch(Exception e) {
      return null;
    }
    return pt;
  }

  private static QueryRequest createQueryRequest(String lat, String lon, String distanceKM) {
    //objects correspond to JSON field names and are populated with given parameters
    Location location = new Location();
    location.setLat(lat);
    location.setLon(lon);
    GeoData geoData = new GeoData(location, "asc", "km");
    GeoDistance geoDistance = new GeoDistance(geoData);
    List<GeoDistance> geoDistances = new ArrayList<GeoDistance>();
    geoDistances.add(geoDistance);
    MatchAll matchAll = new MatchAll();
    FilteredQuery filteredQuery = new FilteredQuery(matchAll);
    GeoArea geoArea = new GeoArea(distanceKM, location);
    Filter filter = new Filter(geoArea);
    Filtered filtered = new Filtered(filteredQuery, filter);
    Query query = new Query(filtered);
    int resultSize = 1;
    return new QueryRequest(geoDistances, query, resultSize);
  }

}

//thread class
class RunnableGeoPt implements Runnable {
  private Thread t;
  private String threadName;
  int startLat;
  int startLon;
  int endLat;
  int endLon;
  DefaultHttpClient httpClient;
  //post request comes from elasticsearch run locally
  HttpPost postRequest = new HttpPost(
      "http://localhost:9200/geo_data/area/_search?pretty=true");
  
  RunnableGeoPt(String name, int startLat, int startLon, int endLat, int endLon, DefaultHttpClient httpClient) {
    threadName = name;
    this.startLat = startLat;
    this.startLon = startLon;
    this.httpClient = httpClient;
    this.endLat = endLat;
    this.endLon = endLon;
  }
  
  //runs thread and creates a text file with encoded lat/lon and flagged indexes
  //prints when finished
  public void run() {
    GenerateLookupCache.generateGeoPtTxtFile(startLat, startLon, endLat, endLon, httpClient, postRequest,
    "/Users/vish/projects/geoLatLon/geoProj/testDir/GeoThread"+threadName+".txt");
    System.out.println("Geo Thread " + threadName + " done.");
  }

  public void start ()
  {
    System.out.println("Starting Geo Thread " + threadName);
    if (t == null)
    {
      t = new Thread (this, threadName);
      t.start ();
    }
  }

}