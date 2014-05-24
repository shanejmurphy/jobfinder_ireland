package ie.jobfinder.app;

import global.AppPool;
import helpers.LogHelper;
import ie.jobfinder.app.interfaces.OnJobDetailsResultListener;

import java.util.ArrayList;

import job.Job;
import soapWebService.GetJobDetailsTask;
import ad.JobAd;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class JobListFragment extends SherlockDialogFragment implements OnJobDetailsResultListener{
	private static final String TAG = "JobListFragment";
	
	public static final String JOB_LIST = "JobList";
	
	private ArrayList<JobAd> mJobList;
	private OnJobSelectedListener mCallback;
	
	public static JobListFragment newInstance(ArrayList<JobAd> jobAdList)
	{
		JobListFragment jlFrag = new JobListFragment();
	    Bundle bundle = new Bundle();
	    bundle.putParcelableArrayList(JOB_LIST, jobAdList);
	    jlFrag.setArguments(bundle);
	    return jlFrag ;
	}
	
    // Container Activity must implement this interface
    public interface OnJobSelectedListener {
        public void onJobDetailsResult(Job j);
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
		//initialize the list of jobs
		mJobList = getArguments().getParcelableArrayList(JOB_LIST);
		
        //change actionbar title
        ((FindJobActivity) activity).getSupportActionBar().setTitle("\tSearch Results - " + mJobList.size() + (mJobList.size() == 1 ? " Job" : " Jobs"));
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnJobSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnJobSelectedListener");
        }
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		LogHelper.logDebug(TAG, "OnCreate()");
		
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		LogHelper.logDebug(TAG, "OnCreateView()");
		
		//inflate view
		View view = inflater.inflate(R.layout.fragment_job_list, container, false);
		  
        // The "loadAdOnCreate" and "testDevices" XML attributes no longer available.
        AdView adView = (AdView) view.findViewById(R.id.adView_job_list);
        AdRequest adRequest = new AdRequest.Builder()
            .addTestDevice(AppPool.TEST_DEVICE_ID)
            .build();
        adView.loadAd(adRequest);
        
        if(this.mJobList.size() > 0) {
    
        	//make sure no jobs text view is set to invisible
        	TextView noJobs = (TextView) view.findViewById(R.id.job_list_no_jobs_txt);
        	noJobs.setVisibility(View.INVISIBLE);
        	
	        //initialise Views from main
		    ListView jobListView = (ListView) view.findViewById(R.id.job_list);
		    
		    //set customised adapter for list
		    JobItemLayoutAdapter jobItemAdapter = new JobItemLayoutAdapter(getActivity(), R.layout.fragment_job_list, this.mJobList);
		    jobListView.setAdapter(jobItemAdapter);
		    //create a click listener for list items
		    jobListView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					LogHelper.logDebug(TAG, "Click Event Fired on Job list");
	                JobAd jobAd = (JobAd) parent.getItemAtPosition(position);
	                int jobId = jobAd.getJobId();
	                
	                //get the job details for this ID
	                try {
						new GetJobDetailsTask(JobListFragment.this).execute(jobId);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						LogHelper.logDebug(TAG, e.toString());
					}
				}
	        });
        }
        else { //no jobs were returned
        	TextView noJobs = (TextView) view.findViewById(R.id.job_list_no_jobs_txt);
        	noJobs.setVisibility(View.VISIBLE);
        }
	    
	    return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		((FindJobActivity) getActivity()).getSupportActionBar().setTitle("\tSearch Results - " + mJobList.size() + (mJobList.size() == 1 ? " Job" : " Jobs"));
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.fragment_search, menu); 
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
		    // action with ID action_refresh was selected
		    case R.id.action_search:
		    	getFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);            
		    	break;
		    default:
		    	break;
		}
	    return true;
	} 



	@Override
	public void doCallBack(Job j) {
		// TODO Auto-generated method stub
		mCallback.onJobDetailsResult(j);
	}
}
