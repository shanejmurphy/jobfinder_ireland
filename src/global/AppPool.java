package global;


/**
 * @author Shane
 * @version 1
 * @since 1.0 - 03/03/2014
 * 
 * Class used to store variables that will be used throughout the application.
 */
public class AppPool {

	private static String packageName;
	private static final String APP_NAME = "Job Finder Ireland";
	
	public static final String TEST_DEVICE_ID = "SH26LW103187";
	public static final String JOB_ID = "ID";
	public static final String JOB = "Job";
	
	public static final String WSDL_TARGET_NAMESPACE = "http://www.jobs.ie/webservices/";
	public static final String SOAP_ADDRESS = "http://www.jobs.ie/JobWS.asmx";
	public static final String JOBS_IE_JOB_URL = "http://www.jobs.ie/ApplyForJob.aspx?Id=";
	public static final String EMAIL_SUBJECT = "New Job Opportunity!";
	
	public static final String SAVED_JOBS_FILENAME = "saved_jobs.xml";
	
	private static final String DOWNLOAD_MSG = "Download App from Play Store";
	public static final String APP_URL = "https://play.google.com/store/apps/details?id=ie.jobfinder.app";
	
	public static final int MAX_SEARCH_SIZE = 3;
	
	
	// ===========================================================
	// Constructors
	// ===========================================================
	private AppPool() {
	}

	// ===========================================================
	// Getters / Setters
	// ===========================================================
	public static String getPackageName() {
		return packageName;
	}

	public static void setPackageName(String packageName) {
		AppPool.packageName = packageName;
	}
	
	public static String getAppInfoForShare() {
		return " via " + APP_NAME+ ".\n\n" + DOWNLOAD_MSG + " - " + APP_URL;
	}
	
}
