package packFarmaList;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/*
 * Clase para extraer los datos de la url.
 */

public class Extractor {
	
	private static Map<String, String> map = new HashMap<String, String>();
	private static StringBuilder sb = new StringBuilder();
	
	private static int index = 0;

	@SuppressWarnings("finally")
	public static String Extract(String url) {
		try {

			map.clear();
			sb.delete(0, sb.length());
			index = 0;
			Document doc = Jsoup.connect(url).get();
			Elements links = doc.select("a[href]");
			// 1. Busco emails en el primer enlace.
			getMails(doc.text());
		
			// 2. Por cada href encontrado busco emails.
			links.forEach((link) -> {
				map.put(link.attr("abs:href").trim(), link.attr("abs:href").trim());
			});
			
			map.forEach((key, value) -> {
				index += 1;
				isMail(key);
				System.out.println("Evaluando ... " + index + " de " + map.size() + ": " +  key);
				getMails(key);
			});
			
		} catch (Exception ex) {
			/* Ya veremos que devolvemos */
		} finally {
			return sb.toString();
		}
	}
	
	/*
	 * A veces me llega el email por el href.
	 */
	private static void isMail(String text) {
		try {
			//Pattern p = Pattern.compile("\\b[a-zA-Z0-9.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z0-9.-]+\\b");
			Pattern p = Pattern.compile("^[_A-Za-z0-9-\\\\+]+(\\\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\\\.[A-Za-z0-9]+)*(\\\\.[A-Za-z]{2,})$;");
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
			System.out.println();
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
