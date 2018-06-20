/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package web_service;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import models.NetCDFFile;
import ucar.ma2.InvalidRangeException;

/**
 * REST Web Service
 *
 * @author ixchel
 */
@Stateless
@Path("rosadeviento")
public class MainResource {
	
	@Context
	private UriInfo context;
	
	/**
	 * Creates a new instance of GenericResource
	 */
	public MainResource() {
	}
	
	/**
	 * Retrieves representation of an instance of paquete_service.GenericResource
	 * @return an instance of java.lang.String
	 */
	@GET
	@Path("/{folder}/{filename}/{lat}/{lon}/{initdate}/{enddate}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getContOtres(@PathParam("folder") String folder, @PathParam("filename") String filename, @PathParam("lat") float lat, @PathParam("lon") float lon,@PathParam("initdate") String initdate, @PathParam("enddate") String enddate) {
        
        String jsonResult = "";
        try {
            /*Create connection to file*/
            /*declaring sentences we going to use*/
            NetCDFFile newnetcdf = new NetCDFFile("/home/ixchel/"+folder+"/"+filename,lat,lon,initdate,enddate);
            jsonResult = "{ \"result\" : { "+newnetcdf.getUVByDaterangeAndCoordinates()+" } }";
        } catch (IOException ex) {
            Logger.getLogger(MainResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidRangeException ex) {
            Logger.getLogger(MainResource.class.getName()).log(Level.SEVERE, null, ex);
        }
		
		DateFormat formatter ;
		Date datey ;
		formatter = new SimpleDateFormat("yyyy-MM-dd HH");
        /*parse dias as Int*/
        //int ndays = Integer.parseInt(dias);
		
		
		return jsonResult;
	}
	
}