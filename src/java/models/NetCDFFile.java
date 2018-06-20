/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import ucar.ma2.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author ixchel
 */
public class NetCDFFile {
    String path = null;
    NetcdfFile dataFile = null;
    float lat;
    float lon;
    String sdate;
    String edate;
    
    public NetCDFFile(String filename, float rlat, float rlon, String rsdate, String redate) throws IOException {
        //path = "/ServerData/OWGIS/Atlas/" + filename;
        path = filename;
        dataFile = NetcdfFile.open( path , null);
        lat = rlat;
        lon = rlon;
        sdate = rsdate;
        edate = redate;
    }
    
    public String getUVByDaterangeAndCoordinates() throws IOException, InvalidRangeException {
        // Get the latitude, longitude and time Variables.
        Variable latVar = dataFile.findVariable("Latitude");
        Variable lonVar = dataFile.findVariable("Longitude");
        Variable timeVar = dataFile.findVariable("time");
               
        // Get the lat/lon data from the file.
        ArrayFloat.D1 latArray;
        ArrayFloat.D1 lonArray;
        latArray = (ArrayFloat.D1) latVar.read();
        lonArray = (ArrayFloat.D1) lonVar.read();

        // Get the time array and time units
        //String timedt = timeVar.getDataType().toString();
        Array timeArray;
        timeArray = timeVar.read();
        
        String timeunits = timeVar.getUnitsString();
        
        int latindex = -1;
        int lonindex = -1;
        int stimeindex = -1;
        int etimeindex = -1;
        
        int[] shapeTime = timeVar.getShape();
        int timeLen = shapeTime[0];
        
        int[] shapeLat = latVar.getShape();
        int latLen = shapeLat[0];
        
        int[] shapeLon = lonVar.getShape();
        int lonLen = shapeLon[0];
        
        float distancelat = Math.abs( latArray.get(0) - lat);
        for(int c = 1; c < latLen; c++){
            float cdistance = Math.abs(latArray.get(c) - lat);
            if(cdistance < distancelat){
                latindex = c;
                distancelat = cdistance;
            }
        }
        
        float distancelon = Math.abs( lonArray.get(0) - lon);
        for(int c = 1; c < lonLen; c++){
            float cdistance = Math.abs(lonArray.get(c) - lon);
            if(cdistance < distancelon){
                lonindex = c;
                distancelon = cdistance;
            }
        }
        
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy HH:mm");
        DateTime dt1 = formatter.parseDateTime(sdate);
        DateTime dt2 = formatter.parseDateTime(edate);
        int dayOfYearS = -1;
        int dayOfYearE = -1;
        
        if(timeunits.contains("days")){
            dayOfYearS = dt1.getDayOfYear()-1;
            dayOfYearE = dt2.getDayOfYear()-1;
        } else if(timeunits.contains("hours")){
            dayOfYearS = dt1.getHourOfDay();
            dayOfYearE = dt2.getHourOfDay();
        }
        
        for (int timeindx = 0; timeindx < timeLen; timeindx++){
            if ((int) timeArray.getDouble(timeindx) == dayOfYearS ){
                //we found the time index
                stimeindex = timeindx;
            } else if ((int) timeArray.getDouble(timeindx) == dayOfYearE){
                etimeindex = timeindx;
            }
        }
        
        String result = null;
        // Get the U10 and V10 variables
        Variable uVar = dataFile.findVariable("U10");
        Variable vVar = dataFile.findVariable("V10");
        
        if(latindex != -1 && lonindex != -1 ){
            int[] origin = new int[] {stimeindex, latindex, lonindex};
            int timeSize = etimeindex-stimeindex;
            int[] size = new int[] {timeSize, 1, 1};
            Array data3DU = uVar.read(origin, size);
            Array data3DV = vVar.read(origin, size);
            Array data1DU = data3DU.reduce();
            Array data1DV = data3DV.reduce(); 

            result = " \"U\" : [ "+data1DU.toString().trim().replaceAll(" ", ", ")+" ], \"V\" : [ "+data1DV.toString().trim().replaceAll(" ", ", ")+" ] ";
        } else {
            result = "Lat and/or Lon dont exist";
        }
        
        return result;
    }
    
    
}
