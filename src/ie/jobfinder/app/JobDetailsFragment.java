package ie.jobfinder.app;

import global.AppPool;
import helpers.FileHelper;
import helpers.LogHelper;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import job.Job;
import soapWebService.GetSavedJobsTask;
import ad.JobAd;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class JobDetailsFragment extends SherlockDialogFragment {
	private static final String TAG = "JobDetailsFragment";	
	//private static final int NUM_PAGES = 2; //The number of pages in the viewpager

	private Job j;

	/**
	 * The pager adapter, which provides the pages to the view pager widget.
	 */
	private Button mApplyButton;

	public static JobDetailsFragment newInstance(Job j)
	{
		JobDetailsFragment detailFrag = new JobDetailsFragment();
		Bundle bundle = new Bundle();
		bundle.putParcelable(AppPool.JOB, j);
		detailFrag.setArguments(bundle);
		return detailFrag ;
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
		
		//initialize the list of jobs
		j = getArguments().getParcelable(AppPool.JOB);
		
        //change actionbar title
        ((FindJobActivity) activity).getSupportActionBar().setTitle("\t" + j.getTitle());
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
		//View view = inflater.inflate(R.layout.fragment_show_job, container, false);
		View view = inflater.inflate(R.layout.fragment_job_details, container, false);

		// The "loadAdOnCreate" and "testDevices" XML attributes no longer available.
		AdView adView = (AdView) view.findViewById(R.id.adView_job_details);
		AdRequest adRequest = new AdRequest.Builder()
		.addTestDevice(AppPool.TEST_DEVICE_ID)
		.build();
		adView.loadAd(adRequest);

		//decalare the layout items
		RelativeLayout details = (RelativeLayout) view.findViewById(R.id.lyt_details_job_details);
		RelativeLayout noJob = (RelativeLayout) view.findViewById(R.id.lyt_no_job_job_details);
		if(j.getTitle() != null && j.getTitle() != "") {
			noJob.setVisibility(View.INVISIBLE);
			details.setVisibility(View.VISIBLE);		
		}
		else {
			details.setVisibility(View.INVISIBLE);
			noJob.setVisibility(View.VISIBLE);
		}
		
		TextView title = (TextView) view.findViewById(R.id.title_text_job_details);  
		TextView company = (TextView) view.findViewById(R.id.company_name_job_details);
		TextView location = (TextView) view.findViewById(R.id.location_name_job_details);
		TextView posted = (TextView) view.findViewById(R.id.date_job_details);
		TextView salary = (TextView) view.findViewById(R.id.salary_job_details);

		// Instantiate a ViewPager and a PagerAdapter.
		/*final ViewPager mPager = (ViewPager) view.findViewById(R.id.pager_show_job);
		PagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
		mPager.setAdapter(pagerAdapter);
		mPager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageScrollStateChanged(int state) {
				//LogHelper.logDebug(TAG, "onPageScrollStateChanged " + state);
			}
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				//LogHelper.logDebug(TAG, "onPageScrolled " + position);
			}

			public void onPageSelected(int position) {
				LogHelper.logDebug(TAG, "onPageSelected " + position);
				switch(position){
				case 0: {
					LogHelper.logDebug(TAG, "Job Description Layout");
					mApplyButton.setText(R.string.apply);
					mApplyButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.apply_icon, 0, 0, 0);
					break;
				}
				case 1: {
					LogHelper.logDebug(TAG, "Job Application Layout");
					mApplyButton.setText(R.string.details);
					mApplyButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.details_icon, 0, 0, 0);
					break;
				}
				} 
			}
		});*/
		//TextView description = (TextView) view.findViewById(R.id.description_job_details);
		WebView htmlDescription = (WebView) view.findViewById(R.id.description_job_details);
		//edit font size of web view
        WebSettings settings = htmlDescription.getSettings();
        settings.setDefaultFontSize(12);

		//Butons
		Button shareButton = (Button) view.findViewById(R.id.share_show_job);
		shareButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//search for jobs given a set of search params
				Intent sendIntent = new Intent();
				sendIntent.setAction(Intent.ACTION_SEND);
				sendIntent.putExtra(Intent.EXTRA_SUBJECT, AppPool.EMAIL_SUBJECT);
				sendIntent.putExtra(Intent.EXTRA_TEXT, j.getJobsIeUrl() + AppPool.getAppInfoForShare());
				sendIntent.setType("text/plain");
				startActivity(sendIntent);
			}
		});

		Button saveButton = (Button) view.findViewById(R.id.save_show_job);
		saveButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ArrayList<JobAd> adList = null;
				boolean isAddable = true; //can we add the ad to the file
				//write the job to a file to save
				JobAd jobAd = new JobAd();
				jobAd.setJobId(j.getId());
				jobAd.setCompany(j.getCompany());
				jobAd.setDateCreated(j.getDateModified());
				jobAd.setLocation(j.getJobLocation());
				jobAd.setTitle(j.getTitle());

				try {
					adList = new GetSavedJobsTask().execute(getActivity().openFileInput(AppPool.SAVED_JOBS_FILENAME)).get();
					for(JobAd ad: adList) {
						if(ad.getJobId() == j.getId()) {
							isAddable = false; //already exists in the file - no need to add again
							break;
						}
					}
					if(isAddable) {
						//set to saved
						jobAd.setSaved(true);
						
						adList.add(jobAd);
						//now write to file
						LogHelper.logDebug(TAG, "Writing job data to storage file");
						FileHelper.writeStringToLocalDataFile(getActivity(), jobAd.toXmlString(adList), AppPool.SAVED_JOBS_FILENAME);

						Toast.makeText(getActivity(), R.string.job_saved_text, Toast.LENGTH_SHORT).show();
					}
					else {
						Toast.makeText(getActivity(), R.string.job_not_saved_text, Toast.LENGTH_SHORT).show();
					}
				}
				catch(FileNotFoundException e) { //means no file exists so cant be any duplcates - just go ahead and add ad
					LogHelper.logDebug(TAG, "File not found exception: storage file does not exist");

					//now write to file
					LogHelper.logDebug(TAG, "Writing job data to storage file");
					try {
					FileHelper.writeStringToLocalDataFile(getActivity(), jobAd.toXmlString(), AppPool.SAVED_JOBS_FILENAME);
					}
					catch(Exception err) {
						LogHelper.logDebug(TAG, err.toString());
					}
					
					Toast.makeText(getActivity(), R.string.job_saved_text, Toast.LENGTH_SHORT).show();
				}
				catch(Exception e) {
					LogHelper.logDebug(TAG, e.toString());
				}
			}
		});

		mApplyButton = (Button) view.findViewById(R.id.apply_show_job);
		mApplyButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//go to jobs.ie job site
				Intent applyIntent = new Intent();
				applyIntent.setAction(Intent.ACTION_VIEW);
				applyIntent.setData(Uri.parse(j.getJobsIeUrl()));
				startActivity(applyIntent);
				
				/**********************************************/
				/* Code Below is for View Pager functionality */
				/**********************************************/
				//toggle the fragment view pager
				/*int i = mPager.getCurrentItem();
				i = 1 - i; //change state of i toggle
				mPager.setCurrentItem(i, true);
				switch(i){
				case 0: {
					LogHelper.logDebug(TAG, "Job Description Layout");
					mApplyButton.setText(R.string.apply);
					mApplyButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.apply_icon, 0, 0, 0);
					break;
				}
				case 1: {
					LogHelper.logDebug(TAG, "Job Application Layout");
					mApplyButton.setText(R.string.details);
					mApplyButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.details_icon, 0, 0, 0);
					break;
				}
				}*/
				/**********************************************/
				/* 			End View Pager functionality 	  */
				/**********************************************/
			}
		});

		//set the layout items
		title.setText(j.getTitle());
		company.setText(j.getCompany());
		location.setText(j.getLocation());
		posted.setText(j.getDateModified());
		String jobSalary = j.getSalary();
		//if salary is not null then set the textview
		if(jobSalary != null && jobSalary.length() > 0) {
			salary.setText(j.getSalary());
		}
		//description.setText(j.getProperlyFormedDescription());
		htmlDescription.loadDataWithBaseURL(null, j.getDescription(), "text/html", "utf-8", null);

		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		LogHelper.logDebug(TAG, "onResume()");
	}
	
	@Override
	public void onPause() {
		super.onPause();
		LogHelper.logDebug(TAG, "onPause");
	}
	
	@Override
	public void onStop() {
		super.onStop();
		LogHelper.logDebug(TAG, "onStop()");
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		LogHelper.logDebug(TAG, "onDestroy()");
	}
	
	//save fragment state when leaving - used for when user goes to saved jobs
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	  super.onSaveInstanceState(savedInstanceState);
	  LogHelper.logDebug(TAG, "onSaveInstanceState()");
	  savedInstanceState.putParcelable(AppPool.JOB, j);
	}

	//restore fragment state
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogHelper.logDebug(TAG, "onActivityCreated()");
	    if (savedInstanceState != null) {
            // Restore last state for checked position.
            j = savedInstanceState.getParcelable(AppPool.JOB);
        }
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


	/**
	 * A simple pager adapter that represents 2 ScreenSlidePageFragment objects, in
	 * sequence.
	 */
	/*private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
		public ScreenSlidePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch(position){
			case 0: {
				return JobDetailsSlideFragment.newInstance(j);
			}
			case 1: {
				return JobApplySlideFragment.newInstance(j);
			}
			default: {
				return JobDetailsSlideFragment.newInstance(j);
			}
			}           
		}

		@Override
		public int getCount() {
			return NUM_PAGES;
		}
	}*/
}
