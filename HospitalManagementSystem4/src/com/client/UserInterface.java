package com.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.model.InPatient;
import com.model.OutPatient;
import com.model.Appointment;
import com.model.Doctor;
import com.service.AllocationService;
import com.service.AppointmentService;
import com.service.DoctorService;
import com.service.InPatientService;
import com.service.OutPatientService;
import com.service.PaymentService;
import com.util.ApplicationUtil;

public class UserInterface {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        OutPatientService outService = new OutPatientService();
        InPatientService inService = new InPatientService();
        DoctorService doctorService = new DoctorService();
        ApplicationUtil au = new ApplicationUtil ();
        AppointmentService appointmentService = new AppointmentService();
        Appointment ap = new Appointment();
        AllocationService allocationService = new AllocationService();
        PaymentService paymentService = new PaymentService();

        while(true) {

            System.out.println("\n");
            System.out.println("                   WELCOME  APOLLO HOSPITAL MANAGEMENT SYSTEM------------>");
            System.out.println("1. OUTPATIENT MODULE");
            System.out.println("2. INPATIENT MODULE");
            System.out.println("3. DOCTOR MODULE");
            System.out.println("4. APPOINTMENT MODULE");
            System.out.println("5. ALLOCATION MODULE");
            System.out.println("6. PAYMENT MODULE");
            System.out.println("7. EXIT");
            System.out.println("------------------------->");
            System.out.print("ENTER YOUR CHOICE : ");
            int mainChoice = sc.nextInt(); 
            sc.nextLine();
            switch(mainChoice) {
            case 1:
                while(true) {
                    System.out.println("\n");
                    System.out.println("              OUTPATIENT MODULE                 ");
                    System.out.println("1. ADD OUTPATIENT");
                    System.out.println("2. UPDATE PHONE NUMBER");
                    System.out.println("3. DELETE OUTPATIENT");
                    System.out.println("4. VIEW OUTPATIENT");
                    System.out.println("5. BACK");
                    System.out.println("---------------------->");
                    System.out.print("ENTER YOUR CHOICE : ");
                    int outChoice = sc.nextInt();
                    sc.nextLine();
                    switch(outChoice) {
                    case 1:
                        System.out.println("\nENTER OUTPATIENT DETAILS");
                        System.out.println("FORMAT : ");
                        System.out.println("name:phone:age:gender:history:specialist");           
                        System.out.print("\nENTER DETAILS : ");
                        String outInput =sc.nextLine();
                        List<String> outList =new ArrayList<>();
                        outList.add(outInput);
                       List<OutPatient> outResult =au.parseOutPatientDetails(outList);
                       boolean outRes=outService.addOutPatient(outResult);
                        if(outRes) {
                            System.out.println("\nOUTPATIENT ADDED SUCCESSFULLY");
                        }
                        else {
                            System.out.println("\nINSERTION FAILED");
                        }
                        break;
                    case 2:
                        System.out.print("\nENTER PATIENT ID : ");
                        String updateId =sc.nextLine();
                        System.out.print("ENTER NEW PHONE NUMBER : ");
                        long phone =sc.nextLong();
                        boolean updateResult = outService.updateOutPatientPhoneNumber(updateId,phone);
                        if(updateResult) {
                            System.out.println("\nPHONE UPDATED SUCCESSFULLY");
                        }
                        else {
                            System.out.println("\nUPDATE FAILED");
                        }
                        break;
                    case 3:
                        System.out.print("\nENTER PATIENT ID : ");
                        String deleteId =sc.nextLine();
                        boolean deleteResult = outService.deleteOutPatient(deleteId);
                        if(deleteResult) {
                            System.out.println("\nDELETED SUCCESSFULLY");
                        }
                        else {
                            System.out.println("\nDELETE FAILED");
                        }
                        break;
                    case 4:
                        System.out.print("\nENTER PATIENT ID : ");
                        String viewId =sc.nextLine();
                        outService.viewOutPatient(viewId);
                        break;
                    case 5:
                        break;
                    default:
                        System.out.println("\nINVALID CHOICE");
                    }
                    if(outChoice == 5) {
                        break;
                    }
                }
                break;
            case 2:

                while(true) {

                    System.out.println("\n");
                    System.out.println("               INPATIENT MODULE--------------->");
                    System.out.println("1. ADD INPATIENT");
//                    System.out.println("2. UPDATE ROOM TYPE");
                    System.out.println("2. UPDATE FOOD PREFERENCE");
                    System.out.println("3. DELETE INPATIENT");
                    System.out.println("4. VIEW INPATIENT");
                    System.out.println("5. BACK");
                    System.out.println("------------------->");
                    System.out.print("ENTER YOUR CHOICE : ");
                    int inChoice = sc.nextInt();
                    sc.nextLine();
                    switch(inChoice) {
                    case 1:
                        System.out.println("\nENTER INPATIENT DETAILS");
                        System.out.println("FORMAT : ");
                        System.out.println("name:phone:age:gender:history:specialist:treatment:days");
                        System.out.print("\nENTER DETAILS : ");
                        String inInput = sc.nextLine();
                        String roomType = "";
                        while(true) {
                            System.out.print("ENTER ROOM TYPE (AC / NONAC) : ");
                            roomType = sc.nextLine().toUpperCase();
                            if(roomType.equals("AC") || roomType.equals("NONAC")) {
                                break;
                            }
                            else {
                                System.out.println("INVALID ROOM TYPE. PLEASE ENTER AC OR NONAC.");
                            }
                        }
//                        int assignedRoom = inService.assignRoom(roomType);
//                        if(assignedRoom == 0) {
//                            System.out.println("NO ROOMS AVAILABLE FOR " + roomType);
//                            break;
//                        }
//                        System.out.println("ROOM NUMBER ASSIGNED : " + assignedRoom);
                        String food = "";
                        while(true) {
                            System.out.print("DO YOU WANT FOOD? (YES / NO) : ");
                            food = sc.nextLine().toUpperCase();
                            if(food.equals("YES") || food.equals("NO")) {
                                break;
                            }
                            else {
                                System.out.println("INVALID CHOICE. PLEASE ENTER YES OR NO.");
                            }
                        }
                        List<String> inList = new ArrayList<>();
                        inList.add(inInput);
                        List<InPatient> inResult = au.parsePatientDetails(inList,roomType);
//                        if(inResult != null && !inResult.isEmpty()) {
//                            inResult.get(0).setRoomType(roomType);
//                            inResult.get(0).setFood(food);
//                        }
                        boolean inRes = inService.addInPatient(inResult);
                        if(inRes) {
                            System.out.println("\nINPATIENT ADDED SUCCESSFULLY");
                        }
                        else {
                            System.out.println("\nINSERTION FAILED");
                        }
                        break;
//                    case 2:
//                        System.out.print("\nENTER PATIENT ID : ");
//                        String patientId = sc.nextLine();
//                        String newRoomType = "";
//                        while(true) {
//                            System.out.print("ENTER NEW ROOM TYPE (AC / NONAC) : ");
//                            newRoomType = sc.nextLine().toUpperCase();
//                            if(newRoomType.equals("AC") || newRoomType.equals("NONAC")) {
//                                break;
//                            }
//                            else {
//                                System.out.println("INVALID ROOM TYPE. PLEASE ENTER AC OR NONAC.");
//                            }
//                        }
//                        int newAssignedRoom = inService.assignRoom(newRoomType);
//                        if(newAssignedRoom == 0) {
//                            System.out.println("NO ROOMS AVAILABLE FOR " + newRoomType);
//                            break;
//                        }
//                        System.out.println("NEW ROOM NUMBER ASSIGNED : " + newAssignedRoom);
//                        boolean roomResult = inService.updateRoomType(patientId, newRoomType);
//                        if(roomResult) {
//                            System.out.println("\nROOM UPDATED SUCCESSFULLY");
//                        }
//                        else {
//                            System.out.println("\nUPDATE FAILED");
//                        }
//                        break;
                    case 2:
                        System.out.print("\nENTER PATIENT ID : ");
                        String foodId = sc.nextLine();
                        String newFood = "";
                        while(true) {
                            System.out.print("DO YOU WANT FOOD? (YES / NO) : ");
                            newFood = sc.nextLine().toUpperCase();
                            if(newFood.equals("YES") || newFood.equals("NO")) {
                                break;
                            }
                            else {
                                System.out.println("INVALID CHOICE. PLEASE ENTER YES OR NO.");
                            }
                        }
                        boolean foodResult = inService.updateFoodPreference(foodId, newFood);
                        if(foodResult) {
                            System.out.println("\nFOOD UPDATED SUCCESSFULLY");
                        }
                        else {
                            System.out.println("\nUPDATE FAILED");
                        }
                        break;
                    case 3:
                        System.out.print("\nENTER PATIENT ID : ");
                        String deletePatient =sc.nextLine();
                        boolean deletePatientResult =inService.deleteInPatient(deletePatient);
                        if(deletePatientResult) {
                            System.out.println("\nDELETED SUCCESSFULLY");
                        }
                        else {
                            System.out.println("\nDELETE FAILED");
                        }
                        break;
                    case 4:
                        System.out.print("\nENTER PATIENT ID : ");
                        String viewPatient =sc.nextLine();
                        inService.viewInPatient(viewPatient);
                        break;
                    case 5:
                        break;
                    default:
                        System.out.println("\nINVALID CHOICE");
                    }
                    if(inChoice == 5) {
                        break;
                    }
                }
                break;
             

            case 3:
                while(true) {
                    System.out.println("\n");
                    System.out.println("                 DOCTOR MODULE ------------------->");
                    System.out.println("1. ADD DOCTOR");
                    System.out.println("2. UPDATE DOCTOR specialist");
                    System.out.println("3. VIEW DOCTOR");
                    System.out.println("4. DELETE DOCTOR");
                    System.out.println("5. BACK");
                    System.out.println("------------------------->");
                    System.out.print("ENTER YOUR CHOICE : ");
                    int doctorChoice =sc.nextInt();
                        sc.nextLine();
                    switch(doctorChoice) {
                    case 1:

                        System.out.println("FORMAT : ");
                        System.out.println("name:specialization:date:time");
                        System.out.println("ENTER DOCTOR DETAILS:");
                        String doctorInput =sc.nextLine();
                        
                        List<String> doctorList =new ArrayList<>();
                        doctorList.add(doctorInput);
                        
                        List<Doctor> doctorResult =au.parseDoctorsDetails(doctorList);
                        boolean doctorres = doctorService.addDoctorPatient(doctorResult);
                        
                        if(doctorres) {
                            System.out.println("\nDOCTOR ADDED SUCCESSFULLY");
                        }
                        else {
                            System.out.println("\nINSERTION FAILED");
                        }
                        break;
                    case 2:
                        System.out.print("\nENTER DOCTOR ID : ");
                        String doctorId =sc.nextLine();
                        System.out.print("ENTER NEW specialist: ");
                        String specialist =sc.nextLine();
                        boolean feeResult =doctorService.updateDoctorSpec(doctorId,specialist);
                        if(feeResult) {
                            System.out.println("\nSPECIALIZATION UPDATED SUCCESSFULLY");
                        }
                        else {
                            System.out.println("\nUPDATE FAILED");
                        }
                        break;
                    case 3:
                        System.out.print("\nENTER DOCTOR ID : ");
                        String viewDoctor =sc.nextLine();
                        doctorService.viewDoctor(viewDoctor);
                        break;
                    case 4:
                        System.out.print("\nENTER DOCTOR ID : ");
                        String deleteDoctor =sc.nextLine();
                        boolean deleteDoctorResult =
                                doctorService.deleteDoctor(deleteDoctor);
                        if(deleteDoctorResult) {
                            System.out.println("\nDELETED SUCCESSFULLY");
                        }
                        else {
                            System.out.println("\nDELETE FAILED");
                        }
                        break;
                    case 5:
                        break;
                    }
                    if(doctorChoice == 5) {
                        break;
                    }
                }
                break;
            case 4:
                System.out.println("\nAPPOINTMENT MODULE");
                System.out.println("FORMAT : ");
                System.out.println("patientId:date:time:mode");
                System.out.println("Enter the details:");
                String appointmentInput =sc.nextLine();
                System.out.println("Enter the Specialist:");
                String specialist =sc.nextLine();
                List<String> appointmentList =new ArrayList<>();
                appointmentList.add(appointmentInput);
                String doctorId = appointmentService.getDoctorIdBySpec(specialist);
        		List<Appointment> appointmentres =au.parseAppointmentDetails(appointmentList,specialist,doctorId);
//                boolean validid =au.isValidPatientId(ap.getPatientId());
//                boolean validDoc =au.isDoctorAvailable(ap.getAppointmentDate(),ap.getAppointmentTime());
//                if(validid) {
//                	if(validDoc) {
                		 boolean resapo =appointmentService.addAppointmentPatient(appointmentres);
                		 if(resapo) {
                             System.out.println("\nAPPOINTMENT BOOKED SUCCESSFULLY");
                         }
                         else {
                             System.out.println("\nBOOKING FAILED");
                         }
//                	}
//                	else {
//                		System.out.println("No doctor available at this time!");
//                	}
//                }
//                else {
//                    System.out.println(" Wrong Patient ID!");
//                    return;
//                } 
                break;
//            case 5:
//                System.out.println("\nALLOCATION MODULE");
//                System.out.println("FORMAT : ");
//                System.out.println("patientId:roomNo:days:admissionDate:dischargeDate:treatment:roomType:food");
//                String allocationInput =sc.nextLine();
//                List<String> allocationList =new ArrayList<>();
//                allocationList.add(allocationInput);
//                boolean allocationResult =
//                        allocationService.addAllocationList(allocationList);
//                if(allocationResult) {
//                    System.out.println("\nROOM ALLOCATED SUCCESSFULLY");
//                }
//                else {
//                    System.out.println("\nALLOCATION FAILED");
//                }
//                break;
//            case 6:
//                System.out.println("\nPAYMENT MODULE");
//                System.out.println("FORMAT : ");
//                System.out.println("patientId:patientName:patientType:paymentDate:paymentMode:billAmount");
//                String paymentInput =sc.nextLine();
//                List<String> paymentList =new ArrayList<>();
//                paymentList.add(paymentInput);
//                boolean paymentResult =paymentService.addPaymentList( paymentList);
//                if(paymentResult) {
//                    System.out.println("\nPAYMENT COMPLETED SUCCESSFULLY");
//                }
//                else {
//                    System.out.println("\nPAYMENT FAILED");
//                }
//                break;
//
            case 7:
                System.out.println("\n");
                System.out.println("      THANK YOU FOR USING THE SYSTEM---------------->");
                System.exit(0);
            default:
                System.out.println("\nINVALID CHOICE");
            }
        }
    }
}




















