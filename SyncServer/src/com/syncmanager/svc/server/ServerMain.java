package com.syncmanager.svc.server;

import com.syncmanager.dao.FileInfoDao;
import com.syncmanager.dao.orm.FileInfo;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;

public class ServerMain extends Thread {

    static String theData = "";
    static String ContentType;
    Socket theConnection;

    public static void main(String[] args) {

        int thePort;
        ServerSocket ss;
        Socket theConnection;
        FileInputStream theFile;

        // cache the file
        try {
            theFile = new FileInputStream(args[0]);
            DataInputStream dis = new DataInputStream(theFile);
            if (args[0].endsWith(".html") || args[0].endsWith(".htm")) {
                ContentType = "text/html";
            }
            else {
                ContentType = "text/plain";
            }

            try {
                String thisLine = "";
                while ((thisLine = dis.readLine()) != null) {
                    theData += thisLine + "\n";
                }
            }
            catch (Exception e) {
                System.err.println("Error: " + e);
            }

        }
        catch (Exception e) {
            System.err.println(e);
            System.err.println("Usage: java onefile filename port");
            System.exit(1);
        }

        // set the port to listen on
        try {
            thePort = Integer.parseInt(args[1]);
            if (thePort < 0 || thePort > 65535) thePort = 80;
        }
        catch (Exception e) {
            thePort = 80;
        }

        try {
            ss = new ServerSocket(thePort);
            System.out.println("Accepting connections on port "
                    + ss.getLocalPort());
            System.out.println("Data to be sent:");
            System.out.println(theData);
            while (true) {
                ServerMain fs = new ServerMain(ss.accept());
                fs.start();
            }
        }
        catch (IOException e) {

        }

    }

    public ServerMain(Socket s) {
        theConnection = s;
    }

    public void run() {

        try {
            PrintStream os = new PrintStream(theConnection.getOutputStream());
            DataInputStream is = new DataInputStream(theConnection.getInputStream());
            String request = is.readLine();
            // If this is HTTP/1.0 or later send a MIME header
            if (request.indexOf("HTTP/") != -1) {
                while (true) {  // read the rest of the MIME header
                    String thisLine = is.readLine();
                    if (thisLine.trim().equals("")) break;
                }

                os.print("HTTP/1.0 200 OK\r\n");
                Date now = new Date();
                os.print("Date: " + now + "\r\n");
                os.print("Server: OneFile 1.0\r\n");
                os.print("Content-length: " + theData.length() + "\r\n");
                os.print("Content-type: " + ContentType + "\r\n\r\n");
            } // end if
            os.println(theData);
            theConnection.close();
        }  // end try
        catch (IOException e) {

        }

    }

}