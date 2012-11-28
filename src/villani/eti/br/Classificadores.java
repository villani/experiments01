package villani.eti.br;

import java.io.FileWriter;
import java.util.TreeMap;

import mulan.classifier.lazy.BRkNN;
import mulan.classifier.lazy.MLkNN;
import mulan.data.MultiLabelInstances;
import mulan.evaluation.Evaluator;
import mulan.evaluation.MultipleEvaluation;

public class Classificadores {

	private static LogBuilder log;
	private static TreeMap<String, String> entradas;
	private static String dataset;

	public static void setLog(LogBuilder log) {
		Classificadores.log = log;
	}

	public static void setEntradas(TreeMap<String, String> entradas) {
		Classificadores.entradas = entradas;
		dataset = Classificadores.entradas.get("dataset");

	}

	public static void avalia(MultiLabelInstances instanciasML) {
		log.write("- Instanciando classificadores multirrótulo");

		log.write("- Instanciando MLkNN");
		MLkNN classificador01 = new MLkNN();

		log.write("- Instanciando BRkNN");
		BRkNN classificador02 = new BRkNN(10);

		log.write("- Instanciando avaliador");
		Evaluator avaliador = new Evaluator();
		int numFolds = 10;

		log.write("- Instanciando registradores dos resultados");
		MultipleEvaluation avaliacao;
		FileWriter resultados;
		try {
			resultados = new FileWriter(dataset + ".results");
			log.write("- Avaliando classificador " + classificador01.getClass());
			avaliacao = avaliador.crossValidate(classificador01, instanciasML, numFolds);
			resultados.write("RESULTADOS DO CLASSIFICADOR " + classificador01.getClass());
			resultados.write("\n=========================================\n\n");
			resultados.write(avaliacao.toString());
			resultados.write("\n=========================================\n\n");
			log.write("- Avaliando classificador " + classificador02.getClass());
			avaliacao = avaliador.crossValidate(classificador02, instanciasML, numFolds);
			resultados.write("RESULTADOS DO CLASSIFICADOR " + classificador02.getClass());
			resultados.write("\n=========================================\n\n");
			resultados.write(avaliacao.toString());
			resultados.write("\n=========================================\n\n");
			resultados.close();
		} catch (Exception e) {
			log.write("- Falha ao instancias registradores: " + e.getMessage());
		}
	}

}
