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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/*
 * Clase para extraer los datos de la url.
 */

public class Extractor {
	
	private static StringBuilder sb = new StringBuilder();
	private static String url = "";

	@SuppressWarnings("finally")
	public static String Extract(String url) {
		try {

			sb.delete(0, sb.length());
			Document doc = Jsoup.connect(url).get();
			Elements links = doc.select("a[href]");
			// 1. Busco emails en el primer enlace.
			getMails(doc.text());
			
			// 2. Por cada href encontrado busco emails.
			links.forEach((link) -> {
				isMail(link.attr("abs:href").trim());
				System.out.println("Evaluando ... : " + link.attr("abs:href").trim());
				getMails(link.attr("abs:href").trim());
			});
			/*
			for (Element link: links) {
				print("Evaluando ... : " + link.attr("abs:href").trim());
				getMails(link.attr("abs:href").trim());
			}
			*/
		} catch (Exception ex) {
			/* Ya veremos que devolvemos */
		} finally {
			//mailList.forEach((mail) -> {
			//	sb.append(mail).append(";");
			//});
			return sb.toString();
		}
	}
	
	/*
	 * A veces me llega el email por el href.
	 */
	private static void isMail(String text) {
		try {
			Pattern p = Pattern.compile("\\b[a-zA-Z0-9.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z0-9.-]+\\b");
			Matcher matcher = p.matcher(new URL(text).getPath());
			if (matcher.find()) 
				addMail(matcher.group(0));
			
		} catch (Exception ex) {
			
		}
	}

	private static void getMails(String text) {
		try {
			
			Document doc = Jsoup.connect(text).get();
			Pattern p = Pattern.compile("\\b[a-zA-Z0-9.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z0-9.-]+\\b");
			Matcher matcher = p.matcher(doc.text());

			while (matcher.find())
				addMail(matcher.group());

		} catch (Exception ex) {
			/* Ya veremos que devolvemos */
		}
	}
	
	private static void addMail(String targetValue) {
		if (!sb.toString().contains(targetValue)) {
			print(" ******************************** ");
			print(" ******************************** ");
			print("NUEVO EMAIL ENCONTRADO: " + targetValue);
			print(" ******************************** ");
			print(" ******************************** ");
			sb.append(targetValue).append(";");
		}
	}
	
	private static void print(String msg, Object... args) {
		System.out.println(String.format(msg, args));
	}
}
