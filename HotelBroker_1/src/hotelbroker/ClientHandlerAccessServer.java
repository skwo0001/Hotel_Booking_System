/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotelbroker;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.lang.String;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author kwokszeyan
 */
public class ClientHandlerAccessServer extends Thread
{
    private Socket link;
    private Socket serverlink; // for access server
    private Scanner input;
    private PrintWriter output;
    private Scanner serverinput;
    private PrintWriter serveroutput;
    private static final int Server_PORT = 3370;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public ClientHandlerAccessServer(Socket socket) {
        
        link = socket;
        
        try 
        {
            serverlink = new Socket("localhost", Server_PORT);
            //for client
            input = new Scanner(link.getInputStream());
            output = new PrintWriter(link.getOutputStream(),true);
            //for server
            serverinput = new Scanner(serverlink.getInputStream());
            serveroutput = new PrintWriter(serverlink.getOutputStream(),true);
            
        } 
        catch (IOException ioEx)
        {
            ioEx.printStackTrace();
        }
    }
    public void run()
    {
        String message = input.nextLine();
        System.out.println(message);

        do
        {
             if (message.equals("selectCity"))
            {
                String City = input.nextLine();
                String sql = "Select rate from Hotels where city = '" + City+ "'";
                serveroutput.println("selectCity");
                serveroutput.println(sql);

                String result = serverinput.nextLine();

                while (!result.equals(" "))
                {
                    System.out.println(result);
                    output.println(result);
                    result = serverinput.nextLine();
                } 
                output.println(" ");
                }
            else if (message.equals("CheckAvailability"))
            {
                String city = input.nextLine();
                String hotel = input.nextLine();
                String checkin = input.nextLine();
                String checkout = input.nextLine();
                
                serveroutput.println("CheckAvailability");
                System.out.println("CheckAvailability");

                String sql = "Select hotelid,vacancy_start,vacancy_ends from Hotels where city = '" + city+ "'";
                
                serveroutput.println(sql);

                serveroutput.println(hotel);

                String result = serverinput.nextLine();
                System.out.println(result);

                String hotelid = result.split(",")[0];
                String vacancystart = result.split(",")[1];
                String vacancyend = result.split(",")[2];

                String sql2 = "Select * from Room_Availability where hotelid = '" + hotelid+ 
                        "' AND Booked_date between '" + checkin+ "' and '"+ checkout+ "'";

                serveroutput.println(sql2);
                String data = serverinput.nextLine();

                ArrayList<String> ayl = new ArrayList();

                while (!data.equals(" "))
                {
                    ayl.add(data);
                    data = serverinput.nextLine();
                }
                
                System.out.println(ayl.size());

                if (ayl.size() == 0)
                {
                    output.println("Success");
                }else {
                    output.println("Fail");   
                }

                try{
                    Date checkindate = sdf.parse(checkin); 
                    Date checkoutdate = sdf.parse(checkout); 
                    Date vacancystartdate = sdf.parse(vacancystart); 
                    Date vacancyenddate = sdf.parse(vacancyend); 

                    if (checkindate.after(vacancystartdate) && checkindate.before(vacancyenddate))
                    {
                        if (checkoutdate.after(vacancystartdate) && checkoutdate.before(vacancyenddate))
                        {
                            output.println("Success");
                        } else {
                            output.println("Fail");
                        }
                    } else {
                            output.println("Fail");
                        } 
                } catch (ParseException ex)
                {}

                }else if (message.equals("makeBooking")){

                    String hotel = input.nextLine();
                    String city = input.nextLine();
                    String Name = input.nextLine();
                    String Email = input.nextLine();
                    String Phone = input.nextLine();
                    String CC = input.nextLine();
                    String checkin = input.nextLine();
                    String checkout = input.nextLine();
                    String booking_ref = "";

                    //generate letter part of booking ref
                    if (hotel.equals("Hilton"))
                        {
                            booking_ref = "htbk";
                        } else if (hotel.equals("Holiday Inn"))
                        {
                            booking_ref = "hibk";
                        } else if (hotel.equals("Marriott"))
                        {
                            booking_ref = "mtbk";
                        } else
                        {
                            booking_ref = "stbk";
                        }

                    //get hotel_id
                    String sql = "select hotelid from hotels where city = '"+city+"'";
                    serveroutput.println(message);
                    System.out.println(message);
                    System.out.println(sql);
                    serveroutput.println(sql);
                    serveroutput.println(hotel);
                    System.out.println(hotel);

                    String hotel_id = serverinput.nextLine();
                    System.out.println(hotel_id);

                    String sql2 = "select bookingid from booking";
                    serveroutput.println(sql2);
                    System.out.println(sql2);
                    String bookid = serverinput.nextLine();
                    ArrayList<String> ayl = new ArrayList();

                    while (!bookid.equals(" "))
                    {
                        System.out.println(bookid);
                        ayl.add(bookid);
                        bookid = serverinput.nextLine();
                    } 

                    int lastdigit = 0;

                    if (ayl.size() == 0)
                    {
                        booking_ref = booking_ref + "001";           
                    }else {
                        for (int i =0; i < ayl.size();i++)
                        {
                            int booknumber = Integer.parseInt(ayl.get(i).split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[1]);
                            lastdigit = Integer.parseInt(ayl.get(0).split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[1]);

                            if (booknumber > lastdigit)
                            {
                                lastdigit = booknumber;
                            } 
                        }

                        lastdigit = lastdigit + 1;
                        String with4digits = String.format("%04d", lastdigit);
                        booking_ref = booking_ref + with4digits;
                    }

                    LocalDate start = LocalDate.parse(checkin);
                    LocalDate end = LocalDate.parse(checkout);
                    List<LocalDate> totalDates = new ArrayList<>();
                    while (!start.isAfter(end)) {
                        totalDates.add(start);
                        start = start.plusDays(1);
                    }

                    String sql3 = "Select * from Room_Availability where hotelid = '" +hotel_id+"' AND Booked_date between '" + checkin+ "' and '"+ checkout+ "'";
                    serveroutput.println(sql3);
                    System.out.println(sql3);
                    String info = serverinput.nextLine();
                    System.out.println(info);

                    //get the total_booked_rooms for each day
                    ArrayList<String> aylinfo = new ArrayList();

                    while (!info.equals(" "))
                    {
                        aylinfo.add(info);
                        info = serverinput.nextLine();
                    }

                    serveroutput.println(aylinfo.size());
                    System.out.println(aylinfo.size());

                    if (aylinfo.size() != 0)
                    {
                        output.println("F"); 
                        
                    }else {
                        
                        output.println("S");
                        
                        String sql5= "INSERT INTO BOOKING VALUES (?,?,?,?,?,?,?,?)" ;
                        serveroutput.println(sql5);
                        System.out.println(sql5);
                        serveroutput.println(booking_ref+","+ hotel_id+","+ Name +","+Email+","+ Phone+","+ checkin + ","+ checkout+","+CC);
                        
                        for (int i = 0; i < totalDates.size()-1; i++)
                        {
                            String insertrm = "INSERT INTO ROOM_AVAILABILITY (hotelid,booked_date)VALUES (?,?)";
                            
                            serveroutput.println(insertrm);
                            serveroutput.println(hotel_id +","+totalDates.get(i));
                            System.out.println(hotel_id +","+totalDates.get(i));
                        }
                        serveroutput.println(" ");
                    }
                    
                        output.println(booking_ref);
                        break;
                    

                    }else if (message.equals("cancelBooking"))   
                    {
                    String booking_ref = input.nextLine();
                    String hotel = "";
                    if ( booking_ref.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[0].equals("htbk"))
                    {
                        hotel = "Hilton";
                    }else if ( booking_ref.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[0].equals("hibk")) 
                    {
                        hotel = "Holiday Inn";
                    }else if ( booking_ref.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[0].equals("mtbk"))
                    {
                        hotel = "Marriott";
                    }else if ( booking_ref.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[0].equals("stbk"))
                    {
                        hotel = "Sheraton";
                    }
                    
                    serveroutput.println("cancelBooking");
                    
                    String sql = "Select hotelid,check_in,check_out from booking where bookingid = '" +booking_ref+"'";
                    serveroutput.println(sql);
                    serveroutput.println(hotel);
                    
                    String result = serverinput.nextLine();
                    System.out.println(result);
                    
                    if (result.equals(" "))
                    {
                        output.println("F");
                        serveroutput.println("F");
                    } else 
                    {
                        serveroutput.println("S");
                        String hotelid = result.split(",")[0];
                        String checkin = result.split(",")[1];
                        String checkout = result.split(",")[2];

                        String deleteroom = "Delete from Room_availability where hotelid = ? and booked_date between ? and ?";
                        serveroutput.println(deleteroom);
                        serveroutput.println(hotelid+","+checkin+","+checkout);

                        String deletebooking = "Delete from Booking where bookingid = ?";
                        serveroutput.println(deletebooking);
                        serveroutput.println(booking_ref);
                        output.println("S");
                    } 
                }

            message = input.nextLine(); 

        } while(!message.equals("CLOSE"));

        try{
            if (link!= null)
            {
                System.out.println("Closing down connection...");

                link.close();
                serverlink.close();
            }
        }
        catch(IOException ioEx)
        {
            System.out.println("Unable to disconnect!");
        }
    }

}



    
    

