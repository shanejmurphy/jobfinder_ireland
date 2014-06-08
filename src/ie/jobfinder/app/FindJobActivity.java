package ie.jobfinder.app;

import global.AppPool;
import helpers.LogHelper;
import ie.jobfinder.app.interfaces.OnSearchSelectedListener;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import job.Job;
import soapWebService.GetSavedJobsTask;
import ad.JobAd;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.ExceptionParser;
import com.google.analytics.tracking.android.ExceptionReporter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.sherlock.navigationdrawer.compat.SherlockActionBarDrawerToggle;

public class FindJobActivity extends SherlockFragmentActivity 
	implements OnSearchSelectedListener, JobListFragment.OnJobSelectedListener {
	private static final String TAG = "FindJobActivity";
	private static final int REQUEST_CODE = 0; //Google Play Services Code
	private String mTitle = "	Job Finder Ireland";
    private DrawerLayout mDrawerLayout;
    private SherlockActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;
    
    /** The view to show the ad. */
    private AdView adView;
	

    @Override
    public void onStart() {
    	super.onStart();   	
    	EasyTracker.getInstance(this).activityStart(this);
    	
    	// Change uncaught exception parser...
        // Note: Checking uncaughtExceptionHandler type can be useful if clearing ga_trackingId during development to disable analytics - avoid NullPointerException.
        Thread.UncaughtExceptionHandler uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        if (uncaughtExceptionHandler instanceof ExceptionReporter) {
          ExceptionReporter exceptionReporter = (ExceptionReporter) uncaughtExceptionHandler;
          exceptionReporter.setExceptionParser(new AnalyticsExceptionParser());
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_job);
        
        //hide the keyboard window at startup
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        
        // Initializing here allows the LogHelper to know if the app is in debug mode or not.
        LogHelper.initialize(this);
        LogHelper.logDebug(TAG, "Logging Initialised");
        
        //dont keep connections alive - useful to prevent socket connection error with soap calls
		System.setProperty("http.keepAlive", "false");
		
		//layout
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);	
		mDrawerToggle = new SherlockActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
			public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
			public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerList = (ListView) findViewById(R.id.lst_left_drawer);
        
		//set customised adapter for list
        mDrawerList.setAdapter(new DrawerItemAdapter(this, R.layout.drawer_list_item, getResources().getStringArray(R.array.drawer_item_array)));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        
        //Ads
        // The "loadAdOnCreate" and "testDevices" XML attributes no longer available.
        adView = (AdView) mDrawerLayout.findViewById(R.id.adViewHome);
        AdRequest adRequest = new AdRequest.Builder()
            .addTestDevice(AppPool.TEST_DEVICE_ID)
            .build();
        adView.loadAd(adRequest);

        //ActionBar Stuff
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setTitle(mTitle);
        
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container_find_job) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            JobRequirementsFragment requirementsFragment = new JobRequirementsFragment();
            
            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            requirementsFragment.setArguments(getIntent().getExtras());
            
            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container_find_job, requirementsFragment).commit();
            
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//    	getSupportMenuInflater().inflate(R.menu.activity_find_job, menu);
//        return true;
//    }
//    
    
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
        	return true;
        }
        // Handle your other action bar items...
        // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.search:{
	        return true;
	    }
	    default:{
	    	return super.onOptionsItemSelected(item);
	    }
	    }
    }
    
    
    @Override
 	protected void onResume() {
 		super.onResume();
 		LogHelper.logDebug(TAG, "onResume()");
 		
 		if (adView != null) {
 		      adView.resume();
 		}
 		
 		//check for google play services before proceeding
     	int googleServicesResult = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
     	if(googleServicesResult != ConnectionResult.SUCCESS){
     		Dialog dialog = GooglePlayServicesUtil.getErrorDialog(googleServicesResult, FindJobActivity.this, REQUEST_CODE);
             dialog.setCancelable(false); 
             //dialog.setOnDismissListener(getOnDismissListener());
             dialog.show();
     	}
 	}
    
    @Override
 	protected void onPause() {
 		super.onPause();
 		
 		if (adView != null) {
 		      adView.pause();
 		}
 		LogHelper.logDebug(TAG, "onPause()");
    }
    
    @Override
 	protected void onStop() {
 		super.onStop();
 		LogHelper.logDebug(TAG, "onStop()");
 		EasyTracker.getInstance(this).activityStop(this);
    }
    
    @Override
 	protected void onDestroy() {
    	// Destroy the AdView.
        if (adView != null) {
          adView.destroy();
        }
 		super.onDestroy();
 		LogHelper.logDebug(TAG, "onDestroy()");
    }

	@Override
	public void onJobAdsResult(ArrayList<JobAd> jobs) {
		LogHelper.logDebug(TAG, "onJobAdsResult");
		//switch fragments on handsets
		// Create fragment and give it an argument specifying the article it should show
		JobListFragment jobList = JobListFragment.newInstance(jobs);
		/*Bundle args = new Bundle();
		args.putParcelableArrayList(JobListFragment.ARG_LIST, jobs);
		jobList.setArguments(args);*/

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		// Replace whatever is in the fragment_container view with this fragment,
		// and add the transaction to the back stack so the user can navigate back
		transaction.replace(R.id.fragment_container_find_job, jobList);
		transaction.addToBackStack(null);

		// Commit the transaction
		transaction.commit();
	} 
	
	@Override
	public void onJobDetailsResult(Job job) {
		LogHelper.logDebug(TAG, "onJobAdsResult");
		String task = "JobDetailsFragment";
		
		/*Intent intent = new Intent(this, ShowJobActivity.class);
		intent.putExtra(AppPool.JOB, job);
		startActivity(intent);*/
		
		//switch fragments on handsets
		// Create fragment and give it an argument specifying the article it should show
		JobDetailsFragment details = JobDetailsFragment.newInstance(job);

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		// Replace whatever is in the fragment_container view with this fragment,
		// and add the transaction to the back stack so the user can navigate back
		transaction.replace(R.id.fragment_container_find_job, details);
		transaction.addToBackStack(task);

		// Commit the transaction
		transaction.commit();
	}
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    }
    
    
    //new class for NAvigation Drawer
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
        // Create a new fragment and specify the planet to show based on position
//        Fragment fragment = new PlanetFragment();
//        Bundle args = new Bundle();
//        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
//        fragment.setArguments(args);

        // Insert the fragment by replacing any existing fragment
//        FragmentManager fragmentManager = getFragmentManager();
//        fragmentManager.beginTransaction()
//                       .replace(R.id.content_frame, fragment)
//                       .commit();
    	switch(position){
    	case(0):{
    		try {
    			LogHelper.logDebug(TAG, "Retrieving Saved Jobs");
  			  new GetSavedJobsTask(this).execute(this.openFileInput(AppPool.SAVED_JOBS_FILENAME)); //split the xml and return a list of JobAds
  		  } catch (FileNotFoundException e) {
  			  LogHelper.logDebug(TAG, "FileNotFoundException: " + e.toString());
  			  Toast.makeText(this, "No Jobs have been Saved Yet!", Toast.LENGTH_LONG).show();
  		  } catch (Exception e) {
  			  LogHelper.logDebug(TAG, e.toString());
  		  }
    		break;
    	}
    	case(1):{
    		//create an alert dialog
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	LayoutInflater li = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        	View about = li.inflate(R.layout.dialog_about, null);
            builder.setTitle(R.string.txt_about_title);
            builder.setView(about);
            builder.setCancelable(true);
            builder.show();
            break;
    	}
    	case(2):{
    		//create an alert dialog for rating
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	LayoutInflater li = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        	View rate = li.inflate(R.layout.dialog_rate, null);
            builder.setTitle(R.string.txt_rate_title);
            builder.setView(rate);
            builder.setPositiveButton(R.string.txt_yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(AppPool.APP_URL));
                    //Bundle b = new Bundle();
//                    b.putBoolean("new_window", true); //sets new window
//                    intent.putExtras(b);
                    startActivity(intent);
                 }
            });
            builder.setNegativeButton(R.string.txt_no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                 }
            });
            builder.show();
            break;
    	}
    	case(3):{ //help
    		//create an alert dialog
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	LayoutInflater li = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        	View help = li.inflate(R.layout.dialog_help, null);
            builder.setTitle(R.string.txt_help_title);
            builder.setView(help);
            builder.setCancelable(true);
            builder.show();
            break;
    	}
    	}
    	
        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        //setTitle(mPlanetTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }
    
    public class AnalyticsExceptionParser implements ExceptionParser {

		@Override
		public String getDescription(String threadName, Throwable t) {
			// TODO Auto-generated method stub
			String exception = "threadName = "
			        + threadName
			        + "\ngetMessage()= " + t.getMessage()
			        + "\ngetLocalizedMessage()=" + t.getLocalizedMessage()
			        + "\ngetCause()=" + t.getCause()
			        + "\ngetStackTrace()=" + t.getStackTrace();
			return exception;
		}    	
    }
}
