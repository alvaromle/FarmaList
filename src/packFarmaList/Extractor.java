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
	private static List<String> mailList = new LinkedList<String>();
	private static String url = "";

	@SuppressWarnings("finally")
	static String Extract(String url) {
		try {
			sb.delete(0, sb.length());
			mailList.clear();
			Extractor.url = url;
			// ***** FILTRO 1
			Filtro1(url);
			extractContent(url);

		} catch (Exception ex) {
			/* Ya veremos que devolvemos */
		} finally {
			mailList.forEach((mail) -> {
				sb.append(mail).append(";");
			});
			return sb.toString();
		}

	}

	static void Filtro1(String url) {
		try {
			Document doc = Jsoup.connect(url).get();
			isMail(doc.text());

			Elements elements = doc.select("a[href]");
			String mail = "";
			for (Element e : elements) {
				try {
					mail = e.attr("href");
					extractMail(Jsoup.connect(mail).get());

				} catch (Exception ex) {
					//String full = mail.startsWith("/") ? url.concat(mail) : url.concat("/"+mail);
					//System.out.println("URL FULL: " + full);
					//extractMail(Jsoup.connect(full).get());
				}
				//showLinks(url.concat(mail));
				//isMail(mail);
			}		

		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	static void extractContent(String urlString) {
		try {
			URL url = new URL(urlString);
			URLConnection urlConnection = url.openConnection();
			InputStream is = urlConnection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String linea = br.readLine();
			System.out.println("Linea: " + linea);
			while (null != linea) {
				linea = br.readLine();
				System.out.println("Evaluando linea: " + linea);
				showLinks(linea); 
			}

		} catch (MalformedURLException ex) {
			System.out.println(ex.getMessage());
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		}
	}

	static void showLinks(String content) {

		try {			
			Pattern pattern = Pattern.compile("(?i)HREF\\s*=\\s*\"(.*?)\"");
			Matcher matcher = pattern.matcher(content);			
			while (matcher.find()) {
				String result = matcher.group(1);
				System.out.println("Evaluando: " + result);
				isMail(result);
				if (isValidHRef(result)) {
					Filtro1(result);
					System.out.println("Extrayendo datos de: " + result);
					Document doc = Jsoup.connect(result).get();
					extractMail(doc);
				}
			}

		} catch (MalformedURLException ex) {
			System.out.println(ex.getMessage());
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		}
	}

	static void extractMail(Document doc) {
		try {
			isMail(doc.text());
		} catch (Exception ex) {
			/* Ya veremos que devolvemos */
		}
	}

	static void isMail(String text) {
		try {
			Pattern p = Pattern.compile("\\b[a-zA-Z0-9.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z0-9.-]+\\b");
			//Matcher matcher = p.matcher(new URL(text).getPath());
			Matcher matcher = p.matcher(text);

			while (matcher.find()) {
				String mail = matcher.group();
				if (isValidEmail(mail))
					addMail(mail);
			}
		} catch (Exception ex) {
			/* Ya veremos que devolvemos */
		}
	}

	static void addMail(String targetValue) {
		if (!mailList.contains(targetValue)) {
			System.out.println(" ******************************** ");
			System.out.println(" ******************************** ");
			System.out.println("NUEVO EMAIL ENCONTRADO: " + targetValue);
			System.out.println(" ******************************** ");
			System.out.println(" ******************************** ");
			mailList.add(targetValue);
		}
	}

	static boolean findMail(String targetValue) {
		return mailList.contains(targetValue);
	}

	static boolean isValidEmail(String mail) {
		return (!mail.isEmpty() && (!mail.contains("css") || !mail.contains("jpg") || !mail.contains("png")
				|| !mail.contains("jpg") || !mail.contains("jpeg")));
	}

	static boolean isValidHRef(String href) {
		return !href.contains("facebook") && !href.contains("twitter") && !href.contains("instagram")
				&& !href.contains("ico") && !href.contains("css") && !href.contains("png") && !href.contains("google")
				&& !href.contains("opera") && !href.contains("mozilla");
	}

}
