package job;

import global.AppPool;
import android.os.Parcel;
import android.os.Parcelable;

public class Job implements Parcelable {
	private int mId;
	//private int mEmployerId;
	private String mTitle;
	//private String mCategory;
	private String mDescription;
	private String mLocation;
	private String mSalary;
	private String mDatePosted;
	private String mDateModified;
	private String mApplicationURL;
	private String mQuestion1;
	private String mQuestion2;
	private String mQuestion3;
	private String mHomePageURL;
	private String mCompany;
	private String mAddress;
	private String mPhone;
	private String mFax;
	private String mEmail;
	private String mContactName;
	private boolean mIsContactHidden = true;
	private boolean mIsCompanyNameHidden = true;
	private boolean mIsEmailHidden = true;
	private boolean mIsPhoneHidden = true;
	private boolean mIsFaxHidden = true;
	private boolean mIsAddressHidden = true;
	private boolean mIsHomePageHidden = true;
	
	//default constructor
	public Job(){}
	
	//set & get
	public int getId() {
		return mId;
	}

	public void setId(int mId) {
		this.mId = mId;
	}
	
	public String getTitle(){
		return this.mTitle;
	}
	
	public String getDescription(){
		return this.mDescription;
	}
	
	public String getProperlyFormedDescription(){
		String description = this.mDescription;
		return android.text.Html.fromHtml(description).toString();
	}
	
	public String getJobLocation(){
		return this.mLocation;
	}
	
	public String getJobSalary(){
		return this.mSalary;
	}

	public void setTitle(String mTitle) {
		this.mTitle = mTitle;
	}

	public void setDescription(String mDescription) {
		this.mDescription = mDescription;
	}

	public String getLocation() {
		return mLocation;
	}

	public void setLocation(String mLocation) {
		this.mLocation = mLocation;
	}

	public String getSalary() {
		return mSalary;
	}

	public void setSalary(String mSalary) {
		this.mSalary = mSalary;
	}

	public String getDatePosted() {
		return mDatePosted;
	}

	public void setDatePosted(String mDatePosted) {
		this.mDatePosted = mDatePosted;
	}

	public String getDateModified() {
		return mDateModified;
	}

	public void setDateModified(String mDateModified) {
		this.mDateModified = mDateModified;
	}

	public String getApplicationURL() {
		return mApplicationURL;
	}

	public void setApplicationURL(String mApplicationURL) {
		this.mApplicationURL = mApplicationURL;
	}

	public String getQuestion1() {
		return mQuestion1;
	}

	public void setQuestion1(String mQuestion1) {
		this.mQuestion1 = mQuestion1;
	}

	public String getQuestion2() {
		return mQuestion2;
	}

	public void setQuestion2(String question2) {
		this.mQuestion2 = question2;
	}

	public String getQuestion3() {
		return mQuestion3;
	}

	public void setQuestion3(String question3) {
		this.mQuestion3 = question3;
	}

	public String getHomePageURL() {
		return mHomePageURL;
	}

	public void setHomePageURL(String homePageURL) {
		this.mHomePageURL = homePageURL;
	}

	public String getCompany() {
		return mCompany;
	}

	public void setCompany(String company) {
		this.mCompany = company;
	}

	public String getAddress() {
		return mAddress;
	}

	public void setAddress(String address) {
		this.mAddress = address;
	}

	public String getPhone() {
		return mPhone;
	}

	public void setPhone(String phone) {
		this.mPhone = phone;
	}

	public String getFax() {
		return mFax;
	}

	public void setFax(String fax) {
		this.mFax = fax;
	}

	public String getEmail() {
		return mEmail;
	}

	public void setEmail(String email) {
		this.mEmail = email;
	}

	public String getContactName() {
		return mContactName;
	}

	public void setContactName(String contactName) {
		this.mContactName = contactName;
	}

	public boolean isContactHidden() {
		return mIsContactHidden;
	}

	public void setContactHidden(boolean isContactHidden) {
		this.mIsContactHidden = isContactHidden;
	}

	public boolean isCompanyNameHidden() {
		return mIsCompanyNameHidden;
	}

	public void setCompanyNameHidden(boolean isCompanyNameHidden) {
		this.mIsCompanyNameHidden = isCompanyNameHidden;
	}

	public boolean isEmailHidden() {
		return mIsEmailHidden;
	}

	public void setEmailHidden(boolean isEmailHidden) {
		this.mIsEmailHidden = isEmailHidden;
	}

	public boolean isPhoneHidden() {
		return mIsPhoneHidden;
	}

	public void setPhoneHidden(boolean isPhoneHidden) {
		this.mIsPhoneHidden = isPhoneHidden;
	}

	public boolean isFaxHidden() {
		return mIsFaxHidden;
	}

	public void setFaxHidden(boolean isFaxHidden) {
		this.mIsFaxHidden = isFaxHidden;
	}

	public boolean isAddressHidden() {
		return mIsAddressHidden;
	}

	public void setAddressHidden(boolean isAddressHidden) {
		this.mIsAddressHidden = isAddressHidden;
	}

	public boolean isHomePageHidden() {
		return mIsHomePageHidden;
	}

	public void setHomePageHidden(boolean isHomePageHidden) {
		this.mIsHomePageHidden = isHomePageHidden;
	}
	
	public String getJobsIeUrl() {
		return AppPool.JOBS_IE_JOB_URL + mId;
	}
	
	//parcelable stuff
		public int describeContents() 
		{
	        return 0;
	    }

	    public void writeToParcel(Parcel out, int flags) 
	    {
	    	out.writeString(this.mTitle);
	        out.writeString(this.mDescription);
	        out.writeString(this.mLocation);
	        out.writeString(this.mSalary);
	        out.writeString(this.mDateModified);
	        out.writeString(this.mDatePosted);
	        out.writeString(this.mApplicationURL);
	        out.writeString(this.mQuestion1);
	        out.writeString(this.mQuestion2);
	        out.writeString(this.mQuestion3);
	        out.writeString(this.mHomePageURL);
	        out.writeString(this.mCompany);
	        out.writeString(this.mAddress);
	        out.writeString(this.mPhone);
	        out.writeString(this.mFax);
	        out.writeString(this.mEmail);
	        out.writeString(this.mContactName);
	        out.writeByte((byte) (this.mIsContactHidden ? 1 : 0));
	        out.writeByte((byte) (this.mIsCompanyNameHidden ? 1 : 0));
	        out.writeByte((byte) (this.mIsEmailHidden ? 1 : 0));
	        out.writeByte((byte) (this.mIsPhoneHidden ? 1 : 0));
	        out.writeByte((byte) (this.mIsFaxHidden ? 1 : 0));
	        out.writeByte((byte) (this.mIsAddressHidden ? 1 : 0));
	        out.writeByte((byte) (this.mIsHomePageHidden ? 1 : 0));
	        //need Genre
	    }

	    public static final Parcelable.Creator<Job> CREATOR
	            = new Parcelable.Creator<Job>() {
	        public Job createFromParcel(Parcel in) {
	            return new Job(in);
	        }

	        public Job[] newArray(int size) {
	            return new Job[size];
	        }
	    };

	    public Job(Parcel in) {
	        this.mTitle = in.readString();
	        this.mDescription = in.readString();
	        this.mLocation = in.readString();
	        this.mSalary = in.readString();
	        this.mDateModified = in.readString();
	        this.mDatePosted = in.readString();
	        this.mApplicationURL = in.readString();
	        this.mQuestion1 = in.readString();
	        this.mQuestion2 = in.readString();
	        this.mQuestion3 = in.readString();
	        this.mHomePageURL = in.readString();
	        this.mCompany = in.readString();
	        this.mAddress = in.readString();
	        this.mPhone = in.readString();
	        this.mFax = in.readString();
	        this.mEmail = in.readString();
	        this.mContactName = in.readString();
	        this.mIsContactHidden = in.readByte() != 0;
	        this.mIsCompanyNameHidden = in.readByte() != 0;
	        this.mIsEmailHidden = in.readByte() != 0;
	        this.mIsPhoneHidden = in.readByte() != 0;
	        this.mIsFaxHidden = in.readByte() != 0;
	        this.mIsAddressHidden = in.readByte() != 0;
	        this.mIsHomePageHidden = in.readByte() != 0;
	    }
}
