package packTest;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import packFarmaList.MailExtractor;

public class Main {

	public static void main(String[] args) {
		try {
			Main.GetMails("http://farmaciapereda.com/");
			//String mails = MailExtractor.Extract("https://www.farmaciaespinosanarvaiza.com/");
			//		System.out.println(mails);
		} catch (Exception ex) {
			System.err.println("Error");
		}
	}

	private static void GetMails(String url) {
		try {
			Document doc = Jsoup.connect(url).get();
			Pattern p = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+");
			Matcher matcher = p.matcher(doc.text());
			Set<String> emails = new HashSet<String>();
			while (matcher.find()) {
				emails.add(matcher.group());
			}
			
	        Set<String> links = new HashSet<String>();

	        Elements elements = doc.select("a[href]");
	        for (Element e : elements) {
	            links.add(e.attr("href"));
	        }
	        
	        System.out.println("Emails: " + emails);
	        System.out.println("Links: " + links);

		} catch (Exception ex) {
			System.err.println("Error: " + ex.getMessage());
		}
	}

}
