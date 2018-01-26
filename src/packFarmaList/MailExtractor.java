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

/**
 * Extrae los links de una url de internet. Abre la URL, carga el contenido en
 * un String y por medio de una expresion regular busca los href="enlace"
 * 
 * @author Chuidiang
 * 
 */
public class MailExtractor {

	private static String mails = "";
	private static boolean continuar = false;
	private static List<String> emails = new LinkedList<String>();

	public static String Extract(String url) {

		try {
			return extractContent(url);
		} catch (Exception ex) {
			return mails;
		}
		finally {
			mails = "";
		}
		// String content = extractContent(url);
		// showLinks(content);
		/*
		 * try { Document doc = Jsoup.connect(url).get(); return extract(doc);
		 * 
		 * } catch (Exception ex) { return ""; }
		 */
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
				if (!matcher.group(1).contains("facebook")) {
					Document doc = Jsoup.connect(matcher.group(1)).get();
					mails = extract(doc);	
				}
			}
			
			return mails;
			
		} catch (Exception ex) {
			return mails;
		}
	}

	private static String extract(Document doc) {
		try {
			Pattern p = Pattern.compile("\\b[a-zA-Z0-9.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z0-9.-]+\\b");
			Matcher matcher = p.matcher(doc.text());
			while (matcher.find()) {
				String mail = matcher.group();
				if (!mail.isEmpty() && !mail.contains(".css") && !mail.contains(".jpg") && !mails.contains("mail")) {
					System.out.println(" ******************************** ");
					System.out.println(" ******************************** ");
					System.out.println("EMAIL ENCONTRADO: " + mail);
					System.out.println(" ******************************** ");
					System.out.println(" ******************************** ");
					mails += mail.concat(";");
				}
			}
			return mails;

		} catch (Exception ex) {
			return mails;
		}
	}
	
	public static boolean getContinuar() {
		return continuar;
	}
	
	private static void addEmail(String mail) {
		emails.add(mail);
	}
	
	public static void removeEmail() {
		emails.remove(0);
	}
}
