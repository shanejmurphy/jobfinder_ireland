package ie.jobfinder.app;

import global.AppPool;
import helpers.FileHelper;
import helpers.LogHelper;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import soapWebService.GetSavedJobsTask;
import ad.JobAd;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class JobItemLayoutAdapter extends ArrayAdapter<JobAd> {
	private static final String TAG = "JobItemLayoutAdapter";
	
	private ArrayList<JobAd> mJobList;
	private ArrayList<JobAd> savedAdList = new ArrayList<JobAd>();

	public JobItemLayoutAdapter(Context context, int resource, ArrayList<JobAd> ads) {
		super(context, resource, ads);
		this.mJobList = ads;
		
		
		try {
			savedAdList = new GetSavedJobsTask().execute(getContext().openFileInput(AppPool.SAVED_JOBS_FILENAME)).get();
		} catch(NullPointerException npe) {
			LogHelper.logDebug(TAG, "NullPointerException getting saved jobs - " + npe.toString());
		} catch(Exception e) {
			LogHelper.logDebug(TAG, "Error getting saved jobs");
		}
	}
	
	public class ViewHolder{
		public TextView mTitle;  
		public TextView mCompany;
		public TextView mLocation;
		public TextView mDatePosted;
		public ImageButton mSave;
		public ImageButton mDelete;
	}
	
	/*public void setList(ArrayList<JobAd> items) {
        this.mJobList.clear(); 
        this.mJobList.addAll(items);
        this.notifyDataSetChanged();
    }
	
	//method for adding more items to the list
	public void addToList(ArrayList<JobAd> items) {
        this.mJobList.addAll(items);
        this.notifyDataSetChanged();
    }*/
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View v = convertView;
		final ViewHolder holder;
		if(v == null) 
		{  
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
            v = vi.inflate(R.layout.layout_job_item, null);
            holder = new ViewHolder();
            holder.mTitle = (TextView) v.findViewById(R.id.title_job_item);
            holder.mCompany = (TextView) v.findViewById(R.id.company_name_job_item);
            holder.mLocation = (TextView) v.findViewById(R.id.location_name_job_item);
            holder.mDatePosted = (TextView) v.findViewById(R.id.date_job_item);
            holder.mSave = (ImageButton) v.findViewById(R.id.btn_save_job_item);
            holder.mDelete= (ImageButton) v.findViewById(R.id.btn_delete_job_item);
            //Dont want this to show until loading more items
            //holder.progress = (LinearLayout) v.findViewById(R.id.progress_layout);
            //holder.progress.setVisibility(View.GONE);
            v.setTag(holder);
		}  
		else{
			holder =(ViewHolder) v.getTag();
		}
		
		//Job
        final JobAd lJobAd = this.mJobList.get(position); 
    	String jobTitle = lJobAd.getTitle(); //convert integer to a string for display purposed
    	String companyName = lJobAd.getCompany(); //convert integer to a string for display purposed
        String locationName = lJobAd.getLocation();
        String datePosted = lJobAd.getDateCreated();
    	
        if(lJobAd != null){
        	holder.mTitle.setText(jobTitle); 
        	holder.mCompany.setText(companyName);
        	holder.mLocation.setText(locationName);
        	holder.mDatePosted.setText(datePosted); 
        	holder.mSave.setVisibility(View.VISIBLE);
        	holder.mDelete.setVisibility(View.INVISIBLE);

        	if(savedAdList != null) {
				for(JobAd ad: savedAdList) {
					if(ad.getJobId() == lJobAd.getJobId()) {
						lJobAd.setSaved(true);
						//LogHelper.logDebug(TAG, ad.getTitle() + " == " + lJobAd.getTitle());
						//jobRemoved = true;
		            	//Job is laready saved so show the delete icon instead of the save icon
						
		            	holder.mSave.setVisibility(View.INVISIBLE);
		            	holder.mDelete.setVisibility(View.VISIBLE);
		            	
						break;
					}
				}  
        	}
    	
        	holder.mDelete.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {					
					boolean jobRemoved = false; //can we add the ad to the file
					//write the job to a file to save

					for(JobAd ad: savedAdList) {
						if(ad.getJobId() == lJobAd.getJobId()) {
							savedAdList.remove(ad);
							LogHelper.logDebug(TAG, ad.getTitle() + " + Removed from Saved Jobs List");
							jobRemoved = true;
							break;
						}
					}
					if(jobRemoved) { //need to rewrite the file
						//now write to file
						LogHelper.logDebug(TAG, "Re-writing job data to storage file");
						
						try {
							FileHelper.writeStringToLocalDataFile(getContext(), lJobAd.toXmlString(savedAdList), AppPool.SAVED_JOBS_FILENAME);
						} 
						catch(FileNotFoundException e) { //means no file exists so cant be any duplcates - just go ahead and add ad
							LogHelper.logDebug(TAG, "File not found exception: storage file does not exist");
							
							//now write to file
							LogHelper.logDebug(TAG, "Cant delete as no file found");
	
							Toast.makeText(getContext(), R.string.error_deleting_job_text, Toast.LENGTH_SHORT).show();
						}
						catch(Exception e) {
							LogHelper.logDebug(TAG, e.toString());
						}
						//set saved boolean
						lJobAd.setSaved(false);
						
		            	//Job is now saved so show the save icon instead of the delete icon
		            	holder.mDelete.setVisibility(View.INVISIBLE);
		            	holder.mSave.setVisibility(View.VISIBLE);
						
						Toast.makeText(getContext(), R.string.job_deleted_text, Toast.LENGTH_SHORT).show();
					}
					else {
						//dont do anything as we didnt find a matching job but remove icon still displayed so error in code
						LogHelper.logDebug(TAG, "No file deleted");
						//Toast.makeText(getContext(), R.string.job_not_saved_text, Toast.LENGTH_SHORT).show();
					}
				}		
        	});
        	
        	holder.mSave.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					boolean isAddable = true; //can we add the ad to the file
//					//write the job to a file to save
					//check to make sure job is not already saved
					if(savedAdList != null) {
						for(JobAd ad: savedAdList) {
							if(ad.getJobId() == lJobAd.getJobId()) {
								isAddable = false; //already exists in the file - no need to add again
								break;
							}
						}
					}
					if(isAddable) {
						savedAdList.add(lJobAd);
						//now write to file
						LogHelper.logDebug(TAG, "Writing job data to storage file");
						try {
							FileHelper.writeStringToLocalDataFile(getContext(), lJobAd.toXmlString(savedAdList), AppPool.SAVED_JOBS_FILENAME);
						} 
						catch(FileNotFoundException e) { //means no file exists so cant be any duplcates - just go ahead and add ad
							LogHelper.logDebug(TAG, "File not found exception: storage file does not exist");
	
							//now write to file
							LogHelper.logDebug(TAG, "Writing job data to storage file");
							try {
								FileHelper.writeStringToLocalDataFile(getContext(), lJobAd.toXmlString(), AppPool.SAVED_JOBS_FILENAME);
							}
							catch(Exception err) {
								LogHelper.logDebug(TAG, err.toString());
							}
							Toast.makeText(getContext(), R.string.job_saved_text, Toast.LENGTH_SHORT).show();
						}
						catch(Exception e) {
							LogHelper.logDebug(TAG, e.toString());
						}
						//set saved boolean
						lJobAd.setSaved(true);
						
		            	//Job is now saved so show the delete icon instead of the save icon
		            	holder.mSave.setVisibility(View.INVISIBLE);
		            	holder.mDelete.setVisibility(View.VISIBLE);
						
						Toast.makeText(getContext(), R.string.job_saved_text, Toast.LENGTH_SHORT).show();
					}
					else {
						Toast.makeText(getContext(), R.string.job_not_saved_text, Toast.LENGTH_SHORT).show();
					}
				}
        	});
    	}        
        return v;
	}
}
