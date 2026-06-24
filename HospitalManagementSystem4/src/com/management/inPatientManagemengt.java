package com.management;
import com.management.DBConnectionManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import com.model.InPatient;


public class inPatientManagemengt {

    public boolean insertInPatientList(List<InPatient> list){

        boolean flag=false;

        try{

            Connection con=
            DBConnectionManager.getConnection();

            String query="insert into inpatient values(?,?,?,?,?,?,?,?,?,?,?)";

            PreparedStatement ps=con.prepareStatement(query);

            for(InPatient i:list){

                ps.setString(1,i.getPatientId());
                ps.setString(2,i.getPatientName());
                ps.setLong(3,i.getPhoneNumber());
                ps.setInt(4,i.getAge());
                ps.setString(5,i.getGender());
                ps.setString(6,i.getMedicalHistory());
                ps.setString(7,i.getSpecialist());
                ps.setString(8,i.getTreatment());
                ps.setString(9,i.getRoomType());
                ps.setString(10,i.getFood());
                ps.setInt(11,i.getDays());

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
    public boolean updateRoomType(
            String patientId,
            String roomType){
        boolean flag=false;
        try{
            Connection con=
            DBConnectionManager.getConnection();
            String query="update inpatient set room_type=? where patient_id=?";
            PreparedStatement ps=
            con.prepareStatement(query);
            ps.setString(1,roomType);
            ps.setString(2,patientId);
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
    public boolean updateFoodPreference(
            String patientId,
            String food){
        boolean flag=false;
        try{
            Connection con=
            DBConnectionManager.getConnection();
            String query="update inpatient set food=? where patient_id=?";
            PreparedStatement ps=
            con.prepareStatement(query);
            ps.setString(1,food);
            ps.setString(2,patientId);
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
    public boolean updateDays(String patientId,int days){
        boolean flag=false;
        try{
            Connection con=
            DBConnectionManager.getConnection();
            String query="update inpatient set days=? where patient_id=?";
            PreparedStatement ps=
            con.prepareStatement(query);
            ps.setInt(1,days);
            ps.setString(2,patientId);
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

    public boolean deleteInPatient(String patientId){
        boolean flag=false;
        try{
            Connection con=
            DBConnectionManager.getConnection();
            String query="delete from inpatient where patient_id=?";
            PreparedStatement ps=
            con.prepareStatement(query);
            ps.setString(1,patientId);
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

    public void viewInPatient(String patientId){

        try{

            Connection con=DBConnectionManager.getConnection();

            String query="select * from inpatient where patient_id=?";
            PreparedStatement ps=con.prepareStatement(query);
            ps.setString(1,patientId);
            ResultSet rs=ps.executeQuery();
            while(rs.next()){
                System.out.println("\nPATIENT ID : "+rs.getString(1));
                System.out.println("NAME : "+rs.getString(2));
                System.out.println("PHONE : "+rs.getLong(3));
                System.out.println("AGE : "+rs.getInt(4));
                System.out.println("GENDER : "+rs.getString(5));
                System.out.println("HISTORY : "+rs.getString(6));
                System.out.println("SPECIALIST : "+rs.getString(7));
                System.out.println("TREATMENT : "+rs.getString(8));
                System.out.println("ROOM TYPE : "+rs.getString(9));
                System.out.println("FOOD : "+rs.getString(10));
                System.out.println("DAYS : "+rs.getInt(11));
            }
        }

        catch(Exception e){

            e.printStackTrace();
        }
        
    }
}