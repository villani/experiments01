package villani.eti.br;

import ij.ImagePlus;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Vector;

import mpi.cbg.fly.Feature;
import mpi.cbg.fly.Filter;
import mpi.cbg.fly.FloatArray2D;
import mpi.cbg.fly.FloatArray2DSIFT;
import mpi.cbg.fly.ImageArrayConverter;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

public class Caracteristicas {
	
	private static LogBuilder log;
	private static TreeMap<String,String> entradas;
	private static File folder;
	private static File[] imagens;
	private static String dataset;
	private static int images;
	private static int keypoints;
	
	public static void setLog(LogBuilder log){
		Caracteristicas.log = log;
	}
	
	public static void setEntradas(TreeMap<String,String> entradas){
		Caracteristicas.entradas = entradas;
		folder = new File(Caracteristicas.entradas.get("folder"));
		dataset = Caracteristicas.entradas.get("dataset");
		images = Integer.parseInt(Caracteristicas.entradas.get("images"));
		keypoints = Integer.parseInt(Caracteristicas.entradas.get("keypoints"));
	}
	
	public static void obtemPontosChave(){
		if(!folder.exists()) log.write("- A pasta de imagens informada não existe: " + folder.getAbsolutePath());
		else {
			log.write("- Pasta de imagens encontrada: " + folder.getAbsolutePath());
			log.write("- Obtendo imagens da pasta " + folder.getName());
			imagens = folder.listFiles();
			
			log.write("Criando conjunto auxiliar:");
			log.write("- Definindo lista de atributos");
			ArrayList<Attribute> listaDeAtributos = new ArrayList<Attribute>();
			for(int i = 0; i < 128; i++){
				listaDeAtributos.add(new Attribute("feat" + i));
			}
			
			log.write("- Criando um conjunto de instancias que armazenará as imagens");
			Instances instancias = new Instances("sift",listaDeAtributos,10);
			
			log.write("- Criando ArffSaver para armazenar o conjunto auxiliar em arquivo");
			ArffSaver saver = new ArffSaver();
			File datasetAux = new File(dataset + "-aux.arff");
			try {
				saver.setFile(datasetAux);
			} catch (IOException e) {
				log.write("- Falha ao manipular o arquivo " + datasetAux.getAbsolutePath() + ". Erro: " + e.getMessage());
			}
			saver.setStructure(instancias);
			saver.setRetrieval(ArffSaver.INCREMENTAL);
			
			log.write("- Obtendo os pontos-chave das imagens");
			ArrayList<String> rotulosAuxiliar = new ArrayList<String>();
			int qtdeImagens = 0;
			for(File imagem : imagens){
				if(qtdeImagens == images) break;
				ImagePlus ip = new ImagePlus(imagem.getAbsolutePath());
				FloatArray2DSIFT sift = new FloatArray2DSIFT(4,8);
				FloatArray2D fa = ImageArrayConverter.ImageToFloatArray2D(ip.getProcessor().convertToFloat());
				Filter.enhance(fa, 1.0f);
				float initial_sigma = 1.6f;
				fa = Filter.computeGaussianFastMirror(fa, (float)Math.sqrt(initial_sigma * initial_sigma - 0.25));
				sift.init(fa, 3, initial_sigma, 64, 1024);
				Vector<Feature> pontosChave = sift.run(1024);
				int qtdePontosChave = 0;
				for(Feature ponto : pontosChave){
					if(qtdePontosChave == keypoints) break;
					Instance instancia = new DenseInstance(instancias.numAttributes());
					instancia.setDataset(instancias);
					for(int j = 0; j < ponto.descriptor.length; j++) instancia.setValue(j, ponto.descriptor[j]);
					try {
						saver.writeIncremental(instancia);
					} catch (IOException e) {
						log.write("- Falha ao salvar a instancia no conjunto auxiliar " + datasetAux.getAbsolutePath() + ". Erro: " + e.getMessage());
					}
					rotulosAuxiliar.add(imagem.getPath());
					qtdePontosChave++;
				}
				qtdeImagens++;
			}
			
			log.write("- Salvando em arquivo a lista de rótulos do conjunto auxiliar");
			File rotulos = new File(dataset + "-aux.labels");
			try {
				FileWriter escritor = new FileWriter(rotulos);
				for(String rotulo : rotulosAuxiliar){
					escritor.write(rotulo + "\n");					
				}
				escritor.close();
			} catch (IOException e) {
				log.write("- Falha ao salvar lista de rótulos do conjunto auxiliar: " + e.getMessage());
			}
			
		}
		 
		
	}

}
