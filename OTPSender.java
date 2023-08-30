import java.util.Properties;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import javax.mail.Address.*;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MailSender {
        public static boolean sent_success;

        public static void sendMail(String from, String to, String host, String port, String username, String password, Boolean ssl, String otp) {
            sent_success = false;
                        System.out.println("preparing to send message ...");
                        String message = "Hello , here is the otp to reset the password of your account : "+otp + "\nKindly do not share it with anyone for security reasons. :)";
                        String subject = "Test Email";

            //get the system properties
            Properties properties = System.getProperties();
            System.out.println("PROPERTIES "+properties);

            //setting important information to properties object

            //host set
            properties.put("mail.smtp.host", host);
            properties.put("mail.smtp.port",port);
            properties.put("mail.smtp.ssl.enable", Boolean.toString(ssl));
            properties.put("mail.smtp.auth","true");

            //Step 1: to get the session object..
            Session session=Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            session.setDebug(true);

            //Step 2 : compose the message [text,multi media]
            MimeMessage m = new MimeMessage(session);

            try {
                InternetAddress fromAddr = new InternetAddress(from);
            //from email
            m.setFrom(fromAddr);

            //adding recipient to message
            m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            //adding subject to message
            m.setSubject(subject);

            //adding text to message
            m.setText(message);

            //send

            //Step 3 : send the message using Transport class
            Transport.send(m);

            System.out.println("Sent success...................");
            sent_success = true;

            }catch (Exception e) {
                e.printStackTrace();
            }

        }
}



public class OTPSender {

    public static void main(String[] args) throws IOException
    {

        int port = 8081;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server listening on port " + port);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream consoleStream = new PrintStream(outputStream);
        System.setOut(consoleStream);
        
        while (true) {
            Socket clientSocket = serverSocket.accept();
            
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            OutputStream out = clientSocket.getOutputStream();
            
            String requestLine = in.readLine();
            
            String request_url = requestLine.split("GET /?")[1].split(" ")[0];
            System.out.println(request_url);
            String[] url_args = request_url.split("&");
            
            String target_email = "", otp = "";
            
            for(String arg_key : url_args){
                String arg = arg_key.split("=")[0];
                String value = arg_key.split("=")[1];
                if(arg.equals("?email")){
                    target_email = value;
                }else if(arg.equals("otp")){
                    otp = value;
                }
                System.out.println(arg_key);
            }

            MailSender.sendMail("noreply@hostingspell.com", target_email, "smtp-pulse.com", "465", "fivecron@gmail.com", "AJn6SAYYjBgt8Y", true, otp);

            
            String sent_output = "Could not send otp email";
            
            if(MailSender.sent_success)
                sent_output = "OTP Sent successfully";

                // Extract requested path from the request
            String response = "HTTP/1.1 404 Not Found\r\n" +
                        "Content-Type: text/plain\r\n" +
                        "\r\n" +
                        sent_output;


            out.write(response.getBytes());
            out.close();
            in.close();
            clientSocket.close ();
        }
    }
}


