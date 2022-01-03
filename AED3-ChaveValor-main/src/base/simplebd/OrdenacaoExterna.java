package base.simplebd;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;


public class OrdenacaoExterna {


    //Tipo de dado para Registro, facilita a ordenação e organização na hora de gravar no arquivo.
    private static class Registro implements Comparable<Registro> {

        private int id;
        private String registro;

        Registro(String registro) {

            this.id = separaId(registro);
            this.registro = registro;
        }

        private static int separaId(String registro) {
            int id = 0;

            char[] mensagem = registro.toCharArray();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mensagem.length; i++) {

                if (mensagem[i] != ';') {
                    sb.append(mensagem[i]);
                } else {
                    id = Integer.parseInt(String.valueOf(sb));
                    return id;
                }
            }
            return id;
        }

        //para que o coparable funcione, permitindo fazer a ordenação mais facilmente.
        @Override
        public int compareTo(OrdenacaoExterna.Registro o) {
            return this.id - o.id;
        }
    }

    /**Metodo para escrever em arquivos conforme valor em string, nome do arquivo e numero do arquivo*/

    private static void escrever(String valor, String name, int blocoCt) {
        boolean flag = false;

        StringBuilder nome = new StringBuilder(name);
        nome.append(blocoCt);
        nome.append(".tmp");

        try {
            File file = new File(String.valueOf(nome));

            if (!file.exists()) {
                file.createNewFile();
                flag = true;
            }

            FileWriter fw = new FileWriter(file, true);
            BufferedWriter out = new BufferedWriter(fw);

            if (!flag) {
                out.newLine();
            }

            out.write(valor);
            out.flush();
            out.close();

        } catch (IOException ioe) {
            System.out.println("Exception occurred:");
            ioe.printStackTrace();
        }

    }

    /**metodo responsavel por realizar a abertura do simpledb.db e sua intercalação em dois arquivos de blocos ordenados.
    *  Onde "n" corresponde ao tamanho do bloco*/

    private static int etapaDistribuicao(int n) {
        int blocoCt = 1, registroCt = 0, maxID=0;                // Tamanho do bloco de leitura n registros, bloco de leitura, contador de registro
        String file = "simpledb.db";                             //Nome do arquivo

        Registro bloco[] = new Registro[n];

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {

                Registro rg = new Registro(line);

                bloco[registroCt] = rg;

                registroCt++;
                if (registroCt == n) {

                    Arrays.sort(bloco);

                    for (int c = 0; c < bloco.length; c++) {
                        escrever(bloco[c].registro, "arq", blocoCt);
                    }

                    if (blocoCt == 1) {

                        registroCt = 0;
                        blocoCt++;

                    } else if (blocoCt == 2) {

                        blocoCt = 1;
                        registroCt = 0;
                    }
                }
            }
            //para os casos em que o arquivo acaba antes de ter 4 registros no bloco
            if (registroCt > 0 && registroCt < n && line == null) {


                Registro blocob[] = new Registro[registroCt];

                for (int c = 0; c < registroCt; c++) {
                    blocob[c] = bloco[c];
                }
                Arrays.sort(blocob);

                for (int c = 0; c < blocob.length; c++) {
                    escrever(blocob[c].registro, "arq", blocoCt);
                }
                maxID = blocob[blocob.length - 1].id;
            }else if (line == null) {
                maxID = bloco[bloco.length - 1].id;
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return maxID;
    }

    private static int etapaIntercalacao(int n) {
        //etapa de intercalação:

        int blocoID = 3; //define o id do bloco de gravação
        int blocoCT = 0; // idefine o contador de itens do bloco de gravacao
        int blocoM = n; // tamanho maximo do bloco de gravacao.

        int contF1=0; //contador do total de itens gravados no arquivo 1 - utilizado para determinar se o processo deve ser repetido ou não.

        int rg1CT=0, rg2CT=0;

        //arquivos criados na etapa anterior a serem lidos e intercalados nessa etapa.
        String file1 = "arq1.tmp";
        String file2 = "arq2.tmp";

        try (BufferedReader br1 = new BufferedReader(new FileReader(file1));
             BufferedReader br2 = new BufferedReader(new FileReader(file2)))
        {
            String line1 = br1.readLine();
            rg1CT++;
            String line2 = br2.readLine();
            rg2CT++;

            Registro rg1 = new Registro(line1);
            Registro rg2 = new Registro(line2);

            while (line1!= null || line2!= null) {

                if (rg1.id!=0 && rg1.id <= rg2.id || (rg2CT > n/2 && rg1CT <= n/2) || (line1 != null && line2 == null)) {

                    escrever(rg1.registro, "arq", blocoID);
                    blocoCT++;
                    line1 = br1.readLine();
                    if (line1!=null) {
                        rg1 = new Registro(line1);
                    }else{
                        rg1 = new Registro("");
                    }

                    if (blocoID==3) {
                    contF1++;
                    }

                } else if (rg2.id!=0 && rg1.id > rg2.id || (rg1CT > n/2 && rg2CT <= n/2) || (line2 != null && line1 == null)) {
                    escrever(rg2.registro, "arq", blocoID);
                    blocoCT++;
                    line2 = br2.readLine();
                    if (line2 != null) {
                        rg2 = new Registro(line2);
                    }else{
                        rg2 = new Registro("");
                    }
                    if (blocoID==3) {
                        contF1++;
                    }
                }

                if (blocoCT == blocoM) {

                    rg1CT = 0;
                    rg2CT = 0;

                    if (blocoID == 4) {
                        blocoID = 3;
                        blocoCT=0;
                    } else {
                        blocoCT=0;
                        blocoID++;
                    }

                }
            }
        }  catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        File f1 = new File(file1);
        f1.delete();
        File f2 = new File(file2);
        f2.delete();

        try {
            Path source = Paths.get("arq3.tmp");
            Files.move(source,source.resolveSibling("arq1.tmp"));
        }catch(Exception e) {
            //System.out.println(e);
        }

        try {
            Path source = Paths.get("arq4.tmp");
            Files.move(source,source.resolveSibling("arq2.tmp"));
        }catch(Exception e) {
            //System.out.println(e);
        }
        return contF1;
    }

    public static void ordenacaoExterna() throws IOException {
        int max = 0; // ID do ultimo registro ordenado / quantidade total de registros.
        int n = 4; //Tamanho do bloco a ser lido ordenado e escrito em um arquivo temporario.
        int itensOrdenados=0;

        max = etapaDistribuicao(n);
        while (itensOrdenados<max) {
            n=n*2;
            itensOrdenados= etapaIntercalacao(n);
        }

        File f1 = new File("simpledb.db");
        f1.delete();

        try {
            Path source = Paths.get("arq1.tmp");
            Files.move(source,source.resolveSibling("simpledb.db"));
        }catch(Exception e) {
            //System.out.println(e);
        }

       f1 = new File("arq1.tmp");
        f1.delete();

    }
    }