package com.management;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import com.model.Doctor;
import com.management.DBConnectionManager;

public class DoctorManagement {

    public boolean insertDoctorList(List<Doctor> list){

        boolean flag=false;

        try{
            Connection con=DBConnectionManager.getConnection();
            String query="insert into doctor values(?,?,?,?,?)";
            PreparedStatement ps=con.prepareStatement(query);
            for(Doctor d:list){
                ps.setString(1,d.getDoctorId());
                ps.setString(2,d.getDoctorName());
                ps.setString(3,d.getSpecialization());
                ps.setString(4,d.getAvailableDate());
                ps.setString(5,d.getAvailableTime());
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
    public Doctor getAvailableDoctor(String specialist,String date, String time){

        Doctor d=null;

        try{

            Connection con=
            DBConnectionManager.getConnection();
            String query="select * from doctor where specialization=? and available_date=? and available_time=? limit 1";
            PreparedStatement ps=con.prepareStatement(query);
            ps.setString(1,specialist);
            ps.setString(2,date);
            ps.setString(3,time);
            ResultSet rs=ps.executeQuery();

            if(rs.next()){
                d=new Doctor();
                d.setDoctorId(rs.getString(1));
                d.setDoctorName(rs.getString(2));
                d.setSpecialization( rs.getString(3));
                d.setAvailableDate(rs.getString(4));
                d.setAvailableTime( rs.getString(5));
            }
        }
        catch(Exception e){

            e.printStackTrace();
        }
        return d;
    }
    public void viewDoctor(
            String doctorId){
        try{
            Connection con=DBConnectionManager.getConnection();
            String query="select * from doctor where doctor_id=?";
            PreparedStatement ps=con.prepareStatement(query);
            ps.setString(1,doctorId);
            ResultSet rs=ps.executeQuery();
            while(rs.next()){
                System.out.println("\nDOCTOR ID : "+rs.getString(1));
                System.out.println("DOCTOR NAME : "+rs.getString(2));
                System.out.println("SPECIALIZATION : "+rs.getString(3));
                System.out.println("AVAILABLE DATE : "+rs.getString(4));
                System.out.println("AVAILABLE TIME : "+rs.getString(5));
            }
        }
        catch(Exception e){

            e.printStackTrace();
        }
    }
 
    public boolean updateDoctorSpec(
            String doctorId,
            String spec){

        boolean flag=false;

        try{
            Connection con= DBConnectionManager.getConnection();
            String query="update doctor set specialization =? where doctor_id=?";
            PreparedStatement ps=con.prepareStatement(query);
            ps.setString(1,spec);
            ps.setString(2,doctorId);
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

    public boolean deleteDoctor(String doctorId){
        boolean flag=false;
        try{
            Connection con=
            DBConnectionManager.getConnection();

            String query=
            "delete from doctor where doctor_id=?";

            PreparedStatement ps=
            con.prepareStatement(query);

            ps.setString(1,
            doctorId);

            int row=
            ps.executeUpdate();

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
