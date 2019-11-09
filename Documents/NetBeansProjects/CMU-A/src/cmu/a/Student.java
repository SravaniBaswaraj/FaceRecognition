/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cmu.a;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import static cmu.a.DBSetup.printSQLException;

/**
 *
 * @author ppsra
 */
public class Student {
    String studentID;
    String name;
    String program;
    String address;
    String gender;
    java.sql.Date DOB;

    public Student(String studentID, String name, String program, String address, String gender, Date DOB) {
        this.studentID = studentID;
        this.name = name;
        this.program = program;
        this.address = address;
        this.gender = gender;
        this.DOB = DOB;
    }
    public Student()
    {}
    
    static ArrayList<Student> getStudentDetails(){
        return getStudentDetails("SELECT * FROM StudentInfo");
    }
    
    static ArrayList<Student> getStudentDetails(String query) {
        String framework = "embedded";
        String protocol = "jdbc:derby:";

        ArrayList<Student> studentDetails = new ArrayList();
        Connection conn = null;
        PreparedStatement psInsert;
        PreparedStatement psUpdate;
        ResultSet rs = null;
        try {

            Properties props = new Properties(); // connection properties
            // providing a user name and password is optional in the embedded
            // and derbyclient frameworks
            props.put("user", "faceapp");
            props.put("password", "faceapp");

            String dbName = "faceRecTrial";
            conn = DriverManager.getConnection(protocol + dbName
                    + ";", props);

            System.out.println("Connection URL" + protocol + dbName + ";create=true");
            Statement s = conn.createStatement();

            System.out.println("Connected to and created database " + dbName);

            rs = s.executeQuery(query);
            while (rs.next()) {
                studentDetails.add(new Student(rs.getString(1), rs.getString(2),
                        rs.getString(3), rs.getString(5), rs.getString(6), rs.getDate(4)));
                System.out.println("data collected: \n\t" + rs.getString(1) + "\t" + rs.getString(2) + "\t" + rs.getString(3)
                        + "\t" + rs.getString(5) + "\t" + rs.getString(6) + "\t" + rs.getDate(4));
            }
            conn.commit();

        } catch (SQLException sqle) {
            printSQLException(sqle);
        } finally {
            // release all open resources to avoid unnecessary memory usage  
            //Connection
            try {
                if (conn != null) {
                    conn.close();
                    conn = null;

                }
                // ResultSet
                try {
                    if (rs != null) {
                        rs.close();
                        rs = null;
                    }
                } catch (SQLException sqle) {
                    printSQLException(sqle);
                }

            } catch (SQLException sqle) {
                printSQLException(sqle);
            }
        }
        return studentDetails;
    }

    
}
