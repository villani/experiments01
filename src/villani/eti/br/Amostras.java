package villani.eti.br;

import java.io.File;
import java.util.TreeMap;

public class Amostras {
	
	private static LogBuilder log;
	private static TreeMap<String,String> entradas;
	
	public static void setLog(LogBuilder log){
		Amostras.log = log;
	}
	
	public static void setEntradas(TreeMap<String,String> entradas){
		Amostras.entradas = entradas;
	}
	
	public static void obtem(){
		File dataset = new File(entradas.get("dataset") + ".arff");
		if(dataset.exists()) log.write("- Conjunto de amostras encontrado em: " + dataset.getAbsolutePath());
		else {
			log.write("- O conjunto de amostras ainda não existe em: " + dataset.getAbsolutePath());
			log.write("Obtendo novo conjunto de amostras: ");
			Caracteristicas.setLog(log);
			Caracteristicas.setEntradas(entradas);
			Caracteristicas.obtemHistogramaSIFT();
		}
	}
	
	

}
