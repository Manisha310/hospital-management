package com.management;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnectionManager {

    static Connection con;

    public static Connection getConnection() {

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");con=DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_management","root","");

        }

        catch(Exception e) {

            e.printStackTrace();
        }

        return con;
    }
}