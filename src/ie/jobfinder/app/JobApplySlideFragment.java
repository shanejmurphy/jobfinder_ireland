package ie.jobfinder.app;

import global.AppPool;
import helpers.LogHelper;
import ie.jobfinder.app.job_application.JobApplication;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import job.Job;
import soapWebService.SendApplicationTask;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class JobApplySlideFragment extends SherlockDialogFragment {
	private static final String TAG = "JobApplySlideFragment";
	private static final int BROWSE_FILE_RESULT_CODE = 1;
	
	private Job job;
	private EditText mCvFileName;
	
	private JobApplication mJobApplication;

	public static JobApplySlideFragment newInstance(Job j)
	{
		JobApplySlideFragment slideFrag = new JobApplySlideFragment();
	    Bundle bundle = new Bundle();
	    bundle.putParcelable(AppPool.JOB, j);
	    slideFrag.setArguments(bundle);
	    return slideFrag;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//initialize the list of jobs
		job = getArguments().getParcelable(AppPool.JOB);
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.layout_job_apply, container, false);
        
		//first off create a new instance of JobApplication
		mJobApplication = new JobApplication();
		mJobApplication.setJobId(job.getId());
        
		final EditText coverNote = (EditText) rootView.findViewById(R.id.apply_job_cover_note);
		final EditText name = (EditText) rootView.findViewById(R.id.apply_job_name);
		name.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)
                {
					LogHelper.logDebug(TAG, "Keyboard Enter Event called");
					InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
						      Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(name.getWindowToken(), 0);
					return false;
                }
				return false;
			}
	    });
		final EditText email = (EditText) rootView.findViewById(R.id.apply_job_email);
		email.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)
                {
					LogHelper.logDebug(TAG, "Keyboard Enter Event called");
					InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
						      Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(email.getWindowToken(), 0);
					return false;
                }
				return false;
			}
	    });
		final EditText phone = (EditText) rootView.findViewById(R.id.apply_job_phone);
		phone.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)
                {
					LogHelper.logDebug(TAG, "Keyboard Enter Event called");
					InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
						      Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(phone.getWindowToken(), 0);
					return false;
                }
				return false;
			}
	    });
		
		mCvFileName = (EditText) rootView.findViewById(R.id.apply_job_cv);
		Button browseForCv = (Button) rootView.findViewById(R.id.apply_job_cv_btn);
		browseForCv.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				// Browse for CV file
	    		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
	    		intent.setType("file/*");
	    		startActivityForResult(intent, BROWSE_FILE_RESULT_CODE);
			}
		});
		Button apply = (Button) rootView.findViewById(R.id.apply_job_apply_btn);
		apply.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String lCoverNote = coverNote.getText().toString();
				String lName = name.getText().toString();
				String lEmail = email.getText().toString();
				String lPhone = phone.getText().toString();
				applyForJob(lCoverNote, lName, lEmail, lPhone);
			}
		});
		
        return rootView;
    }
    
    public void applyForJob(String coverNote, String name, String email, String phone) {
    	this.mJobApplication.setCoverNote(coverNote);
    	this.mJobApplication.setName(name);
    	this.mJobApplication.setEmail(email);
    	this.mJobApplication.setPhone(phone);
    	
      	// TODO do checks to ensure parameter data is valid 
    	
    	// TODO write soap service to post data to jobs.ie
    	new SendApplicationTask(this).execute(mJobApplication);
    	
    }
    
    
    //method to convert an inputstream to byte array
    //should this be done in asyncTask?
    /*public byte[] convertInputStreamToByteArray(InputStream inputStream)
    {
    	byte[] bytes= null;

    	try
    	{
    		ByteArrayOutputStream bos = new ByteArrayOutputStream();

    		byte data[] = new byte[1024];
    		int count;

    		while ((count = inputStream.read(data)) != -1)
    		{
    			bos.write(data, 0, count);
    		}

    		bos.flush();
    		bos.close();
    		inputStream.close();

    		bytes = bos.toByteArray();
    	}
    	catch (IOException e)
    	{
    		e.printStackTrace();
    	}
    	return bytes;
    }*/
    
    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is, "UTF-8").useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	// TODO find out why resultCode is always -1
    	LogHelper.logDebug(TAG, "RequestCode = " + requestCode + "; ResultCode = " + resultCode);
    	switch(requestCode) {
	    	case BROWSE_FILE_RESULT_CODE: {
	    		LogHelper.logDebug(TAG, "ResultCode = " + resultCode);
	    		if(resultCode != Activity.RESULT_CANCELED) {
	    			Uri uri = data.getData();
	    			LogHelper.logDebug(TAG, "URI = "+ uri);
	    			
	    			String filePath = uri.getPath();
	    			String filename = uri.getLastPathSegment();
	    			String mimeType = getActivity().getContentResolver().getType(uri);
	    			String extension = null;
	    			if(mimeType != null) {
	    				extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
	    				//set the application file type
	    				mJobApplication.setCvFileType(extension);
	    			}
	    			
	    			LogHelper.logDebug(TAG, "Chosen path = "+ filePath);
	    			LogHelper.logDebug(TAG, "Chosen file = "+ filename);   
	    			LogHelper.logDebug(TAG, "mimeType = "+ mimeType);
	    			LogHelper.logDebug(TAG, "FileType = "+ extension);
	    			
	    			//set the text on screen
	                mCvFileName.setHint(filename);
	                
	                //set the application file name
	                mJobApplication.setCvFileName(filename);
	                
	                //open the file for reading
	                String scheme = uri.getScheme();
	                InputStream in = null;
	                try {
		                if(scheme.equals("content")) {
		                	//use content resolver
		                	in = getActivity().getContentResolver().openInputStream(uri);
		                	LogHelper.logDebug(TAG, "Scheme = URi");
		                }
		                else if(scheme.equals("file")) {
		                	//use normal file methods
		                	in = new FileInputStream(filePath);
		                	LogHelper.logDebug(TAG, "Scheme = File");
		                }
		                
		                
		                /*
		                 * temp code to make sure i can read all file tpyes
		                 */	                		             
		                byte[] buffer = new byte[16384];
		                ByteArrayOutputStream baos = new ByteArrayOutputStream();

		                int bytesRead;
		                while ((bytesRead = in.read(buffer, 0, buffer.length)) != -1)
		                {
		                    baos.write(buffer, 0, bytesRead);
		                }
		                
		                LogHelper.logDebug(TAG, "Buffer.length = " + buffer.length);
		                
		                //String path = getActivity().getExternalCacheDir();// + File.separator + "temptest.doc";
		                //LogHelper.logDebug(TAG, "Output path = " + path);
		                
		                //below is the different part
		                //boolean writable = false;
	                    String state = Environment.getExternalStorageState();
	                    if (Environment.MEDIA_MOUNTED.equals(state)) {
	                    	//writable = true;
	         
	                    }
	                    else {
	                    	//writable = false;
	                    	LogHelper.logDebug(TAG, "Not able to write to external storage");
	                    }
		                
		                File dir = new File(getActivity().getExternalCacheDir().toString());//path);
		                String fname = filename;
		                File f = new File(dir+File.separator+fname);
		                /*if (!file.mkdirs()) {
		                	LogHelper.logDebug(TAG, "Directory not created");
		                }*/
		                if (!f.exists()) {
		                  f.createNewFile();
		                  LogHelper.logDebug(TAG, "File Created");
		                }
		                
		                FileOutputStream fos = new FileOutputStream(f);
		                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
		                
		                
		                //convert the inputstream to byte array and encode to Base64
		                byte[] cvByteData = Base64.encode(baos.toByteArray(), Base64.DEFAULT);
		              
		                //set the data in the application instance
		                this.mJobApplication.setCvFileData(cvByteData);
		                //LogHelper.logDebug(TAG, "Buffer length = " + cvByteData.length);

		                bos.write(Base64.decode(cvByteData, Base64.DEFAULT));
		                LogHelper.logDebug(TAG, "file written to " + getActivity().getExternalCacheDir());
		                bos.flush();
		                baos.flush();
		                bos.close();
		                fos.flush();
		                fos.close();
		                   
	                }
	                catch(FileNotFoundException e){
	                	LogHelper.logDebug(TAG, "File Not Found " + e.toString());
	                } catch(IllegalArgumentException e){
	                	LogHelper.logDebug(TAG, "Illegal Argument Exception in decode() " + e.toString());
	                } catch (IOException e) {
						// TODO Auto-generated catch block
					}
	    		}
	    		break;
	    	}
    	}
    }
}
