package job;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class JobCategory implements KvmSerializable {
	private int mId;
	private String mName;
	
	//default constructor
	public JobCategory(){}
	
	public JobCategory(int id, String name){
		this.mId = id;
		this.mName = name;
	}
	
	//get & set
	public int getCategoryId(){
		return this.mId;
	}
	
	public String getCategoryName(){
		return this.mName;
	}
	
	@Override 
	public String toString() {
		return this.mName;
	}
	
	@Override
	public Object getProperty(int arg0) {
		switch(arg0)
	    {
	    	case 0:
	    		return this.mId;
	    	case 1:
	    		return this.mName;
	    }
	    return null;
	}

	 @Override
	 public int getPropertyCount() {
	     // TODO Auto-generated method stub
	     return 2;
	 }

	 @Override
	 public void getPropertyInfo(int arg0, Hashtable arg1, PropertyInfo info) {
		 switch(arg0){
	     	case 0:
	     		info.type = PropertyInfo.INTEGER_CLASS;
	     		info.name = "Id";
	     		break;
	     	case 1:
	     		info.type = PropertyInfo.STRING_CLASS;
	     		info.name = "Name";
	     		break;
	     	default:break;
		 }       
	 }

	 @Override
	 public void setProperty(int arg0, Object value) {
	     switch(arg0)
	     {
	     	case 0:
	     		mId = Integer.parseInt(value.toString());
	     		break;
	     	case 1:
	     		mName = value.toString();
	     		break;
	     	default :
	     		break;
	     }
	 }
}

