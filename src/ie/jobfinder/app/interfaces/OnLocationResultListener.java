package ie.jobfinder.app.interfaces;

import java.util.ArrayList;

import job.JobLocation;

public interface OnLocationResultListener {
	public void onLocationResult(ArrayList<JobLocation> location);
}
