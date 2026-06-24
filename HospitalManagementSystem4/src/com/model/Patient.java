package com.model;

public class Patient {
	private String  patientId;
    private String patientName;
    private long phNo;
    private int age;
    private String gender;
    private String medicalHistory;
    private String specialisation;
    
    
       public Patient(String patientId, String patientName, long phNo, int age, String gender, String medicalHistory,
			String specialisation) {
		super();
		this.patientId = patientId;
		this.patientName = patientName;
		this.phNo = phNo;
		this.age = age;
		this.gender = gender;
		this.medicalHistory = medicalHistory;
		this.specialisation = specialisation;
	}
	   public String getPatientId() {
		return patientId;
	}
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}
	public String getPatientName() {
		return patientName;
	}
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	public long getPhNo() {
		return phNo;
	}
	public void setPhNo(long phNo) {
		this.phNo = phNo;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getMedicalHistory() {
		return medicalHistory;
	}
	public void setMedicalHistory(String medicalHistory) {
		this.medicalHistory = medicalHistory;
	}
	public String getSpecialisation() {
		return specialisation;
	}
	public void setSpecialisation(String specialisation) {
		this.specialisation = specialisation;
	}
	
	   
}
