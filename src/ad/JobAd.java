package ad;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import org.xmlpull.v1.XmlSerializer;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Xml;

public class JobAd implements Parcelable {
	private int mJobId;
	private String mDateCreated;
	private String mTitle;
	private String mCompany;
	private String mLocation;
	private int mEmployerId;
	private boolean isSaved =false;
	
	//construcotr
	public JobAd(){
		
	}
	
	//getters & setters
	public int getJobId() {
		return mJobId;
	}
	public void setJobId(int mJobId) {
		this.mJobId = mJobId;
	}
	public String getDateCreated() {
		return mDateCreated;
	}
	public String getDateCreatedAsString() { //return date in string format as - 2014-01-31
		SimpleDateFormat outputFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		return outputFormatter.format(mDateCreated);
	}
	public void setDateCreated(String mDateCreated) {
		this.mDateCreated = mDateCreated;
	}
	public String getTitle() {
		return mTitle;
	}
	public void setTitle(String mTitle) {
		this.mTitle = mTitle;
	}
	public String getCompany() {
		return mCompany;
	}
	public void setCompany(String mCompany) {
		this.mCompany = mCompany;
	}
	public String getLocation() {
		return mLocation;
	}
	public void setLocation(String mLocation) {
		this.mLocation = mLocation;
	}
	public int getEmployerId() {
		return mEmployerId;
	}
	public void setEmployerId(int mEmployerId) {
		this.mEmployerId = mEmployerId;
	}
	
	public boolean isSaved() {
		return isSaved;
	}

	public void setSaved(boolean isSaved) {
		this.isSaved = isSaved;
	}
	
	public String toXmlString() {
		XmlSerializer serializer = Xml.newSerializer();
	    StringWriter writer = new StringWriter();
	    try {
	        serializer.setOutput(writer);
	        serializer.startDocument("UTF-8", true);
	        serializer.startTag("", "JobAd");
	        serializer.startTag("", "Job");
	        //serializer.attribute("", "number", String.valueOf(messages.size()));
	        serializer.startTag("", "id");
	        serializer.text(Integer.toString(this.mJobId));
	        serializer.endTag("", "id");
            serializer.startTag("", "title");
            serializer.text(this.mTitle);
            serializer.endTag("", "title");
            serializer.startTag("", "date");
            serializer.text(this.mDateCreated);
            serializer.endTag("", "date");
            serializer.startTag("", "company");
            serializer.text(this.mCompany);
            serializer.endTag("", "company");
            serializer.startTag("", "location");
            serializer.text(this.mLocation);
            serializer.endTag("", "location");
	        serializer.endTag("", "Job");
	        serializer.endTag("", "JobAd");
	        serializer.endDocument();
	        return writer.toString();
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    } 
	}
	
	public String toXmlString(ArrayList<JobAd> adList) {
		XmlSerializer serializer = Xml.newSerializer();
	    StringWriter writer = new StringWriter();
	    try {
	        serializer.setOutput(writer);
	        serializer.startDocument("UTF-8", true);
	        serializer.startTag("", "JobAd");
	        for(JobAd j: adList) {
		        serializer.startTag("", "Job");
		        //serializer.attribute("", "number", String.valueOf(messages.size()));
		        serializer.startTag("", "id");
		        serializer.text(Integer.toString(j.getJobId()));
		        serializer.endTag("", "id");
	            serializer.startTag("", "title");
	            serializer.text(j.getTitle());
	            serializer.endTag("", "title");
	            serializer.startTag("", "date");
	            serializer.text(j.getDateCreated());
	            serializer.endTag("", "date");
	            serializer.startTag("", "company");
	            serializer.text(j.getCompany());
	            serializer.endTag("", "company");
	            serializer.startTag("", "location");
	            serializer.text(j.getLocation());
	            serializer.endTag("", "location");
		        serializer.endTag("", "Job");
	        }
	        serializer.endTag("", "JobAd");
	        serializer.endDocument();
	        return writer.toString();
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    } 
	}
	
	@Override
	public String toString() {
		return "JobAd [JobId=" + mJobId + ", DateCreated=" + mDateCreated
				+ ", Title=" + mTitle + ", Company=" + mCompany
				+ ", Location=" + mLocation + ", EmployerId=" + mEmployerId
				+ "]";
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel out, int flags) 
    {
    	out.writeInt(this.mJobId);
        out.writeString(this.mTitle);
        out.writeString(this.mCompany);
        out.writeString(this.mLocation);
        out.writeString(this.mDateCreated);
        out.writeInt(this.mEmployerId);
    }

    public static final Parcelable.Creator<JobAd> CREATOR
            = new Parcelable.Creator<JobAd>() {
        public JobAd createFromParcel(Parcel in) {
            return new JobAd(in);
        }

        public JobAd[] newArray(int size) {
            return new JobAd[size];
        }
    };

    public JobAd(Parcel in) {
        this.mJobId = in.readInt();
        this.mTitle = in.readString();
        this.mCompany = in.readString();
        this.mLocation = in.readString();
        this.mDateCreated = in.readString();
        this.mEmployerId = in.readInt();
    }
	
	
	/*
	private Date mDateModified;
	private Job mJob;
	private String mApplicationUrl;
	private ArrayList<String> mQuestions;*/
	
	
	/*public Date getDateCreated() {
		return mDateCreated;
	}
	public void setDateCreated(Date mDateCreated) {
		this.mDateCreated = mDateCreated;
	}
	public Date getDateModified() {
		return mDateModified;
	}
	public void setDateModified(Date mDateModified) {
		this.mDateModified = mDateModified;
	}
	public Job getJob() {
		return mJob;
	}
	public void setJob(Job mJob) {
		this.mJob = mJob;
	}
	public String getApplicationUrl() {
		return mApplicationUrl;
	}
	public void setApplicationUrl(String mApplicationUrl) {
		this.mApplicationUrl = mApplicationUrl;
	}
	public ArrayList<String> getQuestions() {
		return mQuestions;
	}
	public void setQuestions(ArrayList<String> mQuestions) {
		this.mQuestions = mQuestions;
	}*/
}
