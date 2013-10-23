package reedey.server.tracking;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Mailer {

	public void sendMail(String email, String userName, String subject,
			String message) throws UnsupportedEncodingException,
			MessagingException, AddressException {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		Message msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress("reedey.noreply@gmail.com", "Red Eye Admin"));
		msg.addRecipient(Message.RecipientType.TO, new InternetAddress(email,
				userName));
		msg.setSubject(subject);
		msg.setText(message);
		Transport.send(msg);
	}
}
