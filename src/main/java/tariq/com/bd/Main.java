package tariq.com.bd;

import tariq.com.bd.tools.LoadGoogleCredentials;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class Main {
    public static void main(String[] args) throws IOException, MessagingException, GeneralSecurityException {
        System.out.println("Hello world!");

//        LoadGoogleCredentials.sendEmail("tariquljr@anyconnect.com", "testing ", "Hello from system");
        LoadGoogleCredentials.sendEmail("tarik.amtoly@gmail.com", "testing 2", "Hello from system");

//        LoadGoogleCredentials.getGmailService();
    }
}