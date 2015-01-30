# **Implementing Fast in-memory geo lookup of city/zipCode for given latitude/longitude**

Preliminary version.
Version 0.9
## **Brief description**


This is an reference implementation, adapted from the following blog: https://www.paypal-engineering.com/2015/01/29/implementing-a-fast-and-light-weight-geo-lookup-service/

Geo lookup is commonly required feature used by many websites and applications. Most smartphone applications can send latitude and longitude to server applications. Server applications use the latitude and longitude to perform geo lookup. Geo lookup falls into 2 categories:

  * For a given latitude and longitude, retrieve full postal address including street, city, zip.
  * For a given latitude and longitude, retrieve nearest city with zip.

Overwhelming majority of websites and applications require only city and zip only. These web services and web sites use paid data source and paid software for retrieving the zip code. 

The scope of this software is for server applications requiring to extract nearest city with zip for a given latitude and Longitude. This document describes a light weight in memory fast look up of zip code for a given Lat/Lon. This article describes how to implement fast geo look up service.

## **Data providers for Latitude and longitude for city and zip**

Though there are many providers who sell data and software for a cost, this data is available free from government web sites and .org web sites. Here are 2 of them

  * geonames.org : http://download.geonames.org/export/zip/ - File per country. Contains Lat/Long/city/zip in tab limited CSV format.
  * opengeocode : http://opengeocode.org/download.php

## **Geo look up algorithms**

All these data sources typically have around 49000 records for USA available in CSV format. Each record contains lat, lon, state, city and zip. There will be one record per zip code.
The geo lookup process typically involves the following steps

  * User request is received with Latitude and logitude by the web service or application.
  * Query the data source and retrieve the nearest zip calculated using nearest geo distance algorithm.

Nearest point calculation involves selecting set of points (around x Km radius) from CityZipLatLong database around the given lat/long and determine the point with minimum distance among the distances from each selected point. For US, assuming 20KM radius is an optimal choice and assuming 20+ points selected for minimum distance calculation, it might take 50+ of milliseconds.

Geographical distance between 2 lat/long is explained here. http://en.wikipedia.org/wiki/Geographical_distance. You can implement this algorithm or use open source software that supports geo point data. Elastic search server is one such open source server that can be used for this purpose.

  * You can implement this algorithm or use open source software to retrieve nearest point.
  * Ingest the zipcode data into SQL database. Implement SQL client to retrieve the nearest point.
  * Ingest data into elastic search server. Use geo point query to retrieve the nearest point.

## **Typical industry implementations**

Typical implementations use either paid or in house software. These implementations use on demand caching. The caching will be done for each lat/lon. On demand caching has few disadvantages

  * Requires use of services to retrieve nearest point for requests with new lat/lon.
  * Requires provisioning software (service) for new points and make on demand request. Need to maintain this service and software.
  * Cache size may end up being huge of the order of multibillions since Lat/Lon values have 4-digit precision. For example, number of points in US will be 250,000 (valid latitudes between 24.0000 and 49.9999) times 500,000(valid lons between -122.0000 and -70.0000) equaling 125 billion.

## **Data perspective and alternate approach**


The goal of this approach is to enable simple and ultra fast geo lookup service that does not use geo distance calculation services during user request at run time. This implies we need to create cache through pre processing. In addition, this cache size should not be 125 billion entries but lot smaller.

Let us look the analyze source data for USA.  Data facts

  * For US, we have 49000 records (one per zip).
  * Total US area is 9.8 million square Kms. Typical 95 percentile distance between any two adjacent zip is more than 8 KMs.
  * Latitude for US ranges from 24 to 50.  Longitude for US ranges from -124 to -65.
  * A 0.01 difference in latitude is around 1 KM and a 0.01 difference in longitude is around 1.5 km.
  * Given this, the percentage of points among all possible lat/lon in US that may spill to adjacent zip code could be less than 0.5%. Given the usage context, using 2 decimal precisions should be acceptable for most web sites and applications.
  * With 2 decimal precisions being good enough, we can determine the worst-case count of lat/lon values for US contiguous states.
  * Two decimal precisions result in 15 million (2600 times 6000) possible lat/long values. Nearest zip can be calculated for 15 million lat/lon through one time pre processing.


## **Cache Data storage and optimization**


The process of generating the cache involves the following
  * For each possible 2 decimal precisions and radius boundary, request for nearest zip code. With 2 decimal precisions, we will have around 15 million points for which we will fine nearest zip code.
  * Given the vast area of US, with 20KM radius, only 7 million lat/lon have nearest city/zip. 
  * Radius boundary can start from 10KM and if nearest zip is not found, then we can progressively increase radius (say 20KM, 30KM, 40KM) until nearest zip is found. 
  * With only 49000 points available in USA data source, average of 250 lat/lon points in cache will have the same zip code. 
  * When you extend this cache for all countries in the world, it would seem necessary to reduce cache size.
  * If we have duplicate points that have the same nearest zip code, store only one record instead of duplicates. 
  * Few techniques can be used to reduce the cache size. One simple option will be storing one record in cache instead of 10 records for 10 successive lat or lon points with the same nearest zip. For example for a given lat and lon values from -89.16 till -89.24, if the nearest zip codes are same, then we need to store only lat and 89.20. In the fast look up implementation, for a user request with say -89.19, both values for both -89.19 and -89.20 will be fetched. If -89.19 is not found in cache, value for -89.20 will be used.

## **Implementation pseudo code**
Pseudo code for cache generation

  * For each country/region determine the lat/lon boundary. Ex – for US boundaries are Lat range 24 to 49 and -122 to -69.
    * For each 2 decimal precisions lat/lon
      * Set radius = 10Km
      * While (nearest zip code not found AND radius < 50KM)
        * Find nearest zip code
        * If not found , set radius = radius + 10

In above steps, aggregation of lat/lon with same zip codes values can be performed and resulting in reduced cache entries. Format of the cache entry record. There will be 2 fields as (k,v) pair. LatLon will be stored as key with Lat in 16 bit MSB and Lon in 16 bit LSB. Both lat and lat will be stored with 100 times its values as integer. Since 16 bit integer can hold -32768 to +32768, full range lat and as well as lon values (-18000 to +18000) can be stored. The value v in (k,v) holds the seq Id for zip data.

Pseudo code for zip lookup for user request

This is typically executed in web service.

On server startup

  1. GeoLookupCache.csv into HashMap<Integer, Integer>. This holds (k,v) with Lat/Lon as key and seqId as value.
  2. Load USZipWithIndex.csv as HashMap<Integer, NearestPoint>. This holds seqId as key and nearestPoint as value.

User request processing

  1. Receive user request with lat/lon
  2. Convert lat/lon to 2 decimal precisions and convert them to 100 times their values.
  3. Form the key in 32 bit – (lat << 16) | lon  
    a. Note :  lat is stored in 16 bit MSBB and Lon in 16 bit LSB. Shift Lat left 16 times and bit OR with Lon.
  4. Look up in GeoLookupCache to retrieve SeqId.
  5. Look up in USZipIndex to retrieve NearestPoint.

## **Reference implementation for Fast geo lookup for USA**


Currently FastGeoLookup is implemented only for US. This is preliminary implementation. Provided only as reference implementation and is not tested.

https://github.com/Vish-Ram/GeoLookup
 

### **Set up**

  1. Download file from http://download.geonames.org/export/zip/US.zip
  2. Download and install elastic search from http://www.elasticsearch.org/download/.
  3. Start elasticsearch server
  4. Run mapings.sh to map the document in elastic search.

### **One time pre processing (Zip code data ingestion and Cache generation)**

  1. ZipCodeIngester.java : Generate elastic search ingestion script file DataIngestor.sh and new USZipCodeWithIndex.csv. Ingestion file will contain 49000 records each representing the curl command to be used to ingest the lat/long/city/zip data in elastic search. USWithIndex.csv is same as the source file with an additional seq id for each record. https://github.com/Vish-Ram/GeoLookup/tree/master/DataPostGenerator
    a. Input : USLatLongCityZip.csv (Data source downloaded from http://download.geonames.org/export/zip/US.zip)
    b. Output : DataIngestor.sh, USZipCodeWithIndex.csv
  2. Run DataIngestor.sh to zip code data populate data in ES.
  3. FastGeoLookupcache.java : Generates cache 7 million HashMap data that contains lat/lon as key and seqId as value. Store the 7 million processed records in GeoLookupCache file. https://github.com/Vish-Ram/GeoLookup/tree/master/GeoLookupCache

### **Web service fast look up app**


The cache file can be loaded into simple HashMap. Alternatives such as MemCache services can be used.

#### **Service initialization**


Use the cache file generated from FastGeoLookupcache.java.  Load it in HashMap memory or any other im-memory DB such CouchBase memcache on service start up.

#### **On user request**

  1. Receive user request with lat/lon
  2. Convert lat/lon to 2 decimal precisions and convert them to 100 times their values.
  3. Form the key in 32 bit – (lat << 16) | lon  
    a. Note :  lat is stored in 16 bit MSBB and Lon in 16 bit LSB. Shift Lat left 16 times and bit OR with Lon.
  4. Look up in GeoLookupCache to retrieve SeqId.
  5. Look up in USZipIndex to retrieve NearestPoint.

https://github.com/Vish-Ram/GeoLookup/tree/master/FastGeoLookup/src/com/vish/fastgeolookup


License Information

                    GNU AFFERO GENERAL PUBLIC LICENSE                        
                    Version 3, 19 November 2007   
                    Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>  
Everyone is permitted to copy and distribute verbatim copies  of this license document, but changing it is not allowed.                              Preamble    The GNU Affero General Public License is a free, copyleft license for software and other kinds of works, specifically designed to ensure cooperation with the community in the case of network server software.    The licenses for most software and other practical works are designed to take away your freedom to share and change the works.  By contrast, our General Public Licenses are intended to guarantee your freedom to share and change all versions of a program--to make sure it remains free software for all its users.    When we speak of free software, we are referring to freedom, not price.  Our General Public Licenses are designed to make sure that you have the freedom to distribute copies of free software (and charge for them if you wish), that you receive source code or can get it if you want it, that you can change the software or use pieces of it in new free programs, and that you know you can do these things.    Developers that use our General Public Licenses protect your rights with two steps: (1) assert copyright on the software, and (2) offer you this License which gives you legal permission to copy, distribute and/or modify the software.    A secondary benefit of defending all users' freedom is that improvements made in alternate versions of the program, if they receive widespread use, become available for other developers to incorporate.  Many developers of free software are heartened and encouraged by the resulting cooperation.  However, in the case of software used on network servers, this result may fail to come about. The GNU General Public License permits making a modified version and letting the public access it on a server without ever releasing its source code to the public.    The GNU Affero General Public License is designed specifically to ensure that, in such cases, the modified source code becomes available to the community.  It requires the operator of a network server to provide the source code of the modified version running there to the users of that server.  Therefore, public use of a modified version, on a publicly accessible server, gives the public access to the source code of the modified version.    An older license, called the Affero General Public License and published by Affero, was designed to accomplish similar goals.  This is a different license, not a version of the Affero GPL, but Affero has released a new version of the Affero GPL which permits relicensing under this license.
