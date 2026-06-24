package com.model;

public class  Appointment{

    private String appointmentId;
    private String patientId;
    private String doctorId;
    private String specialist;
    private String appointmentDate;
    private String appointmentTime;
    private String modeOfAppiontment;
    public String getDoctorId() {
		return doctorId;
	}


	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	
    
    public String getModeOfAppiontment() {
		return modeOfAppiontment;
	}


	public void setModeOfAppiontment(String modeOfAppiontment) {
		this.modeOfAppiontment = modeOfAppiontment;
	}


	public String getAppointmentId() {

        return appointmentId;
    }

   
    public void setAppointmentId(
            String appointmentId) {

        this.appointmentId=appointmentId;
    }

    public String getPatientId() {

        return patientId;
    }

    public void setPatientId(
            String patientId) {

        this.patientId=patientId;
    }

    public String getSpecialist() {

        return specialist;
    }

    public void setSpecialist(
            String specialist) {

        this.specialist=specialist;
    }
    
    
    public String getAppointmentDate() {

        return appointmentDate;
    }
    public void setAppointmentDate(
            String appointmentDate) {

        this.appointmentDate=appointmentDate;
    }

    public String getAppointmentTime() {

        return appointmentTime;
    }

    public void setAppointmentTime(
            String appointmentTime) {

        this.appointmentTime=appointmentTime;
    }
}