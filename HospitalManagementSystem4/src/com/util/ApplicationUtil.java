package com.util;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import com.management.DBConnectionManager;
import com.model.Appointment;
import com.model.Doctor;
import com.model.InPatient;
import com.model.OutPatient;
import java.io.*;
public class ApplicationUtil {
	
	Appointment ap= new Appointment();

    public String generatePatientId() {
    	String  id =null;
    	try {
	    	FileReader   fr = new FileReader("src/com/util/patientidcount.txt");
	    	BufferedReader br = new BufferedReader(fr);
	    String ids = br.readLine();
	    int newid = Integer.parseInt(ids);
	    newid = newid+1;
	    id="PT"+newid;
	    String lastId=String.valueOf(newid);
	    lastPatientId(lastId);
	           br.close();
	           
	           
	    }

	    catch(Exception e) {
	    	       e.printStackTrace();
	    	       
	    }
    	return id;
    }

    
    public void lastPatientId(String id) {
    	try {
    		 FileWriter f = new FileWriter("src/com/util/patientidcount.txt");
      	  BufferedWriter b = new BufferedWriter(f);
      	  b.write(id);
      	  b.close();
    	    }
    	catch(Exception e) {
    		e.printStackTrace();
    	}
    }

    public String generateAppointmentId() {

        String id=null;

        try {

            FileReader fr=new FileReader("src/com/util/appointmentidcount.txt");
            BufferedReader br=new BufferedReader(fr);
            String ids=br.readLine();
            int newid=Integer.parseInt(ids);
            newid=newid+1;
            id="APT"+newid;
            String lastId=
            String.valueOf(newid);
            lastAppointmentId(lastId);
            br.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return id;
    }
    public void lastAppointmentId(
            String id) {
        try {

            FileWriter f=new FileWriter("src/com/util/appointmentidcount.txt");
            BufferedWriter b=new BufferedWriter(f);
            b.write(id);
            b.close();
        }
        catch(Exception e) {

            e.printStackTrace();
        }
    }
    
   public String generateDoctorId() {

        String id=null;

        try {

            FileReader fr=
            new FileReader(
            "src/com/util/doctoridcount.txt");

            BufferedReader br=
            new BufferedReader(fr);

            String ids=
            br.readLine();

            int newid=
            Integer.parseInt(ids);

            newid=newid+1;

            id="DR"+newid;

            String lastId=
            String.valueOf(newid);

            lastDoctorId(lastId);

            br.close();
        }

        catch(Exception e) {

            e.printStackTrace();
        }

        return id;
    }
    public void lastDoctorId(
            String id) {

        try {

            FileWriter f=
            new FileWriter(
            "src/com/util/doctoridcount.txt");

            BufferedWriter b=
            new BufferedWriter(f);

            b.write(id);

            b.close();
        }

        catch(Exception e) {

            e.printStackTrace();
        }
    }
    
    public List<InPatient> parsePatientDetails(List<String> inputList,String roomtype){

        List<InPatient> resultList = new ArrayList<>();
        
        try{
            
            for(String s:inputList){
                String data[]=s.split(":");
                InPatient i=new InPatient();
                i.setPatientId(generatePatientId());
                i.setPatientName(data[0]);
                i.setPhoneNumber(Long.parseLong(data[1]));
                i.setAge(Integer.parseInt(data[2]));
                i.setGender(data[3]);
                i.setMedicalHistory(data[4]);
                i.setSpecialist(data[5]);
                i.setTreatment(data[6]);
                i.setRoomType(roomtype);
                i.setDays(Integer.parseInt(data[7]));
                resultList.add(i);
            }
//            return resultList;

        }

        catch(Exception e){

            e.printStackTrace();
        }

        return resultList;
    }
    public List<OutPatient> parseOutPatientDetails(List<String> outputList) {

        List<OutPatient> list=new ArrayList<>();
       

        for(String s:outputList) {

            String arr[]=s.split(":");

            OutPatient o=new OutPatient();

            o.setPatientId(generatePatientId());

            o.setPatientName(arr[0]);
            o.setPhoneNumber(Long.parseLong(arr[1]));
            o.setAge(Integer.parseInt(arr[2]));
            o.setGender(arr[3]);
            o.setMedicalHistory(arr[4]);
            o.setSpecialist(arr[5]);

            list.add(o);
        }

        return list;
    }
    public List<Doctor> parseDoctorsDetails(List<String> input) {

        List<Doctor> list=new ArrayList<>();
        for(String s:input) {
            String arr[]=s.split(":");
            Doctor dt = new Doctor();
            dt.setDoctorId(generateDoctorId());
            dt.setDoctorName(arr[0]);
            dt.setSpecialization(arr[1]);
            dt.setAvailableDate(arr[2]);
            dt.setAvailableTime(arr[3]);
            

            list.add(dt);
        }

        return list;
        
    }
    
    public List<Appointment> parseAppointmentDetails(List<String> input,String spec,String dctid) {

        List<Appointment> list=new ArrayList<>();
       

        for(String s:input) {

            String arr[]=s.split(":");

            Appointment ap = new Appointment();

            ap.setAppointmentId(generateAppointmentId());
            ap.setPatientId(arr[0]);
            ap.setDoctorId(dctid);
            ap.setSpecialist(spec);
            ap.setAppointmentDate(arr[1]);
            ap.setAppointmentTime(arr[2]);
            ap. setModeOfAppiontment(arr[3]);
            list.add(ap);
            
        }

        return list;
        
    }

        public  boolean isValidPatientId(String patientId) {
            try {
                Connection con =DBConnectionManager.getConnection();

                String query = "SELECT patient_id FROM outpatient WHERE patient_id = ?";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1, patientId);

                ResultSet rs = ps.executeQuery();

                return rs.next(); // true = exists, false = invalid

            } catch (Exception e) {
                System.out.println(e);
            }
            return false;
        }
    public  boolean isDoctorAvailable( String date, String time) {
        try {
            Connection con =DBConnectionManager.getConnection();

            String query = "SELECT * FROM doctor WHERE  availableDate=? AND availableTime=?";
            PreparedStatement ps = con.prepareStatement(query);

//            ps.setString(1, specialization);
            ps.setString(1, date);
            ps.setString(2, time);

            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }
    

}