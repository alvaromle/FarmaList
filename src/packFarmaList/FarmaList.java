package packFarmaList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FarmaList {

	private static boolean continuar;
	private static int contador;

	private static final String destino = "/Users/alvaro/Desktop/Provincias/Alicante.csv";

	private static int total;
	private static BufferedWriter bw;

	public static void main(String[] args) throws Exception {

		String linea = "";
		File fichero = new File("./src/UrlProvincias");
		BufferedReader br = new BufferedReader(new FileReader(fichero));
		total = 1;

		while ((linea = br.readLine()) != null) {
			bw = new BufferedWriter(new FileWriter(destino));

			continuar = true;
			contador = 1;
			while (continuar) {
				try {
					String url = linea.concat(String.valueOf(contador));
					getFarmaciasURL(new URL(url));
					contador++;
				} catch (Exception ex) {
					continuar = false;
				}
			}
			bw.close();
		}
	}

	private static void getFarmaciasURL(URL url) throws Exception {

		URLConnection uc = url.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
		String inputLine;

		try {

			while ((inputLine = in.readLine()) != null) {
				String html = inputLine;
				Document doc = Jsoup.parse(html);
				Elements entradas = doc.select("div.listado-item");
				Elements cabeceras = entradas.select("div.box");
				Elements row = cabeceras.select("div.row");

				for (Element e : row) {
					Elements consulta = e.select("div.envio-consulta > a");
					String urlAux = consulta.attr("href");
					if (!urlAux.isEmpty()) {
						System.out.println("Obteniendo url de farmacia: " + urlAux);
						getInfoFarmacias(new URL(urlAux));
					}
				}
			}

		} catch (Exception ex) {
			continuar = false;

		} finally {
			in.close();
		}
	}

	private static void getInfoFarmacias(URL url) throws Exception {

		URLConnection uc = url.openConnection();

		BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
		String inputLine;

		String name = "";
		String phone1 = "";
		String phone2 = "";
		String streetAddress = "";
		String postalCode = "";
		String addressLocality = "";
		String addressState = "";
		String webUrl = "";
		String emails = "";

		StringBuilder sb;

		while ((inputLine = in.readLine()) != null) {
			String html = inputLine;
			Document doc = Jsoup.parse(html);
			Elements entradas = doc.select("div.basicInfo");
			Elements cabeceras = entradas.select("div.col-xs-6.col-md-7");

			for (Element e : cabeceras) {
				name = e.select("div.titular").select("h1[itemprop=name]").text().replace(';', '-');
				webUrl = e.select("div.adress > a").attr("href");
				phone1 = e.select("div.telefono").select("span[itemprop=telephone]").text();

				if (phone1.trim().length() > 9) {
					String aux = phone1.substring(0, 9);
					phone2 = phone1.substring(10, phone1.length());
					phone1 = aux;
				}

				// @formatter:off
				streetAddress = e.select("div.bip-links").select("span[itemprop=streetAddress]").text().replace(';','-');
				postalCode = e.select("div.bip-links").select("span[itemprop=postalCode]").text();
				addressLocality = e.select("div.bip-links").select("span[itemprop=addressLocality]").text().replace(';','-');
				addressState = e.select("div.bip-links").select("span[class=addressState]").text().replace(';', '-');
				// @formatter:on

				sb = new StringBuilder();
				sb.append(name).append(";").append(phone1).append(";").append(phone2).append(";").append(streetAddress)
						.append(";").append(postalCode).append(";").append(addressLocality).append(";")
						.append(addressState).append(";").append(webUrl).append(";");

				if (!webUrl.isEmpty()) {
					emails = Extractor.Extract(webUrl);
					sb.append(emails);
				}

				sb.append("\r\n");

				bw.write(sb.toString());
				bw.flush();

				System.out.println("Escribiendo datos en fichero: ");
				System.out.println("-------------------------------------------------------");
				System.out.println(sb.toString());
				System.out.println("-------------------------------------------------------");
				System.out.println("Registros tratados:" + total++);
				System.out.println();
			}
		}
		in.close();
	}
}
