package job;


public class JobSearch {
	private int mCategoryId;
	private int mLocationId;
	private String mJobType;
//	private boolean mExcludeAgencies = false;
//	private JobDuration mJobType = JobDuration.ANY;
//	private JobHours mJobHours = JobHours.ANY;
	private String mKeywords;
	private int mCategoryListPosition;
	private int mLocationListPosition;
//	private Date mFromDate;
	
	public JobSearch(){
		
	}
	
	public JobSearch(int category, int catPos, int location, int locPos, String jobType, String keywords) {
		this.mCategoryId = category;
		this.mCategoryListPosition = catPos;
		this.mLocationId = location;
		this.mLocationListPosition = locPos;
		this.mJobType = jobType;
		this.mKeywords = keywords;
	}
	
	//get & set
	public void setCategoryId(int id){
		this.mCategoryId = id;
	}
	
	public void setLocationId(int id){
		this.mLocationId = id;
	}
	
//	public void setExcludeAgencies(boolean bool){
//		this.mExcludeAgencies = bool;
//	}
	
	public void setKeywords(String keywords){
		this.mKeywords = keywords;
	}
	
	/*public void setFromDate(Date d){
		this.mFromDate = d;
	}
	
	public void setJobDuration(JobDuration dur){
		this.mJobType = dur;
	}
	
	public void setJobHours(JobHours time){
		this.mJobHours = time;
	}*/
	
	public void setJobType(String type){
		this.mJobType = type;
	}

	public void setCategoryListPosition(int pos){
		this.mCategoryListPosition = pos;
	}
	
	public void setLocationListPosition(int pos){
		this.mLocationListPosition = pos;
	}	
	
	public int getCategoryId() {
		return mCategoryId;
	}

	public int getLocationId() {
		return mLocationId;
	}

	/*public boolean isExcludeAgencies() {
		return mExcludeAgencies;
	}

	public JobDuration getJobType() {
		return mJobType;
	}

	public JobHours getJobHours() {
		return mJobHours;
	}*/
	
	public String getJobType() {
		return mJobType;
	}

	public String getKeywords() {
		return mKeywords;
	}
	
	public int getCategoryListPosition() {
		return this.mCategoryListPosition;
	}

	public int getLocationListPosition() {
		return this.mLocationListPosition;
	}

/*	public Date getFromDate() {
		return mFromDate;
	}*/

}
