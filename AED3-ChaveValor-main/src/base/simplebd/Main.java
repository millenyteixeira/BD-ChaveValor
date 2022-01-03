package base.simplebd;

import java.io.IOException;
import java.util.*;

import static base.simplebd.Huffman.*;
import static base.simplebd.Lzw.*;


public class Main {
    public static void main(String[] args) throws IOException {

        HashExtensivel hash = new HashExtensivel();
        CRUD crud = new CRUD();
        selectionSort select = new selectionSort();
        if (args.length == 0 || args[0].equals("--help")) {
            System.out.println("simpledb [cmd]\n" +
                    "  --insert=<sort-key,value> \n      Insere um objeto no banco de dados.\n" +
                    "  --remove=<key>\n      Remove do banco de dados o objeto identificado pela chave key.\n" +
                    "  --search=<key>\n      Busca no banco de dados objeto identificado pela chave key.\n" +
                    "  --update=<key,sort-key,value>\n      Atualiza o objeto que e identificado pela chave key. \n" +
                    "  --list=\"<expr>\"\n      Lista em ordem crescente todos os objetos que possam uma chave de ordenação que atenda aos critérios especificados pela expressão 'expr'.\n" +
                    "  --reverse-list=\"<expr>\"\n      Lista em ordem decrescente  todos os objetos que possam uma chave de ordenação que atenda aos critérios especificados pela expressão 'expr'.\n" +
                    "    * A expressão 'expr' deve aceitar qualquer operação logica simples envolvendo a chave:\n" +
                    "        key>X: objetos que possuem chave de ordenação maior que X.\n" +
                    "        key<X: objetos que possuem chave de ordenação menor que X.\n" +
                    "        key=X: objetos que possuem chave ordenação igual a X.\n" +
                    "        key>=X: objetos que possuem chave de ordenação maior ou igual que X.\n" +
                    "        key<=X: objetos que possuem chave de ordenação menor ou igual que X.\n" +
                    "    (expr deve estar contido de aspas pois os caracteres < e > são usados no linux como redirecionamento de saída) \n" +
                    "  --compress=[huffman|lzw]\n      Compacta os registros do banco de dados usando o algoritmo de Codificação de Huffman ou o Algoritmo de Compressão LZW. \n" +
                    "  --decompress=[huffman|lzw]\n      Descompacta os registros do banco de dados usando o algoritmo de Codificação de Huffman ou o Algoritmo de Compressão LZW. \n");
        } else {
            String[] arg = args[0].split("=");
            if (arg.length == 1) {
                System.out.println("Faltando argumentos para as operações. \nPara mais informações de uso, utilize a opção --help");
            }
            hash.insert();
            hash.insertSK();
            switch (arg[0]) {
                case "--insert":
                    String[] insertArg = arg[1].split(",");
                    if (insertArg.length == 2) {
                        crud.insert(Integer.valueOf(insertArg[0]), insertArg[1]); // sort-key,value
                        hash.insert();
                        hash.insertSK();
                    } else {
                        System.out.println("Número de argumentos incorreto para a operação. \nPara mais informações de uso, utilize a opção --help");
                    }
                    break;
                case "--remove":
                    crud.remove(Integer.valueOf(arg[1])); // arg[1] -> key
                    hash.removeHash(Integer.valueOf(arg[1]));
                    hash.insert();
                    hash.insertSK();
                    break;
                case "--search":
                    crud.search(hash.searchHash(Integer.valueOf(arg[1]))); // arg[1] -> key
                    break;
                case "--update":
                    String[] updateArg = arg[1].split(",");
                    if (updateArg.length == 3) {
                        crud.update(Integer.valueOf(updateArg[0]), Integer.valueOf(updateArg[1]), updateArg[2]); //key,sort-key,value
                        hash.insert();
                        hash.insertSK();
                    } else {
                        System.out.println("Número de argumentos incorreto para a operação. \nPara mais informações de uso, utilize a opção --help");
                    }
                    break;
                case "--list":
                    String[] n;
                    if (arg[1].matches("key<[0-9]+")) {
                        n = arg[1].split("<");
                        hash.listIdxMenor(Integer.valueOf(n[1]), 1);
                    } else if (arg[1].matches("key>[0-9]+")) {
                        n = arg[1].split(">");
                        hash.insertSK();
                        hash.listIdxMaior(Integer.valueOf(n[1]), 1);
                    } else if (arg.length == 3 && arg[1].matches("key") && arg[2].matches("[0-9]+")) { //lembrar que o '=' foi "comido" no split
                        hash.listIdxEqual(Integer.valueOf(arg[2]), 1);
                    } else if (arg.length == 3 && arg[1].matches("key<") && arg[2].matches("[0-9]+")) { //lembrar que o '=' foi "comido" no split
                        hash.listIdxMenorIgual(Integer.valueOf(arg[2]), 1);
                    } else if (arg.length == 3 && arg[1].matches("key>") && arg[2].matches("[0-9]+")) { //lembrar que o '=' foi "comido" no split
                        hash.listIdxMaiorIgual(Integer.valueOf(arg[2]), 1);
                    } else {
                        System.out.println("Opção não contemplada no simpledb\nPara mais informações utilize a opção --help");
                    }
                    break;
                case "--reverse-list":
                    if (arg[1].matches("key<[0-9]+")) {
                        n = arg[1].split("<");
                        hash.listIdxMenor(Integer.valueOf(n[1]), 2);
                    } else if (arg[1].matches("key+>[0-9]+")) {
                        n = arg[1].split(">");
                        hash.listIdxMaior(Integer.valueOf(n[1]), 2);
                    } else if (arg[1].matches("key") && arg[2].matches("[0-9]+")) { //lembrar que o '=' foi "comido" no split
                        hash.listIdxEqual(Integer.valueOf(arg[2]), 2);
                    } else if (arg.length == 3 && arg[1].matches("key<") && arg[2].matches("[0-9]+")) { //lembrar que o '=' foi "comido" no split
                        hash.listIdxMenorIgual(Integer.valueOf(arg[2]), 2);
                    } else if (arg.length == 3 && arg[1].matches("key+>") && arg[2].matches("[0-9]+")) { //lembrar que o '=' foi "comido" no split
                        hash.listIdxMaiorIgual(Integer.valueOf(arg[2]), 2);
                    } else {
                        System.out.println("Opção não contemplada no simpledb\nPara mais informações utilize a opção --help");
                    }
                    break;
                case "--compress":
                    if (arg[1].equals("lzw")) {
                        compressLZW();
                    } else if (arg[1].equals("huffman")) {
                        compresshuffman();
                    } else {
                        System.out.println("Opção não contemplada no simpledb\nPara mais informações utilize a opção --help");
                    }
                    break;
                case "--decompress":
                    if (arg[1].equals("lzw")) {
                        decompressLZW();
                    } else if (arg[1].equals("huffman")) {
                        decompresshuffman();
                    } else {
                        System.out.println("Opção não contemplada no simpledb\nPara mais informações utilize a opção --help");
                    }
                    break;
                default:
                    System.out.println("Opção não contemplada no simpledb\nPara mais informações utilize a opção --help");
                    break;
            }
        }

    }
}
