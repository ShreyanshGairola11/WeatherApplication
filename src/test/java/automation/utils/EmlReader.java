package automation.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class EmlReader {

	// download path
	static String currentDir = System.getProperty("user.dir");
	static String filePath = currentDir + File.separator + "src" + File.separator + "test" + File.separator
			+ "resources" + File.separator + "smbData" + File.separator;

	public Document getEmailBodyAsHtmlDocument(String emlFileName) throws Exception {
		Properties props = System.getProperties();
		props.put("mail.host", "smtp.test.com");
		props.put("mail.transport.protocol", "smtp");

		Session mailSession = Session.getDefaultInstance(props, null);
		InputStream source = new FileInputStream(new File(filePath + emlFileName));
		MimeMessage message = new MimeMessage(mailSession, source);
		source.close();
		String htmlContent = (String) message.getContent();
		Document doc = Jsoup.parse(htmlContent);

		return doc;
	}

	public String getPasswordResetLinkFromEmail(Document doc, String username) {
		String link = null;
		Elements textSpans = doc.select("strong>span");
		for (Element textSpan : textSpans) {
			if (textSpan.text().contains(username)) {
				Elements links = doc.select("a");
				for (Element linkElement : links) {
					if (linkElement.text().equals("Reset your Purdue Global password")) {
						link = linkElement.attr("href");
					}
				}
			}
		}
		return link;
	}

	public void deleteEmlFile(String fileName) {
		File f = new File(filePath + fileName);
		if (f.delete())
		{
			System.out.println(f.getName() + " deleted");
		} else {
			System.out.println("failed");
		}
	}
}