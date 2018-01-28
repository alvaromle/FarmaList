package packFarmaList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class MailExtractor {

	private static StringBuilder emails;
	private static List<String> mailList;

	public static String Extract(String url) {
		try {
			emails = new StringBuilder();
			mailList = new LinkedList<String>();
			return extractContent(url);
		} catch (Exception ex) {
			return emails.toString();
		}
	}

	private static String extractContent(String urlString) throws MalformedURLException, IOException {
		URL url = new URL(urlString);
		String content = "";
		URLConnection urlConnection = url.openConnection();
		InputStream is = urlConnection.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String linea = br.readLine();
		while (null != linea) {
			linea = br.readLine();
			content += showLinks(linea);
		}
		return content;
	}

	private static String showLinks(String content) {
		String mails = "";
		try {
			Pattern pattern = Pattern.compile("(?i)HREF\\s*=\\s*\"(.*?)\"");
			Matcher matcher = pattern.matcher(content);
			while (matcher.find()) {
				System.out.println("Extrayendo datos de: " + matcher.group(1));
				if (!matcher.group(1).contains("facebook") || !matcher.group(1).contains("twitter") 
						                                   || !matcher.group(1).contains("instagram")
						                                   || !matcher.group(1).contains(".ico")
						                                   || !matcher.group(1).contains(".css")
						                                   || !matcher.group(1).contains(".png")
						                                   || !matcher.group(1).contains(".google")
						                                   || !matcher.group(1).contains(".opera")
						                                   || !matcher.group(1).contains(".mozilla")) {
					Document doc = Jsoup.connect(matcher.group(1)).get();
					mails = extractMail(doc);
				}
			}

			return mails;

		} catch (Exception ex) {
			return mails;
		}
	}

	private static String extractMail(Document doc) {
		try {
			StringBuilder sb = new StringBuilder();
			Pattern p = Pattern.compile("\\b[a-zA-Z0-9.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z0-9.-]+\\b");
			Matcher matcher = p.matcher(doc.text());
			while (matcher.find()) {
				String mail = matcher.group();
				if (!mail.isEmpty() && 
						( !mail.contains("css") || !mail.contains("jpg") || !mail.contains("png")  || !mail.contains("jpeg")  )) {
					
					if (!findMail(mail)) {
						System.out.println(" ******************************** ");
						System.out.println(" ******************************** ");
						System.out.println("EMAIL ENCONTRADO: " + mail);
						System.out.println(" ******************************** ");
						System.out.println(" ******************************** ");
						sb.append(mail).append(";");
						addMail(mail);
					}					
				}
			}
			return sb.toString();

		} catch (Exception ex) {
			return emails.toString();
		}
	}
	
	public static void initStringBuilder() {
		emails = new StringBuilder();
	}
	
	public static void initLinkedList() {
		mailList.clear();
	}
	
	private static void addMail (String mail) {
		mailList.add(mail);
	}

	private static boolean findMail (String targetValue) {
		return mailList.contains(targetValue);
	}
}
