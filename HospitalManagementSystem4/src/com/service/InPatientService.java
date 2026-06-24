package com.service;
//package com.management;
import com.management.DBConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.management.inPatientManagemengt;
import com.model.InPatient;
import com.util.ApplicationUtil;

public class InPatientService {

    inPatientManagemengt management=new inPatientManagemengt();

 
    public boolean addInPatient(List<InPatient> input) {
    	boolean res = management.insertInPatientList(input);
    	return res;
    }

//    public int assignRoom(String roomType) {
//        try {
//            Connection con = DBConnectionManager.getConnection();
//            int start = 0;
//            int end = 0;
//            if(roomType.equalsIgnoreCase("AC")) {
//                start = 100;
//                end = 200;
//            }
//            else if(roomType.equalsIgnoreCase("NONAC")) {
//                start = 1;
//                end = 99;
//            }
//            else {
//                return -1;
//            }
//            for(int room = start; room <= end; room++) {
//                String query = "SELECT room_number FROM inpatient WHERE room_number = ?";
//                PreparedStatement ps = con.prepareStatement(query);
//                ps.setInt(1, room);
//                ResultSet rs = ps.executeQuery();
//                if(!rs.next()) {
//                    return room;
//                }
//            }
//        }
//        catch(Exception e) {
//            e.printStackTrace();
//        }
//        return 0;
//    }

   
    
    public boolean deleteInPatient(String patientId) {
        boolean result = false;
        try {
            result = management.deleteInPatient(patientId);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    public void viewInPatient(String patientId) {
        try {
            management.viewInPatient(patientId);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
 

    public boolean updateFoodPreference(
            String patientId,
            String food){

        boolean result=false;

        try{

            result=
            management.updateFoodPreference(patientId,
            food);
        }

        catch(Exception e){

            e.printStackTrace();
        }

        return result;
    }
    public boolean updateRoomType(
            String patientId,
            String roomType){

        boolean result=false;

        try{

            result=
            management.updateRoomType(
            patientId,
            roomType);
        }

        catch(Exception e){

            e.printStackTrace();
        }

        return result;
    }

    public boolean updateDays(
            String patientId,
            int days){

        boolean result=false;

        try{

            result=
            management.updateDays(
            patientId,
            days);
        }

        catch(Exception e){

            e.printStackTrace();
        }

        return result;
    }

    

    
    public double calculateBill(
            String roomType,
            int days,
            double medicineFee,
            double admissionFee){

        double roomCharge=0;

        if(roomType.equalsIgnoreCase("AC")){

            roomCharge=3000;
        }

        else if(roomType.equalsIgnoreCase("NONAC")){

            roomCharge=1500;
        }

        else if(roomType.equalsIgnoreCase("ICU")){

            roomCharge=7000;
        }

        double total=
        admissionFee+
        medicineFee+
        (roomCharge*days);

        return total;
    }
}