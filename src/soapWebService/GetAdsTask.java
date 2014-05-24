package soapWebService;

import global.AppPool;
import helpers.LogHelper;
import ie.jobfinder.app.JobRequirementsFragment;
import ie.jobfinder.app.R;
import ie.jobfinder.app.interfaces.OnJobAdResultListener;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import ad.JobAd;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;

public class GetAdsTask extends AsyncTask<Object, Void, ArrayList<JobAd>> {
	private static final String TAG = "GetAdsTask";
	private static final String OPERATION_NAME = "SearchJobs"; 
	
	private OnJobAdResultListener listener;
	
	private ProgressDialog dialog;
	private AlertDialog.Builder builder;
	private LayoutInflater li;
	
	
	public GetAdsTask(JobRequirementsFragment j){
		this.listener = j;
		dialog = new ProgressDialog(j.getActivity());
		
		//alert dialog for connectivity issues
		builder = new AlertDialog.Builder(j.getActivity());
    	li = (LayoutInflater) j.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
	}
	
	@Override
    protected void onPreExecute()
    {
		this.dialog.setMessage("Please Wait...");
		this.dialog.show();
    }
	
	/**
	 * @params
	 * [0]Categories: int; [1]Locations: int;
	 * [2]JobType:String; [3]DatePosted:String; [4]keywords:String
	 */
	@Override
	protected ArrayList<JobAd> doInBackground(Object... params) {
		//define the params that are returned
		int category = (Integer) params[0];
		int loc = (Integer) params[1];
		String jobType = (String) params[2];
		int datePosted = (Integer) params[3];
		String keywords = (String) params[4];
		
		//define a list of JobCategories to be returned
		ArrayList<JobAd> ads = new ArrayList<JobAd>();		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
		Calendar cal = Calendar.getInstance(Locale.ENGLISH);
		Date fromDate = null;
		Date toDate = null;
		try {
			toDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH).parse(df.format(cal.getTime())); //current time
			//now subtract time from cal object for fromdate
			fromDate = getDate(cal, datePosted);	
		}catch(Exception e){
			LogHelper.logDebug(TAG, "Date parsing Exception");
		}
		
		/******************* CREATE SOAP REQUEST ******************************/
		//create the request using String params
		SoapObject request = new SoapObject(AppPool.WSDL_TARGET_NAMESPACE, OPERATION_NAME);
		
		//set up jobCategoryIds complex type to pass in as parameter to request
		JobCategoryIds jobCategoryIds = new JobCategoryIds();
		PropertyInfo jobCategoryIdsPropertyInfo = new PropertyInfo();
		//check to see if there was nay value passed for job category/industry
		if(category > 0){
			jobCategoryIds.add(category);
			jobCategoryIdsPropertyInfo.setName("jobCategoryIds");
			jobCategoryIdsPropertyInfo.setValue(jobCategoryIds);
			jobCategoryIdsPropertyInfo.setType(jobCategoryIds.getClass());
			request.addProperty(jobCategoryIdsPropertyInfo);
		}
		else {
			//do nothing
		}
		
		//set up jobLocationIds complex type to pass in as parameter to request
		JobLocationIds jobLocationIds = new JobLocationIds();
		PropertyInfo jobLocationIdsPropertyInfo = new PropertyInfo();
		//check to see if there was nay value passed for job category/industry
		if(loc > 0){
			jobLocationIds.add(loc);
			jobLocationIdsPropertyInfo.setName("regionIds");
			jobLocationIdsPropertyInfo.setValue(jobLocationIds);
			jobLocationIdsPropertyInfo.setType(jobLocationIds.getClass());
			request.addProperty(jobLocationIdsPropertyInfo);
		}
		else {
			//do nothing
		}
		
		request.addProperty("excludeAgencies", false);
		request.addProperty("jobType", jobType);
		request.addProperty("jobHours", "Any");
		request.addProperty("keywords", keywords);
		request.addProperty("fromDate", getSOAPDateString((java.util.Date) fromDate));
		request.addProperty("toDate", getSOAPDateString((java.util.Date) toDate));
		request.addProperty("startRecord", 0);
		request.addProperty("pageSize", 0);
        
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        envelope.dotNet = true;       
        envelope.setOutputSoapObject(request); 
        envelope.addMapping(AppPool.WSDL_TARGET_NAMESPACE, "jobCategoryIds", new JobCategoryIds().getClass());
        HttpTransportSE httpTransport = new HttpTransportSE(AppPool.SOAP_ADDRESS);
         
        try {  
        	//decalre local vars
        	int id = 0;
        	String createdDT = null;
        	String title = null;
        	String company = null;
        	String location = null;
        	int employerId = 0;
        	
        	//get the soap response
        	//httpTransport.debug = true;
	        httpTransport.call(AppPool.WSDL_TARGET_NAMESPACE + OPERATION_NAME, envelope); 
	        //LogHelper.logDebug(TAG, httpTransport.requestDump);
	        //LogHelper.logDebug(TAG, httpTransport.responseDump);
	        
            // Get the SoapResult from the envelope body.
            SoapObject response = (SoapObject) envelope.bodyIn;
            SoapObject root = (SoapObject) response.getProperty(0);
	        
	        //how many results?
	        int adCount = root.getPropertyCount();
	        LogHelper.logDebug(TAG, "Ad Count = " + adCount);

	        for(int i=0; i<adCount; i++){
	        	//set each property to an instance of Object
	        	Object property = root.getProperty(i);
	            if (property instanceof SoapObject)
	            {
	                //create new JobCategory Instance with the parsed id and name and add to list
	        		JobAd ad = new JobAd();
	        		
	            	//parse the Object
	                SoapObject job = (SoapObject) property;
	                id = Integer.parseInt(job.getProperty("Id").toString());
	                createdDT = job.getProperty("CreatedDT").toString();
	                title = job.getProperty("Title").toString();
	                company = job.getProperty("Company").toString();
	                location = job.getProperty("Location").toString();
	                employerId = Integer.parseInt(job.getProperty("EmployerId").toString());
	        		
	        		ad.setJobId(id);
	        		//get date from date string
	        		Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(createdDT);
	        		ad.setDateCreated(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(date));
	        		ad.setTitle(title);
	        		ad.setCompany(company);
	        		ad.setLocation(location);
	        		ad.setEmployerId(employerId);
	        		
	        		//add the ad to the list
	        		ads.add(ad);
	        		//LogHelper.logDebug(TAG, ad.toString());
	        	}
	        }    
        }    
        catch (UnknownHostException e) {
        	LogHelper.logDebug(TAG, e.toString());
        	return null;
        }
        catch (SocketTimeoutException e) {
        	LogHelper.logDebug(TAG, e.toString());
        	return null;
        }
        catch (Exception exception) {
        	LogHelper.logDebug(TAG, exception.toString());
        	return null;
        }
        
		return ads;
	}
	
	  @Override
      protected void onPostExecute(ArrayList<JobAd> jobAds) {
		  //hide the dialog
		  if(this.dialog.isShowing()) {
			  this.dialog.dismiss();
		  }
		  
		  //call the listerner
		  if(jobAds != null) {
			  this.listener.doCallBack(jobAds);
		  }
		  else {
			  View help = li.inflate(R.layout.dialog_timeout, null);
			  builder.setTitle(R.string.txt_timeout_title);
			  builder.setView(help);
			  builder.setCancelable(false);
			  builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                //do things
		        	   dialog.cancel();
		           }
		      });
			  builder.show();
		  }
	  }
	
	//method to calculate the date given a date and a value passed from fragment
	private Date getDate(Calendar cal, int datePosted) { 
		/** datePosted
		 * 0. All
		 * 1. Yesterday
		 * 2. Last Week
		 * 3. Last 2 Weeks
		 * 4. Last 3. Weeks
		 * 5. Last Month
		 */
		Date d = null;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
		switch(datePosted) {
			case(0): {
				//All = 2 months
				cal.add(Calendar.MONTH, -2);
				break;
			}
			case(1): {
				cal.add(Calendar.DATE, -1);
				break;
			}
			case(2): {
				cal.add(Calendar.DATE, -7);
				break;
			}
			case(3): {
				cal.add(Calendar.DATE, -14);
				break;
			}
			case(4): {
				cal.add(Calendar.DATE, -21);
				break;
			}
			case(5): {
				cal.add(Calendar.DATE, -1);
				break;
			}
			default: {
				cal.add(Calendar.MONTH, -1);
				break;
			}
		}
		
		try {
			d = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH).parse(df.format(cal.getTime()));;
		}catch(Exception e) {
			LogHelper.logDebug(TAG, "Date Parse Exception in getDate()");
		}
		
		return d;
	}
	
	private static Object getSOAPDateString(java.util.Date itemValue) {
	    String lFormatTemplate = "yyyy-MM-dd'T'hh:mm:ss";
	    DateFormat lDateFormat = new SimpleDateFormat(lFormatTemplate, Locale.ENGLISH);
	    String lDate = lDateFormat.format(itemValue);
	    //LogHelper.logDebug(TAG, "ToDate = " + lDate);
	    return lDate;
	}
}
