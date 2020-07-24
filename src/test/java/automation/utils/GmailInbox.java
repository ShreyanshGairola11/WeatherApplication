package automation.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMultipart;

public class GmailInbox {


	public void read() {

		Properties props = new Properties();

		try {
			props.load(new FileInputStream(new File("smtp.properties")));
			Session session = Session.getDefaultInstance(props, null);

			Store store = session.getStore("imaps");

			// store.connect("smtp.gmail.com", "anujkumar17noida@gmail.com",
			// "5589201301");
			store.connect("smtp.gmail.com", "SRMQA.Automation@kaplan.edu", "K@plan888email");
			Folder inbox = store.getFolder("inbox");
			inbox.open(Folder.READ_ONLY);
			int messageCount = inbox.getMessageCount();
			Date current = new Date();
			System.out.println("Total Messages:- " + messageCount);
			System.out.println("Date: " + new Date().toString());
			Message[] messages = inbox.getMessages();
			System.out.println("------------------------------");
			Map<Integer, Map<String, Object>> all = new LinkedHashMap<Integer, Map<String, Object>>();
			outer: for (int i = messages.length - 1; i >= 0; i--) {
				System.out.println("Count: " + i);
				if (messages[i].getSubject().equalsIgnoreCase("Sandbox: Application and Enrollment Process"))

				{
					Map<String, Object> internal = new HashMap<String, Object>();
					internal.put("Subject", messages[i].getSubject());
					internal.put("Content: ", getTextFromMessage(messages[i]));
					System.out.println(messages[i].getReceivedDate());
					all.put(i, internal);
					// Address[] allmessages = messages[i].getAllRecipients();
					// String[] allemail = null;
					// if (allmessages != null) {
					// allemail = new String[allmessages.length];
					// int count = 0;
					// for (Address rec : allmessages) {
					//
					// allemail[count++] = rec.toString();
					// System.out.println(count + "-" + rec.toString());
					// }
					// }
					// internal.put("Rec:", allemail);
					System.out.println("Mail Subject:- " + cleanTextContent(messages[i].getSubject()));
					System.out.println("Date: " + messages[i].getReceivedDate());
					System.out.println("Text: " + getTextFromMessage(messages[i]));
					System.out.println("*************************************************");
				}
			}
			System.out.println("Completed");

			inbox.close(true);
			store.close();

		} catch (Exception e) {
			System.out.println("Localoised: " + e.getLocalizedMessage());
			e.printStackTrace();

		}
	}

	private String getTextFromMessage(Message message) throws MessagingException, IOException {
		String result = "";
		if (message.isMimeType("text/plain")) {
			// System.out.println("Message is of type: text/plain");
			result = message.getContent().toString();
		} else if (message.isMimeType("multipart/*")) {
			// System.out.println("Message is of type: multipart/*");
			MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
			result = getTextFromMimeMultipart(mimeMultipart);
		}
		return result;
	}

	private String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws MessagingException, IOException {
		String result = "";
		int count = mimeMultipart.getCount();
		for (int i = 0; i < count; i++) {
			BodyPart bodyPart = mimeMultipart.getBodyPart(i);
			System.out.println("Content Type: " + bodyPart.getContentType());
			if (bodyPart.isMimeType("text/plain")) {

				byte[] bytes = bodyPart.getContent().toString().getBytes("ISO-8859-1");
				// result=result+"\n"+bodyPart.getContent().toString();
				// result = result + "\n" + new String(bytes);
				// break; // without break same text appears twice in my tests
			} else if (bodyPart.isMimeType("text/html")) {

				String html = (String) bodyPart.getContent();
				System.err.println("HTML: " + html);
				result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
			} else if (bodyPart.getContent() instanceof MimeMultipart) {
				// result = result + getTextFromMimeMultipart((MimeMultipart)
				// bodyPart.getContent());
			}
		}
		return result;
	}

	private static Date getDate() {
		String dateStr = "Fri Feb 01 13:41:11 IST 2019";
		DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
		Date date = null;
		try {
			date = (Date) formatter.parse(dateStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(date);
		return date;
	}

	private static String cleanTextContent(String text) {
		// strips off all non-ASCII characters
		text = text.replaceAll("[^\\x00-\\x7F]", "");
		// erases all the ASCII control characters
		text = text.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");
		// removes non-printable characters from Unicode
		text = text.replaceAll("\\p{C}", "");
		return text.trim();
	}

}
