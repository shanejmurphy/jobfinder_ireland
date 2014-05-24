package soapWebService;

import global.AppPool;
import helpers.LogHelper;
import ie.jobfinder.app.JobApplySlideFragment;
import ie.jobfinder.app.job_application.JobApplication;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.ProgressDialog;
import android.os.AsyncTask;

public class SendApplicationTask extends AsyncTask<JobApplication, Void, Void>{

	private static final String TAG = "SendApplicationTask";
	private static final String OPERATION_NAME = "AddApplication";
	
	private ProgressDialog dialog;
	private JobApplication mApplication;
		
	public SendApplicationTask(JobApplySlideFragment frag){
		dialog = new ProgressDialog(frag.getActivity());
	}
	
	@Override
    protected void onPreExecute()
    {
		this.dialog.setMessage("Please Wait...");
		this.dialog.show();
    }

	@Override
	protected Void doInBackground(JobApplication... params) {
		// TODO Auto-generated method stub
		mApplication = params[0];
		
		
		/******************* CREATE SOAP REQUEST ******************************/
		//create the request using String params
		SoapObject request = new SoapObject(AppPool.WSDL_TARGET_NAMESPACE, OPERATION_NAME);
	
		request.addProperty("CoverNote", this.mApplication.getCoverNote());
		request.addProperty("Name", this.mApplication.getName());
		request.addProperty("Phone", this.mApplication.getPhone());
		request.addProperty("Email", this.mApplication.getEmail());
		request.addProperty("JobId", this.mApplication.getJobId());
		request.addProperty("CVFileName", this.mApplication.getCvFileName());
		request.addProperty("CVFileType", this.mApplication.getCvFileType());
		request.addProperty("CVFileData", this.mApplication.getCvFileData());
        
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        new MarshalBase64().register(envelope);
        envelope.dotNet = true;       
        envelope.setOutputSoapObject(request); 
        HttpTransportSE httpTransport = new HttpTransportSE(AppPool.SOAP_ADDRESS);
         
        try {  
        	//decalre local vars
        	
        	//get the soap response
        	httpTransport.debug = true;
	        httpTransport.call(AppPool.WSDL_TARGET_NAMESPACE + OPERATION_NAME, envelope);       
        	LogHelper.logDebug(TAG, httpTransport.requestDump);	        
	        LogHelper.logDebug(TAG, httpTransport.responseDump);
	        
            // Get the SoapResult from the envelope body.
            SoapObject response = (SoapObject) envelope.bodyIn;
            SoapObject root = (SoapObject) response.getProperty(0);
        }    
        catch (Exception exception) {
        	LogHelper.logDebug(TAG, exception.toString());
        }
        
        /*********************** END SOAP REQUEST ******************************/
		return null;
	}
	
	@Override
	protected void onPostExecute(Void param) {
		// TODO Auto-generated method stub
		
	}
}
