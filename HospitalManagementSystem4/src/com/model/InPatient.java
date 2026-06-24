package com.model;

public class InPatient {

    private String patientId;
    private String patientName;
    private long phoneNumber;
    private int age;
    private String gender;
    private String medicalHistory;
    private String specialist;
    private String treatment;
    private String roomType;
    private String food;
    private int days;
    
    

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId=patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName=patientName;
    }

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber=phoneNumber;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age=age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender=gender;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory=medicalHistory;
    }

    public String getSpecialist() {
        return specialist;
    }

    public void setSpecialist(String specialist) {
        this.specialist=specialist;
    }

    
    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment=treatment;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType=roomType;
    }
    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }
}