package com.management;
import com.management.DBConnectionManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import com.model.OutPatient;


public class outPatientManagement {

    // INSERT

    public boolean insertOutPatientList(
            List<OutPatient> list) {

        boolean flag=false;

        try {

            Connection con=
            DBConnectionManager.getConnection();
            String query="insert into outpatient values(?,?,?,?,?,?,?)";
            PreparedStatement ps=
            con.prepareStatement(query);
            for(OutPatient o:list) {

                ps.setString(1,o.getPatientId());
                ps.setString(2,o.getPatientName());
                ps.setLong(3,o.getPhoneNumber());
                ps.setInt(4,o.getAge());
                ps.setString(5,o.getGender());
                ps.setString(6,o.getMedicalHistory());
                ps.setString(7,o.getSpecialist());
                int row=ps.executeUpdate();
                if(row>0) {
                    flag=true;
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public boolean updatePhoneNumber(
            String patientId,
            long phoneNumber) {
        boolean flag=false;
        try {
            Connection con=
            DBConnectionManager.getConnection();
            String query="update outpatient set phone_number=? where patient_id=?";
            PreparedStatement ps=
            con.prepareStatement(query);
            ps.setLong(1,phoneNumber);
            ps.setString(2,patientId);
            int row=ps.executeUpdate();
            if(row>0) {
                flag=true;
            }
        }
        catch(Exception e) {

            e.printStackTrace();
        }
        return flag;
    }
    public boolean deleteOutPatient(
            String patientId) {
        boolean flag=false;

        try {
            Connection con=
            DBConnectionManager.getConnection();

            String query="delete from outpatient where patient_id=?";
            PreparedStatement ps=
            con.prepareStatement(query);
            ps.setString(1,patientId);
            int row=ps.executeUpdate();
            if(row>0) {
                flag=true;
            }
        }
        catch(Exception e) {

            e.printStackTrace();
        }

        return flag;
    }

    public void viewOutPatient(
            String patientId) {
        try {
            Connection con=
            DBConnectionManager.getConnection();
            String query="select * from outpatient where patient_id=?";
            PreparedStatement ps=
            con.prepareStatement(query);
            ps.setString(1,patientId);
            ResultSet rs=
            ps.executeQuery();
            while(rs.next()) {

                System.out.println("\nPATIENT ID : "+rs.getString(1));
                System.out.println("NAME : "+rs.getString(2));
                System.out.println("PHONE : "+rs.getLong(3));
                System.out.println("AGE : "+rs.getInt(4));
                System.out.println("GENDER : "+rs.getString(5));
                System.out.println("HISTORY : "+rs.getString(6));
                System.out.println("SPECIALIST : "+rs.getString(7));
            }
        }
        catch(Exception e) {

            e.printStackTrace();
        }
    }
}