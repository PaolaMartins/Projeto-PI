import ij.*;
import ij.plugin.PlugIn;
import ij.process.*;
import ij.gui.*;

import weka.classifiers.Classifier;
import weka.core.*;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;

public class ClassificadorFolhasPlugin implements PlugIn {

    public void run(String arg) {
        ImagePlus imagem = IJ.getImage();
        if (imagem == null) {
            IJ.showMessage("Erro", "Nenhuma imagem aberta.");
            return;
        }

        ImageProcessor ip = imagem.getProcessor();
        double areaTotal = ip.getWidth() * ip.getHeight();

        // [1] Extrair atributos reais
        double areaFolha = calcularAreaFolha(ip.duplicate());
        double areaManchas = calcularAreaManchas(ip.duplicate());
        double corMedia = calcularMediaCor(ip.duplicate());

        IJ.log("area_folha = " + areaFolha);
        IJ.log("area_manchas = " + areaManchas);
        IJ.log("cor_media = " + corMedia);


        try {
            // [2] Carregar modelo treinado
            Classifier modelo = (Classifier) weka.core.SerializationHelper.read("C:/Users/Home/Documents/USP2025/PI/GeradorArffFolhas/classificadorRandom2.model");

            // [3] Definir atributos e estrutura
            ArrayList<Attribute> atributos = new ArrayList<>();
            atributos.add(new Attribute("area_folha"));
            atributos.add(new Attribute("area_manchas"));
            atributos.add(new Attribute("cor_media"));
            ArrayList<String> classes = new ArrayList<>();
            classes.add("saudavel");
            classes.add("doente");
            atributos.add(new Attribute("classe", classes));

            Instances dados = new Instances("Folhas", atributos, 0);
            dados.setClassIndex(dados.numAttributes() - 1);

            // [4] Criar instância com os valores da imagem
           Instance nova = new DenseInstance(dados.numAttributes());
            nova.setValue(atributos.get(0), areaFolha);
            nova.setValue(atributos.get(1), areaManchas);
            nova.setValue(atributos.get(2), corMedia);
            nova.setMissing(atributos.get(3)); // classe desconhecida
            nova.setDataset(dados);            // <<< aqui, liga a instância ao dataset
            dados.add(nova);


            // [5] Classificar e mostrar resultado
            double resultado = modelo.classifyInstance(nova);
            String classePredita = classes.get((int) resultado);

            IJ.showMessage("Classificação da Folha", "Resultado: " + classePredita);

        } catch (Exception e) {
            e.printStackTrace();
            IJ.showMessage("Erro", "Falha ao classificar: " + e.getMessage());
        }
    }

    // ======= FUNÇÕES AUXILIARES =======

    public static double calcularAreaFolha(ImageProcessor ip) {
        ip = ip.convertToByte(true);
        ip.setAutoThreshold("Default");
        ByteProcessor bin = (ByteProcessor) ip.createMask();
        int contagem = 0;
        for (int y = 0; y < bin.getHeight(); y++) {
            for (int x = 0; x < bin.getWidth(); x++) {
                if (bin.get(x, y) == 255) contagem++;
            }
        }
        return contagem;
    }

    public static double calcularAreaManchas(ImageProcessor ip) {
        ip = ip.convertToByte(true);
        ip.blurGaussian(2);
        ip.setAutoThreshold("Li dark"); // tenta pegar regiões escuras
        ByteProcessor bin = (ByteProcessor) ip.createMask();
        int contagem = 0;
        for (int y = 0; y < bin.getHeight(); y++) {
            for (int x = 0; x < bin.getWidth(); x++) {
                if (bin.get(x, y) == 255) contagem++;
            }
        }
        return contagem;
    }

    public static double calcularMediaCor(ImageProcessor ip) {
        int soma = 0;
        int total = ip.getWidth() * ip.getHeight();
        for (int y = 0; y < ip.getHeight(); y++) {
            for (int x = 0; x < ip.getWidth(); x++) {
                int[] rgb = ip.getPixel(x, y, null);
                int media = (rgb[0] + rgb[1] + rgb[2]) / 3;
                soma += media;
            }
        }
        return (double) soma / total;
    }
}
