/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demosever;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Scanner;

/**
 *
 * @author kwokszeyan
 */
public class DemoSever {

    private static ServerSocket serverSocket;
    private static final int PORT = 3370;

    public static void main(String[] args) {
        // TODO code application logic here
        //Connection for 4 databases
        Connection connMarriott = null;
        Connection connHilton = null;
        Connection connHolidayinn = null ;
        Connection connsheraton = null;
        //Statement for 4 databases
        Statement statementMarriott = null;
        Statement statementHilton = null;
        Statement statementHolidayinn = null;
        Statement statementsheraton = null;
        //Result for 4 databases
        ResultSet resultsMarriott = null;
        ResultSet resultsHilton = null;
        ResultSet resultsHolidayinn = null;
        ResultSet resultssheraton = null;
        
        
        try
        {
            connMarriott = DriverManager.getConnection("jdbc:derby://localhost:1527/Marriott2Database","marriott","marriott");
            System.out.println("Connection Marriott created!");
            connHilton = DriverManager.getConnection("jdbc:derby://localhost:1527/Hilton2Database","hilton","hilton");
            System.out.println("Connection Hilton created!");
            connHolidayinn = DriverManager.getConnection("jdbc:derby://localhost:1527/Holidayinn2Database","holidayinn","holidayinn");
            System.out.println("Connection Holiday Inn created!");
            connsheraton = DriverManager.getConnection("jdbc:derby://localhost:1527/sheraton2Database","sheraton","sheraton");
            System.out.println("Connection Sheraton created!");   
        }catch(SQLException sqlEx)
        {
            System.out.println("* Cannot connect to the databases! *");
            System.exit(1);
        }
        
        System.out.println("Opening port...\n");
        try
        {
            serverSocket = new ServerSocket(PORT);
        }
        catch(IOException ioEx)
        {
            System.out.println("Unable to attach to port!");
            System.exit(1);
        }
        do {
            Socket link = null;
        
        
        try 
        {
            link = serverSocket.accept();
            Scanner input = new Scanner(link.getInputStream());
            PrintWriter output = new PrintWriter(link.getOutputStream(),true);
            statementHilton = connHilton.createStatement();
            statementMarriott = connMarriott.createStatement();
            statementHolidayinn = connHolidayinn.createStatement();
            statementsheraton = connsheraton.createStatement();
            
            String message = input.nextLine();
            
           while (!message.equals("CLOSE"))
            { 
                String sql = input.nextLine();
                
                resultsHilton = statementHilton.executeQuery(sql);
                resultsMarriott = statementMarriott.executeQuery(sql);
                resultsHolidayinn = statementHolidayinn.executeQuery(sql);
                resultssheraton = statementsheraton.executeQuery(sql);
 
                System.out.println(message);   
                System.out.println(sql);
   
                if (message.equals("selectCity"))     
                {
                    while (resultsHilton.next())
                    {
                        int rate = resultsHilton.getInt(1);
                        output.println("Hilton  $" + rate);
                        System.out.println("Hilton  $" + rate);
                    }
                        
                    while (resultsMarriott.next())
                    {    
                        int rate = resultsMarriott.getInt(1); 
                        output.println("Marriott  $" + rate);   
                        System.out.println("Marriott  $" + rate);
                    }
                    
                    while (resultsHolidayinn.next())
                    {
                        int rate = resultsHolidayinn.getInt(1);
                        output.println("Holiday Inn  $" + rate);
                        System.out.println("Holiday Inn  $" + rate);
                    }
                    
                    while (resultssheraton.next())
                    {
                        int rate = resultssheraton.getInt(1);
                        output.println("Sheraton  $" + rate);
                        System.out.println("Sheraton  $" + rate);
                    }  
                            
                    output.println(" ");
                }
                
                
                else if (message.equals("CheckAvailability"))
                {
                    String hotel = input.nextLine();
                    String idHilton = "",idMarriott = "",idHolidayinn= "",idsheraton= "";
                    Date startHilton = null ,startMarriott = null,startHolidayinn = null,startsheraton = null;  
                    Date endHilton = null ,endMarriott = null,endHolidayinn = null,endsheraton = null;

                    while (resultsHilton.next()){
                        idHilton = resultsHilton.getString(1);
                        startHilton = resultsHilton.getDate(2);
                        endHilton = resultsHilton.getDate(3);
                    }
                    
                    while (resultsMarriott.next()){
                        idMarriott = resultsMarriott.getString(1);
                        startMarriott = resultsMarriott.getDate(2);
                        endMarriott = resultsMarriott.getDate(3);
                    }
                    while (resultsHolidayinn.next()){
                        idHolidayinn = resultsHolidayinn.getString(1);
                        startHolidayinn = resultsHolidayinn.getDate(2);
                        endHolidayinn = resultsHolidayinn.getDate(3);
                    }
                    
                    while (resultssheraton.next()){
                        idsheraton = resultssheraton.getString(1);
                        startsheraton = resultssheraton.getDate(2);
                        endsheraton = resultssheraton.getDate(3);
                    }

                    if (hotel.equals("Hilton")){
                        output.println(idHilton + ","+ startHilton+ ","+endHilton);
                    }else if (hotel.equals("Marriott")){
                        output.println(idMarriott + ","+ startMarriott+ ","+endMarriott);
                    }else if (hotel.equals("Holiday Inn")){
                         output.println(idHolidayinn + ","+ startHolidayinn+ ","+endHolidayinn);
                    }else{
                        output.println(idsheraton + ","+ startsheraton+ ","+endsheraton);
                    }
                    

                    String sql2 = input.nextLine();
                    System.out.print (sql2);
                    if (hotel.equals("Hilton")){
                        resultsHilton = statementHilton.executeQuery(sql2);
                        if (resultsHilton.next()){
                            output.println(resultsHilton.getString(1)+ ","+resultsHilton.getDate(2));
                        }
                        }else if (hotel.equals("Marriott")){
                            resultsMarriott = statementMarriott.executeQuery(sql2);
                            while (resultsMarriott.next()){
                                output.println(resultsMarriott.getString(1)+ ","+resultsMarriott.getDate(2));
                            }
                        }else if (hotel.equals("Holiday Inn")){
                            resultsHolidayinn = statementHolidayinn.executeQuery(sql2);
                            while (resultsHolidayinn.next()){
                                output.println(resultsHolidayinn.getString(1)+ ","+resultsHolidayinn.getDate(2));
                            }
                        }else{
                            resultssheraton = statementsheraton.executeQuery(sql2);
                            while (resultssheraton.next()){
                                output.println(resultssheraton.getString(1)+ ","+resultssheraton.getDate(2)+ ","+resultssheraton.getInt(3));
                            }
                        }
                       output.println(" "); 
                       
                    } else if (message.equals("makeBooking"))
                    {
                        String hotel = input.nextLine();
                        String idHilton = "",idMarriott = "",idHolidayinn= "",idsheraton= "";
                        

                        while (resultsHilton.next()){
                            idHilton = resultsHilton.getString(1);
                        }

                        while (resultsMarriott.next()){
                            idMarriott = resultsMarriott.getString(1);
                        }
                        while (resultsHolidayinn.next()){
                            idHolidayinn = resultsHolidayinn.getString(1);
                        }

                        while (resultssheraton.next()){
                            idsheraton = resultssheraton.getString(1);
                        }

                        if (hotel.equals("Hilton")){
                            output.println(idHilton );
                        }else if (hotel.equals("Marriott")){
                            output.println(idMarriott);
                        }else if (hotel.equals("Holiday Inn")){
                             output.println(idHolidayinn);
                        }else{
                            output.println(idsheraton);
                        }
                        
                        // get all the bookingid
                        String sql2 = input.nextLine();
                        System.out.print (sql2);

                        if (hotel.equals("Hilton")){
                            resultsHilton = statementHilton.executeQuery(sql2);
                            while (resultsHilton.next()){
                                output.println(resultsHilton.getString(1));
                            }
                        }else if (hotel.equals("Marriott")){
                            resultsMarriott = statementMarriott.executeQuery(sql2);
                            while (resultsMarriott.next()){
                                output.println(resultsMarriott.getString(1));
                                }
                        }else if (hotel.equals("Holiday Inn")){
                            resultsHolidayinn = statementHolidayinn.executeQuery(sql2);
                            while (resultsHolidayinn.next()){
                                output.println(resultsHolidayinn.getString(1));
                                }
                        }else{
                            resultssheraton = statementsheraton.executeQuery(sql2);
                            while (resultssheraton.next()){
                                output.println(resultssheraton.getString(1));
                            }
                        }
                        output.println(" "); 


                        // get the record from room_availability
                        String sql3 = input.nextLine();
                        System.out.println(sql3);

                        if (hotel.equals("Hilton")){
                            resultsHilton = statementHilton.executeQuery(sql3);
                            while (resultsHilton.next()){
                                output.println(resultsHilton.getString(1)+resultsHilton.getDate(2));
                            }
                            }else if (hotel.equals("Marriott")){
                                resultsMarriott = statementMarriott.executeQuery(sql3);
                                while (resultsMarriott.next()){
                                    output.println(resultsMarriott.getString(1)+resultsMarriott.getDate(2));
                                }
                            }else if (hotel.equals("Holiday Inn")){
                                resultsHolidayinn = statementHolidayinn.executeQuery(sql3);
                                while (resultsHolidayinn.next()){
                                    output.println(resultsHolidayinn.getString(1)+ ","+resultsHolidayinn.getDate(2));
                                }
                            }else{
                                resultssheraton = statementsheraton.executeQuery(sql3);
                                while (resultssheraton.next()){
                                    output.println(resultssheraton.getString(1)+ ","+resultssheraton.getDate(2));
                                }
                            }
                            
                        output.println(" "); 
          
                        String msg = input.nextLine();
                        int result;
                        int msgno = Integer.parseInt(msg);
                        if (msgno == 0)
                        {
                            //insert to booking
                            String sql5 = input.nextLine();
                            System.out.println(sql5);
                            
                            String info = input.nextLine();
                            System.out.println(info);
                            
                            if (hotel.equals("Hilton")){
                                PreparedStatement st = connHilton.prepareStatement(sql5);
                                st.setString(1,info.split(",")[0]);
                                st.setString(2,info.split(",")[1]);
                                st.setString(3,info.split(",")[2]);
                                st.setString(4,info.split(",")[3]);
                                st.setInt(5,Integer.parseInt(info.split(",")[4]));
                                st.setString(6,info.split(",")[5]);
                                st.setString(7,info.split(",")[6]);
                                st.setString(8,info.split(",")[7]);
                                result = st.executeUpdate();

                            }else if (hotel.equals("Marriott")){
                                PreparedStatement st = connMarriott.prepareStatement(sql5);
                                st.setString(1,info.split(",")[0]);
                                st.setString(2,info.split(",")[1]);
                                st.setString(3,info.split(",")[2]);
                                st.setString(4,info.split(",")[3]);
                                st.setInt(5,Integer.parseInt(info.split(",")[4]));
                                st.setString(6,info.split(",")[5]);
                                st.setString(7,info.split(",")[6]);
                                st.setString(8,info.split(",")[7]);
                                result = st.executeUpdate();

                            }else if (hotel.equals("Holiday Inn")){
                                PreparedStatement st = connHolidayinn.prepareStatement(sql5);
                                st.setString(1,info.split(",")[0]);
                                st.setString(2,info.split(",")[1]);
                                st.setString(3,info.split(",")[2]);
                                st.setString(4,info.split(",")[3]);
                                st.setInt(5,Integer.parseInt(info.split(",")[4]));
                                st.setString(6,info.split(",")[5]);
                                st.setString(7,info.split(",")[6]);
                                st.setString(8,info.split(",")[7]);
                                result = st.executeUpdate();

                            }else{
                                PreparedStatement st = connsheraton.prepareStatement(sql5);
                                st.setString(1,info.split(",")[0]);
                                st.setString(2,info.split(",")[1]);
                                st.setString(3,info.split(",")[2]);
                                st.setString(4,info.split(",")[3]);
                                st.setInt(5,Integer.parseInt(info.split(",")[4]));
                                st.setString(6,info.split(",")[5]);
                                st.setString(7,info.split(",")[6]);
                                st.setString(8,info.split(",")[7]);
                                result = st.executeUpdate();
                            }
                            
                            if (result== 0)
                                   System.out.println("\nUnable to insert record!");
                            
                            //insert to room_availability
                            String insert = input.nextLine();
                            System.out.println(insert);
                            String book = input.nextLine();
                            while (!insert.equals(" "))
                            {
                               if (hotel.equals("Hilton")){
                                PreparedStatement st = connHilton.prepareStatement(insert);
                                st.setString(1,book.split(",")[0]);
                                st.setString(2,book.split(",")[1]);
                                result = st.executeUpdate();
    
                                }else if (hotel.equals("Marriott")){
                                    PreparedStatement st = connMarriott.prepareStatement(insert);
                                    st.setString(1,book.split(",")[0]);
                                    st.setString(2,book.split(",")[1]);
                                    result = st.executeUpdate();
                                    
                                }else if (hotel.equals("Holiday Inn")){
                                    PreparedStatement st = connHolidayinn.prepareStatement(insert);
                                    st.setString(1,book.split(",")[0]);
                                    st.setString(2,book.split(",")[1]);
                                    result = st.executeUpdate();
                                    
                                }else{
                                    PreparedStatement st = connsheraton.prepareStatement(insert);
                                    st.setString(1,book.split(",")[0]);
                                    st.setString(2,book.split(",")[1]);
                                    result = st.executeUpdate();
                                }
                              
                               insert = input.nextLine();
                               book = input.nextLine();
                            }
                        }
                        
                  } else if (message.equals("cancelBooking"))
                  {
                      
                      String hotel = input.nextLine();
                      System.out.print (hotel);
                      
                      if (hotel.equals("Hilton")){
                            resultsHilton = statementHilton.executeQuery(sql);
                            while (resultsHilton.next()){
                                output.println(resultsHilton.getString(1) + ","+resultsHilton.getDate(2)+","+resultsHilton.getDate(3));
                            }
                        }else if (hotel.equals("Marriott")){
                            resultsMarriott = statementMarriott.executeQuery(sql);
                            while (resultsMarriott.next()){
                                output.println(resultsMarriott.getString(1) + ","+resultsMarriott.getDate(2)+","+resultsMarriott.getDate(3));
                                }
                        }else if (hotel.equals("Holiday Inn")){
                            resultsHolidayinn = statementHolidayinn.executeQuery(sql);
                            while (resultsHolidayinn.next()){
                                output.println(resultsHolidayinn.getString(1) + ","+resultsHolidayinn.getDate(2)+ "," + resultsHolidayinn.getDate(3));
                                }
                        }else{
                            resultssheraton = statementsheraton.executeQuery(sql);
                            while (resultssheraton.next()){
                                output.println(resultssheraton.getString(1) + ","+resultssheraton.getDate(2)+ "," + resultssheraton.getDate(3));
                            }
                        }
                      
                      output.println(" "); 
                      
                      String resultbookingid = input.nextLine();
                      int result;
                      if (resultbookingid.equals("S"))
                      {
                          String sql2 = input.nextLine();
                          String val1 = input.nextLine();
                          if (hotel.equals("Hilton")){
                                PreparedStatement st = connHilton.prepareStatement(sql2);
                                st.setString(1,val1.split(",")[0]);
                                st.setString(2,val1.split(",")[1]);
                                st.setString(3,val1.split(",")[2]);
                                result = st.executeUpdate();

                            }else if (hotel.equals("Marriott")){
                                PreparedStatement st = connMarriott.prepareStatement(sql2);
                                st.setString(1,val1.split(",")[0]);
                                st.setString(2,val1.split(",")[1]);
                                st.setString(3,val1.split(",")[2]);
                                result = st.executeUpdate();

                            }else if (hotel.equals("Holiday Inn")){
                                PreparedStatement st = connHolidayinn.prepareStatement(sql2);
                                st.setString(1,val1.split(",")[0]);
                                st.setString(2,val1.split(",")[1]);
                                st.setString(3,val1.split(",")[2]);
                                result = st.executeUpdate();

                            }else{
                                PreparedStatement st = connsheraton.prepareStatement(sql2);
                                st.setString(1,val1.split(",")[0]);
                                st.setString(2,val1.split(",")[1]);
                                st.setString(3,val1.split(",")[2]);
                                result = st.executeUpdate();
                            }
                          
                          String sql3 = input.nextLine();
                          String val2 = input.nextLine();
                          if (hotel.equals("Hilton")){
                                PreparedStatement st = connHilton.prepareStatement(sql3);
                                st.setString(1,val2);
                                result = st.executeUpdate();

                            }else if (hotel.equals("Marriott")){
                                PreparedStatement st = connMarriott.prepareStatement(sql3);
                                st.setString(1,val2);
                                result = st.executeUpdate();

                            }else if (hotel.equals("Holiday Inn")){
                                PreparedStatement st = connHolidayinn.prepareStatement(sql3);
                                st.setString(1,val2);
                                result = st.executeUpdate();

                            }else{
                                PreparedStatement st = connsheraton.prepareStatement(sql3);
                                st.setString(1,val2);
                                result = st.executeUpdate();
                            }
                      }
                  }
                message = input.nextLine(); 
           }
        }
        catch(IOException ioEx){
           ioEx.printStackTrace();
        }catch(SQLException sqlEx)
        {
           System.out.println("* Cannot execute query! *");
           sqlEx.printStackTrace();
           System.exit(1);
        }
        
        finally {
        try {
            link.close();
        }
        catch(IOException ioEx)
        {
              System.out.println("Unable to disconnect!");
            System.exit(1);
        }
        }
        }while (true);
    }
    
}

