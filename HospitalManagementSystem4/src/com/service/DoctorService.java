package com.service;

import java.util.ArrayList;
import java.util.List;

import com.management.DoctorManagement;
import com.model.Doctor;
import com.model.InPatient;
import com.util.ApplicationUtil;

public class DoctorService {

    DoctorManagement management=new DoctorManagement();

    public boolean addDoctorPatient(List<Doctor> input) {
    	boolean res = management.insertDoctorList(input);
    	return res;
    }




    public Doctor getAvailableDoctor(
            String specialist,
            String date,
            String time){

        return management.getAvailableDoctor(
        specialist,
        date,
        time);
    }



    public void viewDoctor(
            String doctorId){

        management.viewDoctor(
        doctorId);
    }



    public boolean updateDoctorSpec(
            String doctorId,
            String spec){

        return management.updateDoctorSpec(
        doctorId,
        spec);
    }



    public boolean deleteDoctor(
            String doctorId){

        return management.deleteDoctor(
        doctorId);
    }
}
