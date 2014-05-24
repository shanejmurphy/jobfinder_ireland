package ad;

import java.util.Date;

public abstract class Ad {
	private Date mDateCreated;
	private Date mDateModified;
	private Contact mContact;
	
	public Date getDateCreated() {
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
	public Contact getContact() {
		return mContact;
	}
	public void setContact(Contact mContact) {
		this.mContact = mContact;
	}
}
