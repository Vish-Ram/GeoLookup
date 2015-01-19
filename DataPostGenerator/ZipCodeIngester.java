package com.vish.GeoSHWriter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;
 

public class SHWriter {
	public static void main(String[] args) {
		BufferedReader br = null;
		String indexName = "geo_data";
		String typeName = "area";
		try {
			String currentLine = "";
			br = new BufferedReader(new FileReader("/Users/vish/Desktop/US.txt"));
			currentLine = br.readLine();
			System.out.println(currentLine);
			currentLine = currentLine.replaceAll("\t+", "\t");
			System.out.println(currentLine);
			try {
				int index = 0;
				FileWriter writer = new FileWriter("/Users/vish/projects/geoLatLon/geoProj/US.sh");
				FileWriter IndexWriter = new FileWriter("/Users/vish/projects/geoLatLon/geoProj/USIndex.csv");
				String[] line_split = currentLine.split("\\t");
				String zipcode = line_split[1];
				String city = line_split[2].replaceAll("'", " ");
				String state = line_split[3];
				String lat = line_split[6];
				String lon = line_split[7];
				
				for (int i = 0; i < line_split.length; i++) {
					System.out.println(i + ": " + line_split[i]);
				}
				writer.append("curl -XPOST http://localhost:9200/" + indexName + "/" + typeName + "/ -d ");
				writer.append("'{\"index\": \""+index+"\", \"zipcode\": \""+zipcode+"\", \"city\": \""+city+"\", \"state\": \""+state+"\", \"location\": {\"lat\": \""+lat+"\", \"lon\": \""+lon+"\"}}'");
				writer.append("\n");
				IndexWriter.append(index + "," + zipcode + "," + city + "," + state + "," + lat + "," + lon);
				IndexWriter.append("\n");
				index++;
				while ((currentLine = br.readLine()) != null) {
					currentLine = currentLine.replaceAll("\t+", "\t");
					if (index < 3) {
						System.out.println("Line: " + currentLine);
					}
					line_split = currentLine.split("\\t");
					if (line_split.length < 9) {
						System.out.println(currentLine);
						continue;
					}
					
					zipcode = line_split[1];
					city = line_split[2].replaceAll("'", " ");
					state = line_split[3];
					lat = line_split[7];
					lon = line_split[8];
					if (zipcode.equals("99644") || zipcode.equals("99697")) {
						for (int i = 0; i < line_split.length; i++) {
							System.out.println(i + ": " + line_split[i]);
						}
					}
					writer.append("curl -XPOST http://localhost:9200/" + indexName + "/" + typeName + "/ -d ");
					writer.append("'{\"index\": \""+index+"\", \"zipcode\": \""+zipcode+"\", \"city\": \""+city+"\", \"state\": \""+state+"\", \"location\": {\"lat\": \""+lat+"\", \"lon\": \""+lon+"\"}}'");
					writer.append("\n");
					IndexWriter.append(index + "," + zipcode + "," + city + "," + state + "," + lat + "," + lon);
					IndexWriter.append("\n");
					index++;
					
				}
				System.out.println("Valid lines: " + index);
				writer.flush();
				writer.close();
				IndexWriter.flush();
				IndexWriter.close();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
