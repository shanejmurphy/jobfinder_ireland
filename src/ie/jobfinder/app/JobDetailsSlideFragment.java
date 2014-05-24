package ie.jobfinder.app;

import global.AppPool;
import helpers.LogHelper;
import job.Job;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.actionbarsherlock.app.SherlockFragment;

public class JobDetailsSlideFragment extends SherlockFragment {
	private static final String TAG = "JobDetailsSlideFragment";
	
	private Job job;
	
	
	public static JobDetailsSlideFragment newInstance(Job j)
	{
		JobDetailsSlideFragment slideFrag = new JobDetailsSlideFragment();
	    Bundle bundle = new Bundle();
	    bundle.putParcelable(AppPool.JOB, j);
	    slideFrag.setArguments(bundle);
	    return slideFrag ;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		LogHelper.logDebug(TAG, "onCreate()");
		//initialize the list of jobs
		job = getArguments().getParcelable(AppPool.JOB);
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.layout_job_descripion, container, false);
        
		WebView htmlDescription = (WebView) rootView.findViewById(R.id.description_job_details);
		//edit font size of web view
        WebSettings settings = htmlDescription.getSettings();
        settings.setDefaultFontSize(12);
        
        htmlDescription.loadDataWithBaseURL(null, job.getDescription(), "text/html", "utf-8", null);

        return rootView;
    }
}
