package automation.utils;

import static automation.utils.YamlReader.setYamlFilePath;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.search.AndTerm;
import javax.mail.search.FromTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GmailReader {

	public Folder folder;
	public Store store;
	private Date timeStampOfSelectedEmail = null;
	public String currentMailSubject = null;

	public GmailReader() {
		// setYamlFilePath();
	}

	public void GmailReaderConnect(String userId, String password) {
		folder = readFolder("Inbox", userId, password);
	}

	private Folder readFolder(String folderLocation, String userId, String password) {
		Store store = getStore(userId, password);

		try {
			folder = store.getFolder(folderLocation);
			folder.open(Folder.READ_ONLY);
		} catch (MessagingException e) {

			e.printStackTrace();
		}
		return folder;
	}

	private Store getStore(String userId, String password) {

		try {
			System.out.println(
					"Connecting gmail using credentials: " + userId + " as userId " + "&" + password + " as password");
			store = getSession().getStore("imaps");
			store.connect("smtp.gmail.com", userId, password);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return store;
	}

	private Session getSession() {
		Properties properties = new Properties();
		properties.setProperty("mail.store.protocol", "imaps");
		properties.setProperty("mail.imaps.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		properties.setProperty("mail.imaps.socketFactory.fallback", "false");
		properties.setProperty("mail.imaps.ssl.enable", "true");
		Session session = Session.getInstance(properties, null);

		return session;
	}

	public Map<String, String> getLinksFromEmail(String content) {
		System.out.println("Fetching links from Email...");
		Map<String, String> linksMap = new HashMap<String, String>();
		if (content != null) {
			Document doc = Jsoup.parse(content);
			Elements links = doc.select("a");
			for (Element link : links) {
				String pageUrl = link.attr("href");
				String linkText = link.text().trim();
				System.out.println("Link: " + pageUrl);
				System.out.println("Text: " + linkText);
				linksMap.put(linkText, pageUrl);
			}
		}
		return linksMap;

	}

	public String getTextContentOfMail(String content) {
		String text = null;
		if (content != null) {
			Document doc = Jsoup.parse(content);
			doc.body().wrap("<pre></pre>");
			text = doc.text();
			text = text.replaceAll("\u00A0", " ");
			System.out.println("Final Text" + text);
		}
		return text;
	}

	public String searchSpecificMail(String keyword, String sender, String location, String subjectOfMail) {
		String content = null, body = null;
		System.out.println("DOI : " + keyword + "----" + "Sender : " + sender + "----" + "Mail folder : " + location
				+ "---- Mail Subject : " + subjectOfMail);
		try {
			Message[] foundMessages = getSpecificMailList(folder, sender, subjectOfMail);
			System.out.println(foundMessages.length + "----");
			if (foundMessages.length == 0) {
				System.out.println("No Mails found for subject: " + subjectOfMail);
			} else {
				Message foundMess = getSpecificLatestMail(foundMessages, keyword);
				System.out.println("Subject:: " + foundMess.getSubject());
				System.out.println("Time:: " + foundMess.getReceivedDate());
				// timeStampOfSelectedEmail = foundMess.getReceivedDate();
				currentMailSubject = foundMess.getSubject();
				Object emailContent = foundMess.getContent();
				if (emailContent instanceof String) {
					body = (String) emailContent;
					System.out.println(body);
				} else if (emailContent instanceof Multipart) {

					// try {
					Multipart multipart = (Multipart) foundMess.getContent();
					for (int x = 0; x < multipart.getCount(); x++) {

						BodyPart bodyPart = multipart.getBodyPart(x);
						// Multipart m = (Multipart) bodyPart.getContent();
						// for (int j = 0; j < m.getCount(); j++) {
						// content = (String) m.getBodyPart(j).getContent();
						content = (String) multipart.getBodyPart(x).getContent();
						System.out.println(content);

						// }
						// COMMENTED because no nesting for multipart is present
					}

					// } catch (Exception ) {
					// System.out.println("Exception is " + e);
					// }
				}

				System.out.println("message reading completed...");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (content != null)
			return content;
		else
			return body;
	}

	private Message getSpecificLatestMail(Message[] foundMessages, String keyword) {
		Message latestMail = null;
		ArrayList<Message> specificMails = new ArrayList<Message>();

		if (foundMessages.length > 0) {
			try {
				for (int i = foundMessages.length - 1; i >= 0; i--) {
					Message message = foundMessages[i];
					System.out.println("Message Subject : " + message.getSubject() + message.getReceivedDate());
					// if(message.getSubject().contains("(") &&
					// message.getSubject().contains(")")){
					specificMails.add(message);
					// } commented due to change in requirement
				}

			} catch (MessagingException e) {
				System.out.println("Exception: " + e);
			}
		} else {
			System.out.println("No Mails found for DOI: " + keyword);
		}
		System.out.println("list size : " + specificMails.size());
		if (specificMails.size() == 0)
			System.out.println("No mails found for DOI: " + keyword);
		else {
			latestMail = specificMails.get(0);
		}
		return latestMail;
	}

	public Message[] getSpecificMailList(Folder folder, String sender, String subjectMail) {
		Message messages[] = null;
		try {
			SearchTerm searchCondition = new FromTerm(new InternetAddress(sender));
			SearchTerm mailSubject = new SubjectTerm(subjectMail);
			SearchTerm andTerm = new AndTerm(mailSubject, searchCondition);
			messages = folder.search(andTerm);

		} catch (MessagingException e) {
			return null;
		}

		return messages;
	}

	public void closeMail() {
		try {
			folder.close(true);
			store.close();
		} catch (MessagingException e) {
			e.printStackTrace();
		}

	}

	public Date parseDate(String since) {
		DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
		Date sinceDate = null;
		try {
			sinceDate = format.parse(since);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sinceDate;
	}

	public Map<String, String> getImagesFromEmail(String content) {
		Map<String, String> imageTitles = null;
		if (content != null) {
			imageTitles = new HashMap<String, String>();
			System.out.println("Fetching Images and Titles from Email...");
			Document doc = Jsoup.parse(content);
			for (Element e : doc.select("img")) {
				System.out.println(e.attr("src"));
				System.out.println(e.attr("title"));
				imageTitles.put(e.attr("src"), e.attr("title"));
			}
		}
		return imageTitles;

	}

	public List<String> getAllLinks(String content) {
		List<String> allLinks = new ArrayList<String>();
		System.out.println("Fetching Links from Email...");
		if (content != null) {
			Document doc = Jsoup.parse(content);
			for (Element links : doc.select("a:not(child)")) {
				allLinks.add(links.attr("href"));
				System.out.println(links.attr("href"));
			}
		}
		return allLinks;
	}

	public int getYearOfSelectedEmail() {
		int year = -1;
		try {
			Date date = timeStampOfSelectedEmail;
			LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			System.out.println("local:- " + localDate.getYear());
			year = localDate.getYear();

		} catch (NullPointerException ex) {
			System.out.println("Year for selected email not found!!!");
		}
		return year;
	}

}
