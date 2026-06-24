package com.test;

import java.sql.Connection;
import java.sql.DriverManager;

public class TestDB {

    public static void main(String[] args) {

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/hospital_management",
                    "root",
                    ""
            );

            System.out.println("Database Connected Successfully");

            con.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}