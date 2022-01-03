package base.simplebd;

import java.util.PriorityQueue;

class Huffman {
    private static int R = 256;                             // tamanho do dicionário de caracteres para comparacao (ASCII extendido)


    //Classe referente aos nos da trie
    private static class NoTrie implements Comparable<NoTrie> {

        private char simbolo;
        private int frequencia;
        private NoTrie esq;
        private NoTrie dir;

        //Construtores:

        NoTrie(char simbolo, int frequencia, NoTrie esq, NoTrie dir) {
            this.simbolo = simbolo;
            this.frequencia = frequencia;
            this.esq = esq;
            this.dir = dir;
        }

        NoTrie(char simbolo) {
            this.simbolo=simbolo;
        }
        NoTrie(char simbolo, int frequencia) {
            this.simbolo = simbolo;
            this.frequencia = frequencia;
        }
        NoTrie(int frequencia, NoTrie esq, NoTrie dir) {
            this.frequencia = frequencia;
            this.esq = esq;
            this.dir = dir;
        }

        NoTrie(NoTrie esq, NoTrie dir) {
            this.esq = esq;
            this.dir = dir;
        }

        //metodo auxiliar para verificar se um no e folha
        boolean ehFolha() {
            return esq == null && dir == null;
        }

        //Para auxiliar na construcao da Trie ordena da frequencia mais alta para a mais baixa
        @Override
        public int compareTo(NoTrie o) {
            return this.frequencia - o.frequencia;
        }
    }

    /*Metodo que recebe uma mensagem e cria uma trie para essa mensagem com base
     * na frequencia de caracteres*/
    private static NoTrie criaTrie(int[] freq) {

        PriorityQueue<NoTrie> pq = new PriorityQueue<>();
        for (char c = 0; c < R; c++) {
            if (freq[c] > 0) {
                pq.add(new NoTrie(c, freq[c], null, null));
            }
        }
        while (pq.size() > 1) {                                //Combina duas tries para a criacao de um novo no na arvore
            NoTrie x = pq.remove();
            NoTrie y = pq.remove();
            NoTrie parent = new NoTrie('\0', x.frequencia + y.frequencia, x, y);
            pq.add(parent);
        }
        return pq.remove();                                 //remove a ultima trie presente na lista e retorna.
    }

    /*Metodo para criacao da tabela de codigos a partir da trie criada*/
    private static String[] criaCodigo(NoTrie n) {
        String[] tc = new String[R];
        criaCodigo(tc, n, "");
        return tc;
    }

    private static void criaCodigo(String[] tc, NoTrie n, String s) {
        if (n.ehFolha()) {
            tc[n.simbolo] = s;
            return;
        }
        criaCodigo(tc, n.esq, s + '0');
        criaCodigo(tc, n.dir, s + '1');
    }

    /*Metodo responsavel por escrever a trie no arquivo codificado para futura decodificacao*/
    private static void escreveTrie(NoTrie n, BinaryOut saida) {

        if (n.ehFolha()) {
            saida.write(true);
            saida.write(n.simbolo);
        } else {
            saida.write(false);
            escreveTrie(n.esq, saida);
            escreveTrie(n.dir, saida);
        }

    }

    /*metodo que realiza a compressao:
     * Recebe uma cadeia de bits, realizando a leitura de cada bit convertendo em um array de char*/
    public static void compresshuffman() {

        BinaryIn in = new BinaryIn("simpledb.db");
        BinaryOut out = new BinaryOut("simpledb.huffman");

        String entrada = in.readString();
        char[] mensagem = entrada.toCharArray();            //converte a mensagem recebida em um array de char.

        int[] freq = new int[R];

        for (int i = 0; i < mensagem.length; i++) {
            freq[mensagem[i]]++;
        }

        NoTrie trie = criaTrie(freq);                       //cria a trie baseada na frequencia de cada caractere.

        String[] tc = new String[R];
        criaCodigo(tc, trie, "");                           //cria a tabela de codigos para compressao

        escreveTrie(trie, out);                                  //grava a trie no arquivo de saida

        out.write(mensagem.length);                         //grava o tamanho da mensagem no arquivo de saida

        for (int i = 0; i < mensagem.length; i++) {
            String codigo = tc[mensagem[i]];
            for (int j = 0; j < codigo.length(); j++) {
                if (codigo.charAt(j) == '1') {
                    out.write(true);
                } else out.write(false);
            }
        }
        out.close();                                           //encerra a gravação do arquivo.
    }

    /*Metodo para decodificar o arquivo comprimido*/
    public static void decompresshuffman() {
        BinaryIn in2 = new BinaryIn("simpledb.huffman");
        BinaryOut out2 = new BinaryOut("simpledb.db");

        NoTrie trie = leTrie(in2);                                       // carrega os bits do arquivo para descompressao
        int n = in2.readInt();                                           //Tamanho da mensagem armazenada no arquivo
        for (int i = 0; i < n; ++i) {
            NoTrie no = trie;
            do {
                if (in2.readBoolean()) {
                    no = no.dir;
                } else
                    no = no.esq;
            } while (!no.ehFolha());
            out2.write(no.simbolo);
        }
        out2.close();
    }

    /*metodo para ler a trie gravada no arquivo comprimido*/
    private static NoTrie leTrie(BinaryIn inB) {

        if (inB.readBoolean()) {
            return new NoTrie(inB.readChar());
        } else {
            return new NoTrie(leTrie(inB), leTrie(inB));
        }
    }
}


