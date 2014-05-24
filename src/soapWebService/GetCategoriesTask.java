package soapWebService;

import global.AppPool;
import helpers.LogHelper;
import ie.jobfinder.app.JobRequirementsFragment;
import ie.jobfinder.app.interfaces.OnCategoriesResultListener;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import job.JobCategory;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.os.AsyncTask;

public class GetCategoriesTask extends AsyncTask<Void, Void, ArrayList<JobCategory>>{
	private static final String TAG = "GetCategoriesTask";
	private static final String OPERATION_NAME = "GetJobCategories"; 
	
	private OnCategoriesResultListener listener;
	
	public GetCategoriesTask(JobRequirementsFragment jr){
		//context = c;
		this.listener = jr;
		//dialog = new ProgressDialog(j.getActivity());
	}
	
	@Override
	protected ArrayList<JobCategory> doInBackground(Void... params) {
		//define a list of JobCategories to be returned
		ArrayList<JobCategory> categories = new ArrayList<JobCategory>();
		
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
        	
        	//add initial vlaues to allow users to search for jobs in any industry
        	JobCategory job = new JobCategory(id, name);
        	categories.add(job);
        	
        	
        	//get the soap response
	        httpTransport.call(AppPool.WSDL_TARGET_NAMESPACE + OPERATION_NAME, envelope);        
	        SoapObject response = (SoapObject)envelope.getResponse();
	        
	        //how many results?
	        int categoryCount = response.getPropertyCount();
	        //LogHelper.logDebug(TAG, "Job Category Count = " + categoryCount);

	        for(int i=0; i<categoryCount; i++){
	        	//set each property to an instance of Object
	        	Object property = response.getProperty(i);
	            if (property instanceof SoapObject)
	            {
	            	//parse the Object
	                SoapObject category = (SoapObject) property;
	                id = Integer.parseInt(category.getProperty("Id").toString());
	                name = category.getProperty("Name").toString();

	                //create new JobCategory Instance with the parsed id and name and add to list
	        		job = new JobCategory(id, name);
	        		categories.add(job);
	        		
	        		//LogHelper.logDebug(TAG, "Category ID: " + id + ", Category Name: " + name);
	        	}
	        }    
        }    
        catch(SocketTimeoutException se) {
        	LogHelper.logDebug(TAG, se.toString());
        	return categories;
        }
        catch (Exception exception) {
        	LogHelper.logDebug(TAG, exception.toString());
        	return categories;
        }
        
		return categories;
	}
	
	@Override
	protected void onPostExecute(ArrayList<JobCategory> categories) {
		this.listener.onCategoriesResult(categories);
	}
}
