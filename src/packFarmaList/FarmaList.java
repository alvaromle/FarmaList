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

public class FarmaList implements Runnable {
	
	private static boolean continuar;
	private static int contador;
	
	private static int total;

	private static File fichero;
	private static BufferedWriter bw;

	public static void main(String[] args) throws Exception {
		
		String linea = "";
		
		String path = "./src/UrlProvincias";
		fichero = new File(path);
		BufferedReader br = new BufferedReader(new FileReader(fichero));
		
		//int hashkey = 0;
		total = 0;
		
		while ((linea = br.readLine()) != null) {
			String pathFichero = "/Users/alvaro/Desktop/Provincias/Espana.csv"; //.concat(farmacia.getFileName(hashkey));
			bw = new BufferedWriter(new FileWriter(pathFichero));
			
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
			//hashkey += 1;
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
		
		while ((inputLine = in.readLine()) != null) {
			String html = inputLine;
			Document doc = Jsoup.parse(html);
			Elements entradas = doc.select("div.basicInfo");
			Elements cabeceras = entradas.select("div.col-xs-6.col-md-7");

			for (Element e : cabeceras) {
				Elements titular = e.select("div.titular");
				Elements telefono = e.select("div.telefono");
				Elements address = e.select("div.bip-links");  
				Elements adress = e.select("div.adress > a");
				
				name = titular.select("h1[itemprop=name]").text().replace(';', '-');
				webUrl = adress.attr("href");
				phone1 = telefono.select("span[itemprop=telephone]").text();
				
				if (phone1.trim().length() > 9 ) {
					String aux = phone1.substring(0, 9);
					phone2 = phone1.substring(10, phone1.length());
					phone1 = aux;
				}					
				
				streetAddress = address.select("span[itemprop=streetAddress]").text().replace(';', '-');
				postalCode = address.select("span[itemprop=postalCode]").text();
				addressLocality = address.select("span[itemprop=addressLocality]").text().replace(';', '-');
				addressState = address.select("span[itemprop=addressState]").text().replace(';', '-');
				
				
				if (!webUrl.isEmpty())
					emails = MailExtractor.Extract(webUrl);							
				
				bw.write(name + ";" + phone1 + ";" + phone2 + "; "+ streetAddress + ";" + postalCode + ";" 
						      + addressLocality + ";" + addressState + ";" + webUrl + ";" 
						      +  emails + ";\r\n" );
				
				System.out.println("Escribiendo datos en fichero: ");
				System.out.println("-------------------------------------------------------");
				System.out.println(name + ";" + phone1 + ";" + phone2 + ";"+ streetAddress + ";" 
				                        + postalCode + ";" + addressLocality + ";" + addressState 
				                        + ";" + webUrl + ";" + emails + " \r\n");
				System.out.println("-------------------------------------------------------");
				System.out.println("Registros tratados:" + total++);

			}
		}
		in.close();
	}
	
	@Override
	public void run() {
		
		
	}
}
