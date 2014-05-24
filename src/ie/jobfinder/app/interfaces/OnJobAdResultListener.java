package ie.jobfinder.app.interfaces;

import java.util.ArrayList;

import ad.JobAd;

public interface OnJobAdResultListener {
	public void doCallBack(ArrayList<JobAd> jobAds);
}
