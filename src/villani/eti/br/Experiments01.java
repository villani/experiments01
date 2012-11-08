package villani.eti.br;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.TreeMap;

public class Experiments01 {
	
	private static LogBuilder log;
	private static TreeMap<String,String> entradas;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		log = new LogBuilder("Experiments01.log");
		log.write("Iniciando experimento 1.3:");
		log.write("Obtendo as entradas do sistema a partir de conf.ini.");
		try{
			init();
		} catch(FileNotFoundException fnfe){
			log.write("Falha ao receber as entradas do sistema: " + fnfe.getMessage());
		}
		
		log.write("Entradas obtidas.");
		log.write("Lendo entradas:");
		log.write("- Conjunto de amostras: " + entradas.get("dataset"));
		log.write("- Caminho da pasta de imagens: " + entradas.get("folder"));
		log.write("- Arquivo com a relação imagem/IRMA: " + entradas.get("csv"));
		log.write("- Arquivo estrutura código irma: " + entradas.get("txt"));
		log.write("- Quantidade de imagens que serão utilizadas: " + entradas.get("images"));
		log.write("- Limite de pontos-chave obtidos: " + entradas.get("keypoints"));
		log.write("- Tamanho do histograma SIFT: " + entradas.get("histoSize"));
		
		log.write("Obtendo conjunto de amostras:");
		Amostras.setLog(log);
		Amostras.setEntradas(entradas);
		Amostras.obtem();
		
		log.write("Fim do experimento");

	}
	
	public static boolean init() throws FileNotFoundException{
		File conf = new File("./conf.ini");
		Scanner leitor = new Scanner(conf);
		entradas = new TreeMap<String,String>();
		while(leitor.hasNextLine()){
			String linha = leitor.nextLine();
			String parametros[] = linha.split("=");
			if(parametros.length < 2) continue;
			entradas.put(parametros[0], parametros[1]);
		}
		leitor.close();
		return true;
	}

}
