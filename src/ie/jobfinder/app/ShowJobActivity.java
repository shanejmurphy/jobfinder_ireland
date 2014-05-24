/*package ie.jobfinder.app;

import global.AppPool;
import helpers.LogHelper;
import job.Job;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import com.actionbarsherlock.view.MenuItem;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class ShowJobActivity extends SherlockFragmentActivity {
	private static final String TAG = "ShowJobActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_job);
        
        LogHelper.logDebug(TAG, "onCreate()");
        
        // Make sure we're running on Honeycomb or higher to use ActionBar APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
        	getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        //get the bundeld data
        Bundle bundle = getIntent().getExtras();
        Job bundledJob = (Job) bundle.get(AppPool.JOB);
        
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container_show_job) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            JobDetailsFragment detailsFragment = JobDetailsFragment.newInstance(bundledJob);

    		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            
            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            //detailsFragment.setArguments(getIntent().getExtras());
            
            // Add the fragment to the 'fragment_container' FrameLayout
            transaction.add(R.id.fragment_container_show_job, detailsFragment).commit();
            
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
*/