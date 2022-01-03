package base.simplebd;

import java.io.*;
import java.util.*;

public class selectionSort {
    public static void selectionCode(String[] arr, int n,int op) {
        BufferedWriter bw =null;
        for(int i = 0; i < n - 1; i++) {
            int min_index = i;
            String minStr = arr[i];

            //Compara string sucessora com antecessora e define o valor alfabetico, ou seja, a diferença numerica entre elas.
            for(int j = i + 1; j < n; j++) {
                if(arr[j].compareTo(minStr) < 0) {
                    minStr = arr[j];
                    min_index = j;
                }
            }

            // Se necessario trocar posição dentro do array para ordenação.
            if(min_index != i) {
                String temp = arr[min_index];
                arr[min_index] = arr[i];
                arr[i] = temp;
            }
        }

        int count =0;

        try {
            bw = new BufferedWriter(new FileWriter("simpledb.db"));

            if (op == 1) { //Crescente, a contagem serve para evitar linhas vazias dentro do arquivo.
                for (int k = 0; k < arr.length; k++) {
                    if(count == 0){
                        bw.write(arr[k]);
                    } else {
                        bw.write("\n" + arr[k]);
                    }
                    count++;
                }
            } else if (op == 2) { //Decrescente
                for (int k = arr.length - 1; k >= 0; k--) {
                    if(count == 0){
                        bw.write(arr[k]);
                    } else {
                        bw.write("\n" + arr[k]);
                    }
                    count++;
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void lerArquivo (int op){
        BufferedReader br = null;

        try{
            br = new BufferedReader(new FileReader("simpledb.db"));
            String line;
            String Conteudo = "";
            int contador = 0;

            // Conteudo = x;x;x, < a virgula é usada como referencia para o split, resultando na criação de um array funcional
            // para o metodo de ordenação por seleção.

            while ((line = br.readLine()) != null) {
                if(!line.equals("")) {
                    Conteudo = Conteudo + line + ",";
                    contador++;
                }
            }

            String[] trimmed = Conteudo.split(",");

            selectionCode(trimmed,contador,op);

        }catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
