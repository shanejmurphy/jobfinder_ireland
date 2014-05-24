package ad;

public class Contact {
	private String mName;
	private String mAddress;
	private String mPhone;
	private String mEmail;
	private String mFax;
	private String mWeb;
	
	//set & get
	public void setContactName(String name){
		this.mName = name;
	}
	
	public String getName() {
		return mName;
	}
	
	public void setContactAddress(String address){
		this.mAddress = address;
	}
	
	public String getAddress() {
		return mAddress;
	}
	
	public void setContactPhone(String phone){
		this.mPhone = phone;
	}
	
	public String getPhone() {
		return mPhone;
	}
	
	public void setContactEmail(String email){
		this.mEmail = email;
	}
	
	public String getEmail() {
		return mEmail;
	}
	
	public void setContactFax(String fax){
		this.mFax = fax;
	}
	
	public String getFax() {
		return mFax;
	}
	
	public void setContactWeb(String web){
		this.mWeb = web;
	}
	
	public String getWeb() {
		return mWeb;
	}
}
