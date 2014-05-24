package ie.jobfinder.app.job_application;

public class JobApplication {

	private int jobId;
	private String coverNote;
	private String name;
	private String phone;
	private String email;
	private String[] answers;
	private String cvFileName;
	private String cvFileType;
	private byte[] cvFileData;
	
	
	public int getJobId() {
		return jobId;
	}
	public void setJobId(int jobId) {
		this.jobId = jobId;
	}
	
	public String getCoverNote() {
		return coverNote;
	}
	public void setCoverNote(String coverNote) {
		this.coverNote = coverNote;
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	
	public String[] getAnswers() {
		return answers;
	}
	public void setAnswers(String[] answers) {
		this.answers = answers;
	}
	
	
	public String getCvFileName() {
		return cvFileName;
	}
	public void setCvFileName(String cvFileName) {
		this.cvFileName = cvFileName;
	}
	
	
	public String getCvFileType() {
		return cvFileType;
	}
	public void setCvFileType(String cvFileType) {
		this.cvFileType = cvFileType;
	}
	
	
	public byte[] getCvFileData() {
		return cvFileData;
	}
	public void setCvFileData(byte[] fileData) {
		this.cvFileData = fileData;
	}
}
