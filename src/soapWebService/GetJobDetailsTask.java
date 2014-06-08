package soapWebService;

import global.AppPool;
import helpers.LogHelper;
import ie.jobfinder.app.JobListFragment;
import ie.jobfinder.app.R;
import ie.jobfinder.app.interfaces.OnJobDetailsResultListener;

import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import job.Job;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;

public class GetJobDetailsTask extends AsyncTask<Integer, Void, Job> {
	private static final String TAG = "GetJobDetailsTask";
	private static final String OPERATION_NAME = "GetJobDisplay";
	
	private OnJobDetailsResultListener listener;
	
	private ProgressDialog dialog;
	private AlertDialog.Builder builder;
	private LayoutInflater li;
	/*private JobRequirementsFragment fragment; */
	
	
	public GetJobDetailsTask(JobListFragment j){
		//context = c;
		this.listener = j;
		dialog = new ProgressDialog(j.getActivity());
		
		//alert dialog for connectivity issues
		builder = new AlertDialog.Builder(j.getActivity());
    	li = (LayoutInflater) j.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
	}
	
	@Override
	protected void onPreExecute()
    {
		this.dialog.setMessage("Loading Job...");
		this.dialog.show();
    }
	
	
	@Override
	protected Job doInBackground(Integer... params) {
		Job job = new Job();
		//retrieve the job details from WebService
		int jobId = (Integer) params[0];
		
		//call Web service method using id as parameter
		/******************* CREATE SOAP REQUEST ******************************/
		//create the request using String params
		SoapObject request = new SoapObject(AppPool.WSDL_TARGET_NAMESPACE, OPERATION_NAME);
		
		request.addProperty("jobId", jobId);
        
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        envelope.dotNet = true;       
        envelope.setOutputSoapObject(request); 
        HttpTransportSE httpTransport = new HttpTransportSE(AppPool.SOAP_ADDRESS);
         
        try {  
        	//get the soap response
        	httpTransport.debug = true;
	        httpTransport.call(AppPool.WSDL_TARGET_NAMESPACE + OPERATION_NAME, envelope); 
	        //LogHelper.logDebug(TAG, httpTransport.requestDump);
	        //LogHelper.logDebug(TAG, httpTransport.responseDump);
	        
            // Get the SoapResult from the envelope body.
            SoapObject response = (SoapObject) envelope.getResponse(); //bodyIn; getResponse();
            
        	//parse the Object
            job.setId(Integer.parseInt(response.getPrimitivePropertySafelyAsString("Id")));
            job.setTitle(response.getPrimitivePropertySafelyAsString("Title"));
            job.setDescription(response.getPrimitivePropertySafelyAsString("HtmlDescription"));
            job.setLocation(response.getPrimitivePropertySafelyAsString("Location"));
            job.setSalary(response.getPrimitivePropertySafelyAsString("Payment"));
            //Date modification before adding it ot Job Object
            Date dateM = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(response.getPrimitivePropertySafelyAsString("ModifiedDT"));
            job.setDateModified(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(dateM));
            Date dateC = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(response.getPrimitivePropertySafelyAsString("CreatedDT"));
            job.setDatePosted(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(dateC));              
            job.setApplicationURL(response.getPrimitivePropertySafelyAsString("ApplicationURL"));
            job.setQuestion1(response.getPrimitivePropertySafelyAsString("Question1"));
            job.setQuestion2(response.getPrimitivePropertySafelyAsString("Question2"));
            job.setQuestion3(response.getPrimitivePropertySafelyAsString("Question3"));
            job.setHomePageURL(response.getPrimitivePropertySafelyAsString("HomepageURL"));
            job.setCompany(response.getPrimitivePropertySafelyAsString("Company"));
            job.setAddress(response.getPrimitivePropertySafelyAsString("Address"));
            job.setPhone(response.getPrimitivePropertySafelyAsString("Phone"));
            job.setFax(response.getPrimitivePropertySafelyAsString("Fax"));
            job.setEmail(response.getPrimitivePropertySafelyAsString("Email"));
            job.setContactName((response.getPrimitivePropertySafelyAsString("Contact")));
            job.setContactHidden(Boolean.parseBoolean(response.getPrimitivePropertySafelyAsString("IsContactHidden")));
            job.setCompanyNameHidden(Boolean.parseBoolean(response.getPrimitivePropertySafelyAsString("IsCompanyHidden")));
            job.setEmailHidden(Boolean.parseBoolean(response.getPrimitivePropertySafelyAsString("IsEmailHidden")));
            job.setPhoneHidden(Boolean.parseBoolean(response.getPrimitivePropertySafelyAsString("IsPhoneHidden")));
            job.setFaxHidden(Boolean.parseBoolean(response.getPrimitivePropertySafelyAsString("IsFaxHidden")));
            job.setAddressHidden(Boolean.parseBoolean(response.getPrimitivePropertySafelyAsString("IsAddressHidden")));
            job.setHomePageHidden(Boolean.parseBoolean(response.getPrimitivePropertySafelyAsString("IsHomePageHidden")));
    		
    		//LogHelper.logDebug(TAG, "jobId = " + job.getDatePosted()); 
        }    
/*        catch(SocketTimeoutException se) {
        	LogHelper.logDebug(TAG, se.toString());
        	//TODO: show dialog with retry information
	  		//hide the dialog
        	if(this.dialog.isShowing()) {
	  			this.dialog.dismiss();
	  		}
        	View help = li.inflate(R.layout.dialog_timeout, null);
            builder.setTitle(R.string.txt_timeout_title);
            builder.setView(help);
            builder.setCancelable(false);
            builder.show();
        }*/
        catch (Exception exception) {
        	LogHelper.logDebug(TAG, exception.toString());
        	return null;
        }
		
		return job;
	} 
	
	  @Override
      protected void onPostExecute(Job job) {		  
		  //hide the dialog
		  if(this.dialog.isShowing()) {
			  this.dialog.dismiss();
		  }
		  
		  //call the listerner
		  if(job != null) {
			  this.listener.doCallBack(job);
		  }
		  else {
			  View help = li.inflate(R.layout.dialog_timeout, null);
			  builder.setTitle(R.string.txt_timeout_title);
			  builder.setView(help);
			  builder.setCancelable(true);
			  builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                //do things
		        	   dialog.cancel();
		           }
		      });
			  builder.show();
		  }
	  }
}
