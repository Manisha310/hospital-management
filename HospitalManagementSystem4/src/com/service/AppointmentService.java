package com.service;

import java.util.ArrayList;
import java.util.List;

import com.management.AppointmentManagement;
import com.model.Appointment;
import com.model.Doctor;
import com.util.ApplicationUtil;

public class AppointmentService {

    AppointmentManagement management=
    new AppointmentManagement();

    DoctorService doctorService=
    new DoctorService();

    public boolean addAppointmentPatient(List<Appointment> input) {
    	boolean res = management.insertAppointmentList(input);
    	return res;
    }
    public void viewAppointment(
            String appointmentId){
        try{
            management.viewAppointment(
            appointmentId);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public String getDoctorIdBySpec(String spec) {
    	 String doctId = management.getDoctorIdBySpecialist(spec);
    	 return doctId;
    }
   
    public boolean deleteAppointment(
            String appointmentId){

        boolean result=false;

        try{

            result=
            management.deleteAppointment(
            appointmentId);
        }

        catch(Exception e){

            e.printStackTrace();
        }

        return result;
    }
}
