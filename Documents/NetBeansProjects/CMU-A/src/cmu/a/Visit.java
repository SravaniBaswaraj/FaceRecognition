/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cmu.a;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;
import static cmu.a.DBSetup.printSQLException;
/**
 *
 * @author ppsra
 */
public class Visit {
  String studentID;
    String visitID;
    String reason;
    java.sql.Timestamp time;
    public Visit()
    {}
    
    public Visit(String studentID, String visitID, String reason, Timestamp time) {
        this.studentID = studentID;
        this.visitID = visitID;
        this.reason = reason;
        this.time = time;
    }
    
     static ArrayList<Visit> getVisitDetails(){
        return getVisitDetails("SELECT * FROM VisitDetails");
    }
    
    static ArrayList<Visit> getVisitDetails(String query) {
        String framework = "embedded";
        String protocol = "jdbc:derby:";

        ArrayList<Visit> visitDetails = new ArrayList();
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
                visitDetails.add(new Visit( rs.getString(2),rs.getString(1),
                        rs.getString(3), rs.getTimestamp(4)));
                System.out.println("data collected: \n\t" + rs.getString(1));
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
        return visitDetails;
    }

    public static void main(String args[])
    {
        ArrayList<Visit> st = (new Visit()).getVisitDetails();
        System.out.println("Trial Check :   " + st.get(0).studentID);

    }
    
    
}
