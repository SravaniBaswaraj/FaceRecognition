/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cmu.a;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

/**
 *
 * @author ppsra
 */
public class DBSetup {
     private String framework = "embedded";
    private String protocol = "jdbc:derby:";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        new DBSetup().go(args);
        System.out.println("Database setup finished");
        //ArrayList<Student> st = (new Sampleconnection()).getStudentDetails();
        //System.out.println("Trial Check :   " + st.get(0).name);

    }
    
    
    void go(String[] args) {
        /* parse the arguments to determine which framework is desired*/
        parseArguments(args);

        System.out.println("Database setup in " + framework + " mode");
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
                    + ";create=true", props);

            System.out.println("Connection URL" + protocol + dbName + ";create=true");

            System.out.println("Connected to and created database " + dbName);

            /* Creating a statement object that we can use for running various
             * SQL statements commands against the database.*/
            //CLEANUP PREVIOUS VALUES
            deleteAllTables(conn);

            // CREATE ALL TABLES
            createTables(conn);

            //POPULATE TABLES
            populateTables(conn);
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
            } catch (SQLException sqle) {
                printSQLException(sqle);
            }
        }

    }

    private void populateTables(Connection conn) {
        try {
            String directoryPath = "C:\\Users\\ppsra\\Documents\\NetBeansProjects\\CMU-A\\";
            String StudentInfoFile = directoryPath + "StudentInfo.csv";
            String VisitDetailsFile = directoryPath + "VisitDetails.csv";
            String AnnouncementFile = directoryPath + "Announcements.csv";
            ArrayList<Statement> statements = new ArrayList<Statement>(); // list of Statements, PreparedStatements

            //INSERTING STUDENT INFO
            PreparedStatement psInsert = conn.prepareStatement("insert into StudentInfo values (?, ?, ?, ?, ?, ?)");
            //statements.add(psInsert);
            BufferedReader in = new BufferedReader(new FileReader(StudentInfoFile));
            Scanner sc = new Scanner(in);
            sc.useDelimiter(",|\\n");
            while (sc.hasNext()) {
                String StudentID = sc.next();
                psInsert.setString(1, StudentID);
                psInsert.setString(2, sc.next());
                psInsert.setString(3, sc.next());
                psInsert.setDate(4, java.sql.Date.valueOf(sc.next()));
                psInsert.setString(5, sc.next());
                psInsert.setString(6, sc.next());
                psInsert.executeUpdate();
                System.out.println("Inserted " + StudentID);
            }

            //INSERTING VISITING DETAILS
            PreparedStatement psInsert2 = conn.prepareStatement("insert into VisitDetails values (?, ?, ?, ?)");
            //statements.add(psInsert2);
            in = new BufferedReader(new FileReader(VisitDetailsFile));
            sc = new Scanner(in);
            sc.useDelimiter(",|\\n");
            while (sc.hasNext()) {
                String VisitID = sc.next();
                psInsert2.setString(1, VisitID);
                psInsert2.setString(2, sc.next());
                psInsert2.setString(3, sc.next());
                psInsert2.setTimestamp(4, java.sql.Timestamp.valueOf(sc.next()));
                psInsert2.executeUpdate();
                System.out.println("Inserted " + VisitID);
            }

            //INSERTING ANNOUNCEMENTS
            PreparedStatement psInsert3 = conn.prepareStatement("insert into Announcements values (?, ?)");
            //statements.add(psInsert3);
            in = new BufferedReader(new FileReader(AnnouncementFile));
            sc = new Scanner(in);
            sc.useDelimiter(",|\\n");
            while (sc.hasNext()) {

                psInsert3.setString(1, sc.next());
                psInsert3.setString(2, sc.next());
                psInsert3.executeUpdate();
                System.out.println("Inserted Annoumcement");
            }

        } catch (SQLException e) {
            System.out.println(e);
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found");
        }

    }

    void deleteAllTables(Connection conn) {
        try {

            ArrayList<Statement> statements = new ArrayList<Statement>(); // list of Statements, PreparedStatements
            Statement s;
            s = conn.createStatement();
            statements.add(s);
            //s.execute("drop table location");
            //System.out.println("Deleted table location ");
            s.execute("drop table StudentInfo");
            System.out.println("Deleted table StudentInfo ");
            s.execute("drop table VisitDetails");
            System.out.println("Deleted table VisitDetails ");
            s.execute("drop table Announcements");
            System.out.println("Deleted table VisitDetails ");

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    void createTables(Connection conn) {
        try {

            ArrayList<Statement> statements = new ArrayList<Statement>(); // list of Statements, PreparedStatements

            Statement s;
            s = conn.createStatement();
            statements.add(s);
            s.execute("create table StudentInfo(studentID varchar(20),name varchar(40), program varchar(40),DOB DATE, address varchar(40),gender varchar(20))");
            System.out.println("Created StudentInfo");
            s.execute("create table VisitDetails(visitID varchar(20), studentID varchar(40), reason varchar(70), time TIMESTAMP)");
            System.out.println("Created VisitDetails");
            s.execute("create table Announcements(studentID varchar(40), announcement varchar(70))");
            System.out.println("Created Announcements");

        } catch (SQLException e) {
            System.out.println(e);
        }

    }

    private void parseArguments(String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("derbyclient")) {
                framework = "derbyclient";
                protocol = "jdbc:derby://localhost:1527/";
            }
        }
    }
    
    public static void printSQLException(SQLException e)
    {
        // Unwraps the entire exception chain to unveil the real cause of the
        // Exception.
        while (e != null)
        {
            System.err.println("\n----- SQLException -----");
            System.err.println("  SQL State:  " + e.getSQLState());
            System.err.println("  Error Code: " + e.getErrorCode());
            System.err.println("  Message:    " + e.getMessage());
            // for stack traces, refer to derby.log or uncomment this:
            //e.printStackTrace(System.err);
            e = e.getNextException();
        }
    }

}

