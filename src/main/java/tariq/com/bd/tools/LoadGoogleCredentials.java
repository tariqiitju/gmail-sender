package tariq.com.bd.tools;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class LoadGoogleCredentials {

    public static MimeMessage createEmail(String to, String from, String subject, String bodyText) throws MessagingException {
        Properties properties = new Properties();
        Session session = Session.getDefaultInstance(properties, null);
        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject);
        email.setText(bodyText);

        return email;
    }
    public static Message createMessageWithEmail(MimeMessage email) throws MessagingException, IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        email.writeTo(stream);
        String encodedEmail = Base64.encodeBase64URLSafeString(stream.toByteArray());
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }
    public static void sendEmail(String toEmail, String mailSubject, String mailBody)
            throws IOException, GeneralSecurityException, MessagingException {
        Gmail service = getGmailService();
        MimeMessage Mimemessage = createEmail(toEmail, "me", mailSubject, mailBody);
        Message message = createMessageWithEmail(Mimemessage);
        message = service.users().messages().send("me", message).execute();

        System.out.println("MessageId: " + message.getId());
        System.out.println(message.toPrettyString());
    }

    public static GoogleCredential createCredentialForServiceAccountImpersonateUser
            (HttpTransport transport, JsonFactory jsonFactory, String serviceAccountId,
             Collection<String> serviceAccountScopes, PrivateKey privateKey,
             String serviceAccountUser) throws GeneralSecurityException, IOException {
        return new GoogleCredential.Builder()
                .setTransport(transport)
                .setJsonFactory(jsonFactory)
                .setServiceAccountId(serviceAccountId)
                .setServiceAccountScopes(serviceAccountScopes)
                .setServiceAccountPrivateKey(privateKey)
                .setServiceAccountUser(serviceAccountUser)
                .build();
    }

    public static Gmail getGmailService() throws IOException, GeneralSecurityException {
        String pathToKeyFile = "/Users/tariq/code/sample projects/gmail-sender/src" +
                "/main/resources/smarterai-email-sender-2ab36c0d864e.json";
        InputStream in = new FileInputStream(pathToKeyFile);
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        String serviceAccountUser = "noreply@smarterai.camera";
        GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream(pathToKeyFile))
                .createScoped(Collections.singletonList(GmailScopes.GMAIL_SEND));
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        GoogleCredential cred =  createCredentialForServiceAccountImpersonateUser(httpTransport, jsonFactory,
                credential.getServiceAccountId(), credential.getServiceAccountScopes(),
                credential.getServiceAccountPrivateKey(), serviceAccountUser);
        Gmail gmail = new Gmail.Builder(httpTransport, jsonFactory, cred)
                .setApplicationName("test").build();
        return gmail;

    }
}
