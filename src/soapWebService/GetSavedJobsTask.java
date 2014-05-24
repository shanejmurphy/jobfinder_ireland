package soapWebService;

import helpers.LogHelper;
import ie.jobfinder.app.FindJobActivity;
import ie.jobfinder.app.interfaces.OnSearchSelectedListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import ad.JobAd;
import android.os.AsyncTask;
import android.util.Xml;

public class GetSavedJobsTask extends AsyncTask<InputStream, Void, ArrayList<JobAd>>{
	private static final String TAG = "GetSavedJobsTask";
	//private Find context;
	
    private OnSearchSelectedListener listener;
	// We don't use namespaces
    private static final String NS = null;
    
    public GetSavedJobsTask() {
    }

    public GetSavedJobsTask(FindJobActivity act) {
        this.listener = act;
    }
    

	@Override
	protected ArrayList<JobAd> doInBackground(InputStream... params) {
		ArrayList<JobAd> adList = null;
		try {
			adList = parse(params[0]);
		} catch(XmlPullParserException e) {
			LogHelper.logDebug(TAG, "XmlPullParserException: " + e.toString());
		} catch(Exception e) {
			LogHelper.logDebug(TAG, e.toString());
		}
		return adList;
	}
	
	@Override
	protected void onPostExecute(ArrayList<JobAd> ads){
		//try/catch required because this is throwing a nullpointerexception
		try{
			this.listener.onJobAdsResult(ads);
		} catch(Exception e) {
			LogHelper.logDebug(TAG, "Exception: " + e.toString());
		}
	}
	
	public ArrayList<JobAd> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }
	
	private ArrayList<JobAd> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
		ArrayList<JobAd> adList = new ArrayList<JobAd>();

	    //parser.require(XmlPullParser.START_TAG, NS, "Job");
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
	        // Starts by looking for the entry tag
	        if (name.equals("Job")) {
	        	adList.add(readJob(parser));
	        } else {
	            skip(parser);
	        }
	    }  
	    return adList;
	}
	
	// Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
	// to their respective "read" methods for processing. Otherwise, skips the tag.
	private JobAd readJob(XmlPullParser parser) throws XmlPullParserException, IOException {
	    parser.require(XmlPullParser.START_TAG, NS, "Job");
	    JobAd ad = new JobAd();
	    int id = 0;
	    String title = null;
	    String date = null;
	    String company = null;
	    String location = null;
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
	        if(name.equals("id")) {
	        	id = readId(parser);
	        	ad.setJobId(id);
	        } else if (name.equals("title")) {
	            title = readTitle(parser);
	            ad.setTitle(title);
	        } else if (name.equals("date")) {
	            date = readDate(parser);
	            ad.setDateCreated(date);
	        } else if (name.equals("company")) {
	            company = readCompany(parser);
	            ad.setCompany(company);
	        } else if (name.equals("location")) {
	            location = readLocation(parser);
	            ad.setLocation(location);
	        } else {
	            skip(parser);
	        }
	    }
	    return ad;
	}

	// Processes title tags in the feed.
	private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
	    parser.require(XmlPullParser.START_TAG, NS, "title");
	    String title = readText(parser);
	    parser.require(XmlPullParser.END_TAG, NS, "title");
	    return title;
	}
	
	// Processes title tags in the feed.
	private String readCompany(XmlPullParser parser) throws IOException, XmlPullParserException {
	    parser.require(XmlPullParser.START_TAG, NS, "company");
	    String company = readText(parser);
	    parser.require(XmlPullParser.END_TAG, NS, "company");
	    return company;
	}
	// Processes title tags in the feed.
	private String readDate(XmlPullParser parser) throws IOException, XmlPullParserException {
	    parser.require(XmlPullParser.START_TAG, NS, "date");
	    String date = readText(parser);
	    parser.require(XmlPullParser.END_TAG, NS, "date");
	    return date;
	}
  
	// Processes title tags in the feed.
	private String readLocation(XmlPullParser parser) throws IOException, XmlPullParserException {
	    parser.require(XmlPullParser.START_TAG, NS, "location");
	    String location = readText(parser);
	    parser.require(XmlPullParser.END_TAG, NS, "location");
	    return location;
	}	

	// Processes summary tags in the feed.
	private int readId(XmlPullParser parser) throws IOException, XmlPullParserException {
	    parser.require(XmlPullParser.START_TAG, NS, "id");
	    int id = Integer.parseInt(readText(parser));
	    parser.require(XmlPullParser.END_TAG, NS, "id");
	    return id;
	}

	// For the tags title and summary, extracts their text values.
	private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
	    String result = "";
	    if (parser.next() == XmlPullParser.TEXT) {
	        result = parser.getText();
	        parser.nextTag();
	    }
	    return result;
	}
	
	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
	    if (parser.getEventType() != XmlPullParser.START_TAG) {
	        throw new IllegalStateException();
	    }
	    int depth = 1;
	    while (depth != 0) {
	        switch (parser.next()) {
	        case XmlPullParser.END_TAG:
	            depth--;
	            break;
	        case XmlPullParser.START_TAG:
	            depth++;
	            break;
	        }
	    }
	 }
}
