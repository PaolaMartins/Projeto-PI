import ij.IJ;
import ij.ImagePlus;
import ij.io.DirectoryChooser;
import ij.plugin.PlugIn;
import ij.process.*;

import java.awt.Color;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;

public class GeradorArffFolhas implements PlugIn {

    static class Folha {
        double areaFolha;
        double areaManchas;
        double corMedia;
        String classe;

        public Folha(double areaFolha, double areaManchas, double corMedia, String classe) {
            this.areaFolha = areaFolha;
            this.areaManchas = areaManchas;
            this.corMedia = corMedia;
            this.classe = classe;
        }
    }

    public void run(String arg) {
        try {
            DirectoryChooser chooser = new DirectoryChooser("Selecione a pasta principal (com 'saudavel' e 'doente')");
            String basePath = chooser.getDirectory();
            if (basePath == null) {
                IJ.showMessage("Cancelado", "Nenhuma pasta foi selecionada.");
                return;
            }

            String[] classes = {"saudavel", "doente"};
            ArrayList<Folha> listaFolhas = new ArrayList<>();

            for (String classe : classes) {
                File pasta = new File(basePath + File.separator + classe);
                if (!pasta.exists()) {
                    IJ.showMessage("Erro", "Pasta não encontrada: " + pasta.getAbsolutePath());
                    return;
                }

                File[] imagens = pasta.listFiles();
                for (File imgFile : imagens) {
                    if (!imgFile.getName().endsWith(".jpg") && !imgFile.getName().endsWith(".png")) continue;

                    ImagePlus img = IJ.openImage(imgFile.getAbsolutePath());
                    if (img == null) {
                        IJ.log("Erro ao abrir imagem: " + imgFile.getName());
                        continue;
                    }

                    ImageProcessor ip = img.getProcessor();
                    double areaFolha = calcularAreaFolha(ip.duplicate());
                    double areaManchas = calcularAreaManchas(ip.duplicate());
                    double corMedia = calcularMediaCor(ip.duplicate());

                    listaFolhas.add(new Folha(areaFolha, areaManchas, corMedia, classe));
                }
            }

            salvarComoArff(listaFolhas, basePath + File.separator + "folhasCorrig.arff");
            IJ.showMessage("Sucesso", "Arquivo folhas.arff salvo em:\n" + basePath);

        } catch (Exception e) {
            e.printStackTrace();
            IJ.showMessage("Erro", "Erro ao executar plugin. Veja o console para detalhes.");
        }
    }

    // ======= MESMAS FUNÇÕES DO PLUGIN DE CLASSIFICAÇÃO =======

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

    public static void salvarComoArff(ArrayList<Folha> folhas, String caminhoCompleto) throws Exception {
        PrintWriter writer = new PrintWriter(caminhoCompleto);
        writer.println("@relation folhas_neem\n");
        writer.println("@attribute area_folha numeric");
        writer.println("@attribute area_manchas numeric");
        writer.println("@attribute cor_media numeric");
        writer.println("@attribute classe {saudavel,doente}\n");
        writer.println("@data");

        for (Folha f : folhas) {
            writer.printf(java.util.Locale.US, "%.2f,%.2f,%.2f,%s\n", f.areaFolha, f.areaManchas, f.corMedia, f.classe);

        }

        writer.close();
    }
}
