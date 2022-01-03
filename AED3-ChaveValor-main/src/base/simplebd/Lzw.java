package base.simplebd;

import java.io.*;
import java.util.*;

public class Lzw {
    public static void compressLZW() throws IOException {

        File fArquivo = new File("simpledb.db");
        File fArquivocomp = new File("simpledb.lzw");
        FileWriter fwArquivo;
        FileWriter fwArquivocomp;
        BufferedWriter bw;
        BufferedReader br;
        ObjectOutputStream file = new ObjectOutputStream(new FileOutputStream("simpledb.lzw"));

        if (fArquivo.exists()) {
            fwArquivo = new FileWriter(fArquivo, true);
        } else {
            fwArquivo = new FileWriter(fArquivo);
        }
        if (fArquivocomp.exists()) {
            fwArquivocomp = new FileWriter(fArquivocomp, true);
        } else {
            fwArquivocomp = new FileWriter(fArquivocomp);
        }
        bw = new BufferedWriter(fwArquivocomp);
        br = new BufferedReader(new FileReader("simpledb.db"));
        String text = "";
        String line;
        while ((line = br.readLine()) != null) {
            text = text.concat(line);
            text = text.concat(";");
        }
        int dictam = 256;
        Map<String, Integer> dic = new HashMap<String, Integer>();
        for (int i = 0; i < 256; i++) {
            dic.put("" + (char) i, i);
        }
        String w = "";
        List<Integer> result = new ArrayList<Integer>();
        for (char c : text.toCharArray()) {
            String wc = w + c;
            if (dic.containsKey(wc))
                w = wc;
            else {
                result.add(dic.get(w));
                //Add wc to the dic.
                dic.put(wc, dictam++);
                w = "" + c;
            }
        }
        //Output the code for w.
        if (!w.equals("")) {
            result.add(dic.get(w));
        }
        file.writeObject(result.toString());
        br.close();
        bw.close();
        fwArquivo.close();
        file.close();
    }

    public static void decompressLZW() throws IOException {
        BufferedWriter bw = null;
        File fArquivo = null;
        FileWriter fwArquivo = null;
        int dictam = 256;
        String inputFile = "simpledb.lzw";
        String text = "";
        Map<Integer, String> dic = new HashMap<Integer, String>();
        InputStream inputStream = new FileInputStream(inputFile);

        fArquivo = new File("simpledb.db");
        fwArquivo = new FileWriter(fArquivo);

        bw = new BufferedWriter(fwArquivo);

        int data = inputStream.read();
        while ((data = inputStream.read()) != -1) {
            text = text.concat(String.valueOf((char) data));
        }
        text = text.substring(7);
        text = text.substring(0, text.length() - 1);
        for (int i = 0; i < 256; i++)
            dic.put(i, "" + (char) i);
        String[] texto = text.split(", ");
        List<Integer> compressed = new ArrayList<Integer>();
        for (int i = 0; i < texto.length; i++) {
            compressed.add(Integer.parseInt(texto[i]));
        }
        String w = "" + (char) (int) compressed.remove(0);
        StringBuffer result = new StringBuffer(w);
        for (int k : compressed) {
            String entry;
            if (dic.containsKey(k))
                entry = dic.get(k);
            else if (k == dictam)
                entry = w + w.charAt(0);
            else
                throw new IllegalArgumentException("Bad compressed k: " + k);

            result.append(entry);
            dic.put(dictam++, w + entry.charAt(0));

            w = entry;
        }
        String r=result.toString();
        String rr="";
        String [] split = r.split(";");
        rr=rr.concat(split[0] + ";" + split[1] + ";" + split[2]);
        for(int i=3;i<split.length; i+=3) {
            rr=rr.concat("\n" + split[i] + ";" + split[i+1] + ";" + split[i+2] );
        }
        bw.write(rr.toString());
        bw.close();
    }
}
