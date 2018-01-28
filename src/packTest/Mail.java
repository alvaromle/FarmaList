package packTest;

import packFarmaList.MailExtractor;

public class Mail {

	public static void main(String[] args) {

		String emails = MailExtractor.Extract("http://www.farmaciamonteagudo.com");
		
		System.out.println(emails);

	}

}
