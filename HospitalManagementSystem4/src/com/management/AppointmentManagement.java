package com.management;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import com.model.Appointment;
import com.management.DBConnectionManager;

public class AppointmentManagement {

    public boolean insertAppointmentList(
            List<Appointment> list){

        boolean flag=false;

        try{

            Connection con=
            DBConnectionManager.getConnection();

            String query=
            "insert into appointment values(?,?,?,?,?,?,?)";

            PreparedStatement ps=
            con.prepareStatement(query);

            for(Appointment a:list){

                ps.setString(1,a.getAppointmentId());
                ps.setString(2,a.getPatientId());
                ps.setString(3,a.getDoctorId());               
                ps.setString(4,a.getSpecialist());
                ps.setString(5,a.getAppointmentDate());
                ps.setString(6,a.getAppointmentTime());
                ps.setString(7,a.getModeOfAppiontment());
                int row=ps.executeUpdate();
                if(row>0){

                    flag=true;
                }
            }
        }

        catch(Exception e){

            e.printStackTrace();
        }

        return flag;
    }


        public String getDoctorIdBySpecialist(String specialization) {

            String doctorId = null;

            try {
                Connection con = DBConnectionManager.getConnection();
                String query = "SELECT doctor_id FROM doctor WHERE specialization = ?";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1, specialization);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    doctorId = rs.getString("doctor_id");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return doctorId;
        }
    
    public void viewAppointment(
            String appointmentId){

        try{

            Connection con=DBConnectionManager.getConnection();
            String query="select * from appointment where appointment_id=?";
            PreparedStatement ps=con.prepareStatement(query);
            ps.setString(1,appointmentId);
            ResultSet rs=ps.executeQuery();
            while(rs.next()){
                System.out.println("\nAPPOINTMENT ID : "+rs.getString(1));
                System.out.println("PATIENT ID : "+rs.getString(2));
                System.out.println("SPECIALIST : "+rs.getString(3));
                System.out.println("DOCTOR NAME : "+rs.getString(4));
                System.out.println("CONSULTATION FEE : "+rs.getDouble(5));
                System.out.println("DATE : "+rs.getString(6));
                System.out.println("TIME : "+rs.getString(7));
            }
        }
        catch(Exception e){

            e.printStackTrace();
        }
    }
    public boolean deleteAppointment(
            String appointmentId){
        boolean flag=false;

        try{

            Connection con=DBConnectionManager.getConnection();
            String query="delete from appointment where appointment_id=?";
            PreparedStatement ps=con.prepareStatement(query);
            ps.setString(1,appointmentId);
            int row=ps.executeUpdate();

            if(row>0){

                flag=true;
            }
        }
        catch(Exception e){

            e.printStackTrace();
        }

        return flag;
    }
}
