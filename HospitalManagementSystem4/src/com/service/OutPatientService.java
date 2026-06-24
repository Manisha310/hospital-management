
package com.service;

import java.util.ArrayList;
import java.util.List;

import com.management.outPatientManagement;
import com.model.InPatient;
import com.model.OutPatient;
import com.util.ApplicationUtil;

public class OutPatientService {

    outPatientManagement management= new outPatientManagement();

    public boolean addOutPatient(List<OutPatient> input) {
    	boolean res = management.insertOutPatientList(input);
    	return res;
    }
    public boolean updateOutPatientPhoneNumber(String patientId,long phone) {

        return management.updatePhoneNumber(patientId,phone);
    }

    public boolean deleteOutPatient(
            String patientId) {

        return management.deleteOutPatient(patientId);
    }

    public void viewOutPatient(
            String patientId) {

        management.viewOutPatient(
                patientId);
    }
}




