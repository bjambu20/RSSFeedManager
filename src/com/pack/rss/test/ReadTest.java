package com.pack.rss.test;

import com.pack.rss.model.Feed;
import com.pack.rss.model.FeedMessage;
import com.pack.rss.read.RSSFeedParser;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
public class ReadTest {
	public static void main(String[] args) {
		RSSFeedParser parser = new RSSFeedParser("https://cio.economictimes.indiatimes.com/rss/topstories");
		Feed feed = parser.readFeed();
		System.out.println(feed);
		StringBuffer sb = new StringBuffer();
		for (FeedMessage message : feed.getMessages()) {
			sb.append(message);
		}
		System.out.println(sb.toString());
		String feedsToBeSent = sb.toString();
//      Also the system must send feeds to the users whenever an update receives from RSS API. 
//		The system should not send feeds which are already sent. An efficient compression must be used by the system for transferring feeds to its users.
//      Each news records send from System must have fields “News Header, News published date & News description”. 
//		News header content size and Description content size will be dynamic 
		String loggedInUser="bjambu20@gmail.com";//change accordingly
		String from="flowerplus12345@gmail.com";//change accordingly
		String password="*****";//change accordingly

		//Get the session object
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class",
		"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");

		Session session = Session.getDefaultInstance(props,
		new javax.mail.Authenticator() {
		protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(from,password);
		}
		});

		//compose message
		try {
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from));
		message.addRecipient(Message.RecipientType.TO,new InternetAddress(loggedInUser));
		message.setSubject("RSS FEED");
		message.setText(feedsToBeSent);

		//send message
		Transport.send(message);

		System.out.println("RSS Feed sent successfully");

		} catch (MessagingException e) {throw new RuntimeException(e);}
		
	}
}