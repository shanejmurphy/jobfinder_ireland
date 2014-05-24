package ie.jobfinder.app;

import global.AppPool;
import helpers.LogHelper;
import ie.jobfinder.app.interfaces.OnCategoriesResultListener;
import ie.jobfinder.app.interfaces.OnJobAdResultListener;
import ie.jobfinder.app.interfaces.OnLocationResultListener;
import ie.jobfinder.app.interfaces.OnSearchSelectedListener;

import java.lang.reflect.Type;
import java.util.ArrayList;

import job.JobCategory;
import job.JobLocation;
import job.JobSearch;

import org.droidparts.widget.ClearableEditText;

import soapWebService.GetAdsTask;
import soapWebService.GetCategoriesTask;
import soapWebService.GetLocationsTask;
import ad.JobAd;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class JobRequirementsFragment extends SherlockDialogFragment implements OnCategoriesResultListener,
		OnLocationResultListener, OnItemSelectedListener, OnJobAdResultListener{
	private static final String TAG = "JobRequirementsFragment";
	private static final String INDUSTRY_PREFS = "Industry";
	private static final String LOCATION_PREFS = "Location";
	private static final String SEARCH_PREFS = "Search";
	
	private static ArrayList<JobCategory> mCategories;
	private ArrayList<JobLocation> mLocations;
	
	//holders for stored sharedpref values
	private ArrayList<JobCategory> mCategoryPref = new ArrayList<JobCategory>();
	private ArrayList<JobLocation> mLocationPref = new ArrayList<JobLocation>();
	private ArrayList<JobSearch> mSearchPref = new ArrayList<JobSearch>();
	
	//holders for list values
	private ArrayList<String> mCategoryList = new ArrayList<String>();
	private ArrayList<String> mLocationList = new ArrayList<String>();
	private ArrayList<JobSearch> mRecentSearches = new ArrayList<JobSearch>();

	private int categoryId;
	private int locationId;
	private String jobType = "Any"; 	//required for search call
	private int datePosted = 0; 		//required for search call
	private String keywords = "";
	
	private int mCategoryPos = 0;
	private int mLocationPos = 0;
	
	private Spinner categorySpinner;
	private Spinner locationSpinner;
	
	private static int getTasksCount = 0;
	
	private OnSearchSelectedListener mCallback;
	
	//private ArrayList<JobAd> mAds; 		//member variable ads (holder for all the adds returned from request)

	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        //change actionbar title
        ((FindJobActivity) activity).getSupportActionBar().setTitle("\tSearch Jobs");
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnSearchSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnSearchSelectedListener");
        }
    }
    
    @Override
    public void onStart(){
    	super.onStart();  	
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	LogHelper.logDebug(TAG, "onCreate()");
    			
		// Restore preferences
       	SharedPreferences cPrefs = getActivity().getSharedPreferences(INDUSTRY_PREFS, 0);
       	Gson gson = new Gson();
       	Type categoryType = new TypeToken<ArrayList<JobCategory>>(){}.getType();
       	String json = cPrefs.getString("Industries", "");
       	if(json != "") {
	       	mCategoryPref = gson.fromJson(json, categoryType);
	       	//set up Catgories temporarily to equal whatever is in pref file
	       	mCategories = mCategoryPref;
       	}
       	
       	SharedPreferences lPrefs = getActivity().getSharedPreferences(LOCATION_PREFS, 0);
       	Type locationType = new TypeToken<ArrayList<JobLocation>>(){}.getType();
       	json = lPrefs.getString("Locations", "");
       	if(json != "") {
	       	mLocationPref = gson.fromJson(json, locationType);    	
	       	mLocations = mLocationPref;
       	}
       	
       	SharedPreferences sPrefs = getActivity().getSharedPreferences(SEARCH_PREFS, 0);
       	Type jobSearchType = new TypeToken<ArrayList<JobSearch>>(){}.getType();
       	json = sPrefs.getString("Searches", "");
       	if(json != "") {
	       	mSearchPref = gson.fromJson(json, jobSearchType);    	
	       	mRecentSearches = mSearchPref;
       	}
              
    	//get drop down choices for selection criteria - only use if differ from sharedpref object values
       	//only want to do this every x times
       	if(getTasksCount == 0) {
       		LogHelper.logDebug(TAG, "Executing Tasks to return Industry & Location");
	    	getCategoryChoices();	
	    	getLocationChoices(); 
	    	getTasksCount++;
       	}
       	else if(getTasksCount == 5 || mCategories.size() <=1 || mLocations.size() <= 1) {
       		getTasksCount = 0;
       	}
       	else {
       		getTasksCount++;
       	}
    	
    	setHasOptionsMenu(false);
    }
	
    
    @Override
    public void onResume() {
    	super.onResume();
    	LogHelper.logDebug(TAG, "onResume()");
    	((FindJobActivity)getActivity()).getSupportActionBar().setTitle("\tSearch Jobs");
    }
    
    @Override
 	public void onStop() {
 		super.onStop();
 		LogHelper.logDebug(TAG, "onStop()");
    }
    
    
    @Override
    public void onPause() {
    	super.onPause();
    	LogHelper.logDebug(TAG, "onPause()");
    	LogHelper.logDebug(TAG, "saving shared preferences");
    	
    	//store important values
       	SharedPreferences cPrefs = getActivity().getSharedPreferences(INDUSTRY_PREFS, 0);
       	Editor industryPrefsEditor = cPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mCategoryPref);
        industryPrefsEditor.putString("Industries", json);
        industryPrefsEditor.commit();
        
       	SharedPreferences lPrefs = getActivity().getSharedPreferences(LOCATION_PREFS, 0);
       	Editor locationPrefsEditor = lPrefs.edit();
        json = gson.toJson(mLocationPref);
        locationPrefsEditor.putString("Locations", json);
        locationPrefsEditor.commit();
        
        SharedPreferences sPrefs = getActivity().getSharedPreferences(SEARCH_PREFS, 0);
       	Editor searchPrefsEditor = sPrefs.edit();
        json = gson.toJson(mRecentSearches);
        searchPrefsEditor.putString("Searches", json);
        searchPrefsEditor.commit();
    }
    
    
    @Override
 	public void onDestroy() {
 		super.onDestroy();
 		LogHelper.logDebug(TAG, "onDestroy()");
    }
    
    
	  @Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		  //inflate view
		  View view = inflater.inflate(R.layout.fragment_find_job, container, false);
		  
          // The "loadAdOnCreate" and "testDevices" XML attributes no longer available.
          AdView adView = (AdView) view.findViewById(R.id.adViewHome);
          AdRequest adRequest = new AdRequest.Builder()
              .addTestDevice(AppPool.TEST_DEVICE_ID)
              .build();
          adView.loadAd(adRequest);
		  
		  //initialise fragment entities
          /*Button savedButton = (Button) view.findViewById(R.id.saved_jobs_btn);
          savedButton.setOnClickListener(new View.OnClickListener() {
			  public void onClick(View v) {
				  //search for jobs given a set of search params
				  //String xml = FileHelper.readFromFile(getActivity(), AppPool.SAVED_JOBS_FILENAME);
				  //LogHelper.logDebug(TAG, xml);
				  try {
					  ArrayList<JobAd> ja = new GetSavedJobsTask().execute(getActivity().openFileInput(AppPool.SAVED_JOBS_FILENAME)).get(); //split the xml and return a list of JobAds
					  doCallBack(ja);
					  LogHelper.logDebug(TAG, ja.get(0).getTitle());
				  } catch (FileNotFoundException e) {
					  LogHelper.logDebug(TAG, "FileNotFoundException: " + e.toString());
				  } catch (Exception e) {
					  LogHelper.logDebug(TAG, e.toString());
				  }
			  }
	      });*/
          
		  final ClearableEditText keywordInput = (ClearableEditText) view.findViewById(R.id.keyword);
		  keywordInput.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)
                {
					LogHelper.logDebug(TAG, "Keyboard Enter Event called");
					InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
						      Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(keywordInput.getWindowToken(), 0);
					return false;
                }
				return false;
			}
	      });
		  categorySpinner = (Spinner) view.findViewById(R.id.category);
		  categorySpinner.setOnItemSelectedListener(this);
		  locationSpinner = (Spinner) view.findViewById(R.id.location);
		  locationSpinner.setOnItemSelectedListener(this);
		  Spinner jobTypeSpinner = (Spinner) view.findViewById(R.id.job_type);
		  jobTypeSpinner.setOnItemSelectedListener(this);
		  //Spinner dateSpinner = (Spinner) view.findViewById(R.id.date_posted);	 
		  //dateSpinner.setOnItemSelectedListener(this);
		  Button searchButton = (Button) view.findViewById(R.id.search);
		  searchButton.setOnClickListener(new View.OnClickListener() {
			  public void onClick(View v) {
				  //search for jobs given a set of search params
				  keywords = keywordInput.getText().toString();
				  searchJobs(categoryId, locationId, jobType, datePosted, keywords, false);
			  }
	      });
		  
		  /****************************************************************************
		  							//Recent Search stuff
		  ****************************************************************************/
		  RelativeLayout searchItemLayout1 = (RelativeLayout) view.findViewById(R.id.search_item_1);
		  RelativeLayout searchItemLayout2 = (RelativeLayout) view.findViewById(R.id.search_item_2);
		  RelativeLayout searchItemLayout3 = (RelativeLayout) view.findViewById(R.id.search_item_3);
		  //RelativeLayout searchItemLayout4 = (RelativeLayout) view.findViewById(R.id.search_item_4);
		  
		  //Recent Searches Title
		  TextView recentSearchTitle = (TextView) view.findViewById(R.id.txt_recent_searches);
		  
		  //Actual searched items
		  Button searchItem1 = (Button) view.findViewById(R.id.btn_search_item_1);
		  searchItem1.setOnClickListener(new View.OnClickListener() {
			  public void onClick(View v) {
				  searchJobs(mRecentSearches.get(0).getCategoryId(), mRecentSearches.get(0).getLocationId(), mRecentSearches.get(0).getJobType(), datePosted, mRecentSearches.get(0).getKeywords(), true);
			  }
	      });
		  Button searchItem2 = (Button) view.findViewById(R.id.btn_search_item_2);
		  searchItem2.setOnClickListener(new View.OnClickListener() {
			  public void onClick(View v) {
				  searchJobs(mRecentSearches.get(1).getCategoryId(), mRecentSearches.get(1).getLocationId(), mRecentSearches.get(1).getJobType(), datePosted, mRecentSearches.get(1).getKeywords(), true);
			  }
	      });
		  Button searchItem3 = (Button) view.findViewById(R.id.btn_search_item_3);
		  searchItem3.setOnClickListener(new View.OnClickListener() {
			  public void onClick(View v) {
				  searchJobs(mRecentSearches.get(2).getCategoryId(), mRecentSearches.get(2).getLocationId(), mRecentSearches.get(2).getJobType(), datePosted, mRecentSearches.get(2).getKeywords(), true);
			  }
	      });
		  
		  //Logic to display contnet
		  if(this.mRecentSearches.size() == 0) {
			  recentSearchTitle.setVisibility(View.INVISIBLE);
			  
			  searchItemLayout1.setVisibility(View.INVISIBLE);
			  searchItemLayout2.setVisibility(View.INVISIBLE);
			  searchItemLayout3.setVisibility(View.INVISIBLE);
			  
			  searchItem1.setText("");
			  searchItem2.setText("");
			  searchItem3.setText("");
		  }
		  else {
			  //display title
			  recentSearchTitle.setVisibility(View.VISIBLE);
			  
			  if(this.mRecentSearches.size() == 1) {
				  searchItem1.setText(getText(this.mRecentSearches.get(0)));
				  searchItemLayout1.setVisibility(View.VISIBLE);
				  
				  searchItemLayout2.setVisibility(View.INVISIBLE);
				  searchItemLayout3.setVisibility(View.INVISIBLE);
			  }
			  else if(this.mRecentSearches.size() == 2) {
				  searchItem1.setText(getText(this.mRecentSearches.get(0)));
				  searchItem2.setText(getText(this.mRecentSearches.get(1)));
				  searchItemLayout2.setVisibility(View.VISIBLE);	
				  
				  searchItemLayout3.setVisibility(View.INVISIBLE);
			  }
			  else if(this.mRecentSearches.size() == 3) {
				  searchItem1.setText(getText(this.mRecentSearches.get(0)));
				  searchItem2.setText(getText(this.mRecentSearches.get(1)));
				  searchItem3.setText(getText(this.mRecentSearches.get(2)));
				  searchItemLayout3.setVisibility(View.VISIBLE);			  		  			  
			  }
		  }
		  
		  /*else if(this.mRecentSearches.size() == 4) {
			  recentSearchTitle.setVisibility(View.VISIBLE);
			  searchItemLayout1.setVisibility(View.VISIBLE);
			  searchItemLayout2.setVisibility(View.VISIBLE);
			  searchItemLayout3.setVisibility(View.VISIBLE);
			  searchItemLayout4.setVisibility(View.VISIBLE);			  
			  searchItem1.setText(getText(this.mRecentSearches.get(0)));
			  searchItem2.setText(getText(this.mRecentSearches.get(1)));			  
			  searchItem3.setText(getText(this.mRecentSearches.get(2)));			  
			  searchItem4.setText(getText(this.mRecentSearches.get(3)));
		  }

		  Button removeSearchItem1 = (Button) view.findViewById(R.id.btn_remove_search_item_1);
		  removeSearchItem1.setOnClickListener(new View.OnClickListener() {
			  public void onClick(View v) {
				  removeSearchItem1();
			  }
	      });
		  Button removeSearchItem2 = (Button) view.findViewById(R.id.btn_remove_search_item_2);
		  removeSearchItem2.setOnClickListener(new View.OnClickListener() {
			  public void onClick(View v) {
				  removeSearchItem2();
			  }
	      });
		  Button removeSearchItem3 = (Button) view.findViewById(R.id.btn_remove_search_item_3);
		  removeSearchItem3.setOnClickListener(new View.OnClickListener() {
			  public void onClick(View v) {
				  removeSearchItem3();
			  }
	      });
		  Button removeSearchItem4 = (Button) view.findViewById(R.id.btn_remove_search_item_4);
		  removeSearchItem4.setOnClickListener(new View.OnClickListener() {
			  public void onClick(View v) {
				  removeSearchItem4();
			  }
	      });*/
		  
		  //get the categories list
		  /*mCategories = getCategoryChoices(); 		  
		  //create String List from job categories
		  ArrayList<String> list = new ArrayList<String>();
		  for(int i=0; i<mCategories.size(); i++) {
			  String s = mCategories.get(i).getCategoryName();
			  list.add(s); 
		  }
		  
		  //get the locations
		  mLocations = getLocationChoices();
		  //create a list of Strings from location results
		  ArrayList<String> locationList = new ArrayList<String>();
		  for(int j=0; j<mLocations.size(); j++) {
			  String s = mLocations.get(j).getLocationName();
			  locationList.add(s);
		  }*/
		  
		  /****************************************************************************
									//End Recent Search stuff
		  ****************************************************************************/
		  
		  if(mCategoryPref != null) {
	    	  createInitialCategorySpinner(mCategoryPref);
	      }
	      
	      if(mLocationPref != null) {
	    	  createInitialLocationSpinner(mLocationPref);
	      }
		  //create spinner adapters with drop down info
		  //createSpinnerFromList(categorySpinner, this.mCategoryList);
		  //createSpinnerFromList(locationSpinner, this.mLocationList);
		  createSpinnerFromResource(jobTypeSpinner, R.array.job_type_array);
		  //createSpinnerFromResource(dateSpinner, R.array.date_posted_array);
		  

		  return view;
	  }
	  
	  //execute getCategoriesTask to return the list of categories available to choose from
	  private void getCategoryChoices() {
		  //ArrayList<JobCategory> categories = new ArrayList<JobCategory>();
		  //get requirement drop down fields
		  try {
				
			  //get a list of the job categories
			  //categories = 
			  new GetCategoriesTask(this).execute();//.get();
			  //LogHelper.logDebug(TAG, "Category ID: " + categories.get(6).getCategoryId() + ", Category Name: " + categories.get(6).getCatgoryName());
			  
		  }catch(Exception e) {
			LogHelper.logDebug(TAG, e.toString());
		  }
	     
		  //return categories;
	  }
	  
	  /**
	   * @author shanem
	   * 
	   * @return ArrayList<JobLocation>
	   * <p>Method to retrieve list of locations for users to select from dropdown/spinner</p>
	   */
	  private void getLocationChoices() {
		  //ArrayList<JobLocation> locations = new ArrayList<JobLocation>();
		  //get requirement drop down fields
		  try {
				
			  //get a list of the job categories
			  //locations = 
			  new GetLocationsTask(this).execute();//.get();
			  //LogHelper.logDebug(TAG, "Category ID: " + categories.get(6).getCategoryId() + ", Category Name: " + categories.get(6).getCatgoryName());
			  
		  }catch(Exception e) {
			LogHelper.logDebug(TAG, e.toString());
		  }
	     
		  //return locations;
	  }
	  
	  //create spinner from default values stored in string arrays in strings.xml
	  private void createSpinnerFromResource(Spinner spi, int res) {
		  // Create an ArrayAdapter using the string array and a default spinner layout
		  ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), res, android.R.layout.simple_spinner_item);
		  // Specify the layout to use when the list of choices appears
		  adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		  // Apply the adapter to the spinner
		  spi.setAdapter(adapter);
	  }
	  
	  //create spinner using dynamic text values
	  private void createSpinnerFromList(Spinner spi, ArrayList<String> list) {
		  ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
		  // Specify the layout to use when the list of choices appears
		  adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		  // Apply the adapter to the spinner
		  spi.setAdapter(adapter);
	  }
	  
	  /**
	   * @author shanem
	   * @since ver 1.0
	   * <p> Search for jobs given a set of search params </p>
	   * @return ArrayList<AdJob>
	   */
	  public void searchJobs(int categories, int location, String jobType, int d, String keywords, boolean isRecentSearch) {
		  //ArrayList<JobAd> ads = new ArrayList<JobAd>();
		  //get jobs
		  try {	
			  //call the searchjobs API
			  //ads = 
			  new GetAdsTask(this).execute(categories, location, jobType, d, keywords);
			  //LogHelper.logDebug(TAG, "Category ID: " + ads.get(6).getTitle() + ", Category Name: " + categories.get(6).getCatgoryName());

			  //if search is selected from recent search list we dont want to save it again
			  if(!isRecentSearch) {
				  //only add to list if there are filters
				  if(mCategoryPos != 0 || mLocationPos !=0 || !keywords.isEmpty()) {
					  JobSearch recentSearch = new JobSearch(categories, mCategoryPos, location, mLocationPos, jobType, keywords);
					  if(this.mRecentSearches.size() == AppPool.MAX_SEARCH_SIZE) {
						  this.mRecentSearches.remove(AppPool.MAX_SEARCH_SIZE - 1);
					  } 
					  
					  this.mRecentSearches.add(0, recentSearch); //add new String to start of list
				  }
			  }
		  }catch(Exception e) {
			LogHelper.logDebug(TAG, e.toString());
		  }
		  
		  //return ads;
	  }
	  
	  private String getText(JobSearch j) {			 
		  //use keyword + Industry + location
		  //e.g. String = Android - Banking/Insurance - Dublin West
		  //	   data =  11, 21, 1, 0, Android
		  int lCategory = j.getCategoryListPosition();
		  int lLocation = j.getLocationListPosition();
		  String searchText = "";
		  if(!j.getKeywords().isEmpty()){
			  searchText = j.getKeywords();
		  }
		  if(lCategory != 0) {
			  if(!searchText.isEmpty()) {
				  searchText += " - " + mCategories.get(lCategory).getCategoryName();
			  }
			  else{
				  searchText += mCategories.get(lCategory).getCategoryName();
			  }
		  }
		  if(lLocation != 0) {
			  if(!searchText.isEmpty()) {
				  searchText += " - " + mLocations.get(lLocation).getLocationName();
			  }
			  else {
				  searchText += mLocations.get(lLocation).getLocationName();
			  }
		  }
		  
		  return searchText;
	  }
	  
	  
	  //getters & setters
	  public ArrayList<JobCategory> getCategories() {
		  return mCategories;
	  }

	  public ArrayList<JobLocation> getLocations() {
		  return mLocations;
	  }

	  public void setLocations(ArrayList<JobLocation> mLocations) {
		  this.mLocations = mLocations;
	  }

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		LogHelper.logDebug(TAG, "Spinner Item Selected");
	    switch (parent.getId()) {
		    case R.id.category: {
		    	categoryId = mCategories.get(position).getCategoryId();
		    	mCategoryPos = position;
		    	break;
		    }
		    case R.id.location: {
		    	locationId = mLocations.get(position).getLocationId();
		    	mLocationPos = position;
		    	break;
		    }
		    case R.id.job_type: {
		    	if(position == 0) {
		    		jobType = "Any";
		    	}
		    	if(position == 1) {
		    		jobType = "Permanent";
		    	}
		    	if(position == 2) {
		    		jobType = "Contract";
		    	}
		    	break;
		    }
		    /*case R.id.date_posted: {
		    	datePosted = position;
		    }*/
	    }
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	@Override
	public void doCallBack(ArrayList<JobAd> jobAds) {
		mCallback.onJobAdsResult(jobAds);
	}


	
	@Override
	public void onCategoriesResult(ArrayList<JobCategory> categories) {
		//if categories size is not 1 or empty we want to update the drop down list
		// size will include the "Any" value so will always be at least 1
		if(categories.size() > 1) { //could be empty due to a timeout
			mCategories = categories;
			for(int i=0; i<mCategories.size(); i++) {
				String s = mCategories.get(i).getCategoryName();
				this.mCategoryList.add(s); 
			}
			
			//if they are different reload the spinner
			if(mCategoryPref == null || mCategories.size() != mCategoryPref.size()) {
				LogHelper.logDebug(TAG, "(Re)Loading Category Spinner");
				mCategoryPref = categories;
				createSpinnerFromList(categorySpinner, this.mCategoryList);
			}
		}
	}
	
	@Override
	public void onLocationResult(ArrayList<JobLocation> locations) {
		if(locations.size() > 1) {
			mLocations = locations;
			for(int i=0; i<mLocations.size(); i++) {
				String s = mLocations.get(i).getLocationName();
				this.mLocationList.add(s); 
			}
			
			//if they are different reload the spinner
			if(mLocationPref == null || mLocations.size() != mLocationPref.size()) {
				mLocationPref = locations;
				LogHelper.logDebug(TAG, "(Re)Loading Locations Spinner");
				createSpinnerFromList(locationSpinner, this.mLocationList);
			}
		}
	}
	
	
	//create an initial spinner from stored category list - will be changed if different from results of tasks
	public void createInitialCategorySpinner(ArrayList<JobCategory> categories) {
		ArrayList<String> catList = new ArrayList<String>();
		for(int i=0; i<categories.size(); i++) {
			String s = categories.get(i).getCategoryName();
			catList.add(s); 
		}
		
		LogHelper.logDebug(TAG, "Loading Category Spinner");
		createSpinnerFromList(categorySpinner, catList);
	}
	
		
	//create an initial spinner from stored location list - will be changed if different from results of tasks
	public void createInitialLocationSpinner(ArrayList<JobLocation> locations) {
		ArrayList<String> locList = new ArrayList<String>();
		for(int i=0; i<locations.size(); i++) {
			String s = locations.get(i).getLocationName();
			locList.add(s); 
		}
		
		LogHelper.logDebug(TAG, "Loading Locations Spinner");
		createSpinnerFromList(locationSpinner, locList);
	}
	
	
/*	public void removeSearchItem1() {
		this.mRecentSearches.remove(0);
	}
	public void removeSearchItem2() {
		this.mRecentSearches.remove(1);
	}
	public void removeSearchItem3() {
		this.mRecentSearches.remove(2);
	}
	public void removeSearchItem4() {
		this.mRecentSearches.remove(3);
	}*/
}
