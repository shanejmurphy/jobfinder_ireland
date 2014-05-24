package job;

public class JobLocation {
	private int mId;
	private String mName;
	
	//default constructor
	public JobLocation(){}
	
	public JobLocation(int id, String name){
		this.mId = id;
		this.mName = name;
	}
	
	//get & set
	public int getLocationId(){
		return this.mId;
	}
	
	public String getLocationName(){
		return this.mName;
	}
}
