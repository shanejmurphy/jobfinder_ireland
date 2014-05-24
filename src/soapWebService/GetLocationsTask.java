package soapWebService;

import global.AppPool;
import helpers.LogHelper;
import ie.jobfinder.app.JobRequirementsFragment;
import ie.jobfinder.app.interfaces.OnLocationResultListener;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import job.JobLocation;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.os.AsyncTask;

public class GetLocationsTask extends AsyncTask<Void, Void, ArrayList<JobLocation>>{
	private static final String TAG = "GetLocationsTask";
	private static final String OPERATION_NAME = "GetJobRegions"; 
	
	private OnLocationResultListener listener;
	
	public GetLocationsTask(JobRequirementsFragment jr){
		this.listener = jr;
	}
	
	@Override
	protected ArrayList<JobLocation> doInBackground(Void... params) {
		//define a list of JobCategories to be returned
		ArrayList<JobLocation> regions = new ArrayList<JobLocation>();
		
		//create the request using String params
		SoapObject request = new SoapObject(AppPool.WSDL_TARGET_NAMESPACE, OPERATION_NAME);
        
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        envelope.dotNet = true;       
        envelope.setOutputSoapObject(request);        
        HttpTransportSE httpTransport = new HttpTransportSE(AppPool.SOAP_ADDRESS);
         
        try {  
        	//decalre local vars
        	int id = 0;
        	String name = "Any";
        	
        	//Add initial value to list to allow users to search for jobs in any location
        	JobLocation loc = new JobLocation(id, name);
        	regions.add(loc);
        	
        	//get the soap response
	        httpTransport.call(AppPool.WSDL_TARGET_NAMESPACE + OPERATION_NAME, envelope);        
	        SoapObject response = (SoapObject)envelope.getResponse();
	        
	        //how many results?
	        int regionCount = response.getPropertyCount();
	        //LogHelper.logDebug(TAG, "Job Location Count = " + regionCount);

	        for(int i=0; i<regionCount; i++){
	        	//set each property to an instance of Object
	        	Object property = response.getProperty(i);
	            if (property instanceof SoapObject)
	            {
	            	//parse the Object
	                SoapObject region = (SoapObject) property;
	                id = Integer.parseInt(region.getProperty("Id").toString());
	                name = region.getProperty("Name").toString();

	                //create new JobCategory Instance with the parsed id and name and add to list
	                loc = new JobLocation(id, name);
	        		regions.add(loc);
	        		
	        		//LogHelper.logDebug(TAG, "Location ID: " + id + ", Location Name: " + name);
	        	}
	        }    
        }    
        catch(SocketTimeoutException se) {
        	LogHelper.logDebug(TAG, se.toString());
        	return regions;
        }
        catch (Exception exception) {
        	LogHelper.logDebug(TAG, exception.toString());
        	return regions;
        }
        
		return regions;
	}
	
	@Override
	protected void onPostExecute(ArrayList<JobLocation> locations){
		this.listener.onLocationResult(locations);
	}

}
