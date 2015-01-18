package com.vish.fastgeolookup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;

public class FastGeoLookup {
	private static Map<Integer, Integer> geoIndexMap = new HashMap<Integer, Integer>();
	private static Map<Integer, NearestGeoPoint> geoZipMap = new HashMap<Integer, NearestGeoPoint>();
	public static void main(String[] args) {
		
		long heapSize = Runtime.getRuntime().totalMemory();

        // Get maximum size of heap in bytes. The heap cannot grow beyond this size.
        // Any attempt will result in an OutOfMemoryException.
        long heapMaxSize = Runtime.getRuntime().maxMemory();

        // Get amount of free memory within the heap in bytes. This size will increase
        // after garbage collection and decrease as new objects are created.
        long heapFreeSize = Runtime.getRuntime().freeMemory();
        
        System.out.println("\tInput data : heapSize : " + heapSize + ", heapMaxSize : " + heapMaxSize + ", heapFreeSize : " + heapFreeSize );
        
		// Get current size of heap in bytes
        String geoCacheFile = "/Users/vish/projects/geoLatLon/geoProj/CacheDir/GeoCache.csv";
        String geoZipFile = "/Users/vish/projects/GeoLatLon/geoProj/USIndex.csv";
        // Generate the map in memory
        File finIndex = new File(geoCacheFile);
        File finZip = new File(geoZipFile);
        
		try {
			readIndexFile(finIndex, geoIndexMap);
			System.out.println("Successfully read " + geoCacheFile);
			readZipFile(finZip, geoZipMap);
			System.out.println("Successfully read " + geoZipFile);
		} catch (IOException e) {
			System.out.println("Could not read file" + geoCacheFile);
		} catch(Exception e) {
			System.out.println("Could not read file " + geoZipFile);
		}
		
		heapSize = Runtime.getRuntime().totalMemory();

        // Get maximum size of heap in bytes. The heap cannot grow beyond this size.
        // Any attempt will result in an OutOfMemoryException.
        heapMaxSize = Runtime.getRuntime().maxMemory();

        // Get amount of free memory within the heap in bytes. This size will increase
        // after garbage collection and decrease as new objects are created.
        heapFreeSize = Runtime.getRuntime().freeMemory();
        
        System.out.println("\tInput data : heapSize : " + heapSize + ", heapMaxSize : " + heapMaxSize + ", heapFreeSize : " + heapFreeSize );
		
		// query for given lat, lon

		// case 1 = 37.69, -100.49
		double lat = 37.696;
		double lon = -100.498;

		long qryStartTime = new DateTime().getMillis();
		int iLat = (int) Math.round(lat * 100);
		int iLon = (int) Math.round(lon * 100);
		

		Integer key = (iLat << 16) + iLon;
		Integer index = geoIndexMap.get(key);
		
		NearestGeoPoint pt;
		if (index != null) {
			pt = geoZipMap.get(index);
			if (pt == null) {
				System.out.println("We may have a wrong index value / Serious error " + index);
				return;
			}
		} else {
			pt = null;
			return;
		}
		
		
		
		long qryEndTime = new DateTime().getMillis();
		System.out.println("\tInput data : iLat : " + iLat + ", iLon : " + iLon
				+ ", index : " + index);
		System.out.println("\tTime taken for to get value ( " + pt.toString() + " ) : "
				+ Long.valueOf(qryEndTime - qryStartTime) + "ms");
	}

	private static void readIndexFile(File fin, Map<Integer, Integer> mapData) throws IOException {
		FileInputStream fis = new FileInputStream(fin);
		
		int count = 0;
		
		System.out.println("Reading Index File");
		
		// Construct BufferedReader from InputStreamReader
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));

		long qryStartTime = new DateTime().getMillis();

		String line = null;
		while ((line = br.readLine()) != null) {
			String[] values = line.split(",");
			if (values != null && values.length == 2) {
				mapData.put(Integer.valueOf(values[0]),
						Integer.valueOf(values[1]));
				count++;
			}
			if (count % 10000 == 0) {
				System.out.println("Entries read: " + count);
			}
		}

		long qryEndTime = new DateTime().getMillis();
		System.out.println("\tTime taken for to generate map : "
				+ Long.valueOf(qryEndTime - qryStartTime) + "ms");
		br.close();
	}
	
	private static void readZipFile(File fin, Map<Integer, NearestGeoPoint> mapData) throws Exception {
		FileInputStream fis = new FileInputStream(fin);

		System.out.println("reading ZIP file");
		
		// Construct BufferedReader from InputStreamReader
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		
		long qryStartTime = new DateTime().getMillis();

		String line = null;
		while ((line = br.readLine()) != null) {
			String[] values = line.split(",");
			NearestGeoPoint pt = new NearestGeoPoint(new Location(values[4], values[5]), values[3], values[2], values[1]);
			if (values != null && values.length == 2) {
				mapData.put(Integer.valueOf(values[0]), pt);
			}
			// System.out.println(line);
		}

		long qryEndTime = new DateTime().getMillis();
		System.out.println("\tTime taken for to generate map : "
				+ Long.valueOf(qryEndTime - qryStartTime) + "ms");
		br.close();
	}
}
