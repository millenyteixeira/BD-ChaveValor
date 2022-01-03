package base.simplebd;

import java.io.*;
import java.util.*;

public class HashExtensivel<K,V> {

    HashMap<Integer, Long> bucket = new HashMap<Integer, Long>();
    HashMap<Long,Integer> bucketSortKey = new HashMap<Long,Integer>();

    //cria hash com sortkey
    public void insertSK() {
    RandomAccessFile br13 = null;
    String line;
    String sortKey = "";

    int sk = 0;

    String filePath = "simpledb.db";
        try {
        br13 = new RandomAccessFile(filePath, "rw");
        long pointer = br13.getFilePointer();
        line = br13.readLine();
        while (line != null) {
            if(!line.equals("")) {
                String[] trimmed = line.split(";");
                sortKey = trimmed[1];
                sk = Integer.valueOf(sortKey);
                    bucketSortKey.put(pointer,sk);
            }
            pointer = br13.getFilePointer();
            line = br13.readLine();
        }
    } catch (IOException e) {
        e.printStackTrace();
    } finally {
        try {
            br13.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}//ok

    //cria hash com os index
    public void insert() {
        RandomAccessFile br12 = null;
        String line;
        String index = "";
        int idx = 1;

        String filePath = "simpledb.db";
        try {
            br12 = new RandomAccessFile(filePath, "rw");
            long pointer = br12.getFilePointer();
            line = br12.readLine();
            while (line != null) {
                if(!line.equals("")) {
                    String[] trimmed = line.split(";");
                    index = trimmed[0];
                    idx = Integer.valueOf(index);

                    if (!bucket.containsKey(idx)) {
                        bucket.put(idx, pointer);
                    }
                }
                pointer = br12.getFilePointer();
                line = br12.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br12.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }//ok

    void updateHash(Integer idx, Integer sortKey) {
        bucket.put(idx, searchHash(idx));
    } //ok

    Long searchHash(Integer idx) {

            for (Integer i : bucket.keySet()) {
                if (i == idx) {
                    return bucket.get(i);
                }
            }
            return null;
    }//ok



    void removeHash(Integer idx){
        bucket.remove(idx);

    }


    // key>X: objetos que possuem chave de ordenação maior que X.
    void listIdxMaior(Integer sk, int op){
        TreeMap<Long,Integer> newMap = new TreeMap<>();
        newMap.putAll(bucketSortKey);
        CRUD crud = new CRUD();
        if(op==1) {
            Iterator<Long> j= bucketSortKey.keySet().iterator();
            while(j.hasNext()){
                Long chave = j.next();
                Integer skt = bucketSortKey.get(chave);
                if (skt > sk && newMap.get(chave).equals(skt)){
                    crud.search(chave);
                }
            }

        }else if(op==2){
            TreeMap<Long,Integer> newMapR = reverseHash(bucketSortKey);

            Iterator<Long> j= bucketSortKey.keySet().iterator();
            while(j.hasNext()){
                Long chave = j.next();
                Integer skt = bucketSortKey.get(chave);
                if (skt > sk && newMapR.get(chave).equals(skt)){
                    crud.search(chave);
                }
            }
        }


    }
    //inverte a Hash obs: usar no método list();

    void listIdxMenor(Integer sk, int op){
        TreeMap<Long,Integer> newMap = new TreeMap<>();
        newMap.putAll(bucketSortKey);
        CRUD crud = new CRUD();
        if(op==1) {
            Iterator<Long> j= bucketSortKey.keySet().iterator();
            while(j.hasNext()){
                Long chave = j.next();
                Integer skt = bucketSortKey.get(chave);
                if (skt < sk && newMap.get(chave).equals(skt)){
                    crud.search(chave);
                }
            }
        }else if(op==2){
            TreeMap<Long,Integer> newMapR = reverseHash(bucketSortKey);

            Iterator<Long> j= bucketSortKey.keySet().iterator();
            while(j.hasNext()){
                Long chave = j.next();
                Integer skt = bucketSortKey.get(chave);
                if (skt < sk && newMapR.get(chave).equals(skt)){
                    crud.search(chave);
                }
            }
        }

    }
    void listIdxEqual(Integer sk,int op){
        //ordenaHash();
        TreeMap<Long,Integer> newMap = new TreeMap<>();
        newMap.putAll(bucketSortKey);
        CRUD crud = new CRUD();

        if(op==1) {
            Iterator<Long> j= bucketSortKey.keySet().iterator();
            while(j.hasNext()){
                Long chave = j.next();
                Integer skt = bucketSortKey.get(chave);
                if (skt == sk && newMap.get(chave).equals(skt)){
                    crud.search(chave);
                }
            }
        }else if(op==2){
            TreeMap<Long,Integer> newMapR = reverseHash(bucketSortKey);

            Iterator<Long> j= bucketSortKey.keySet().iterator();
            while(j.hasNext()){
                Long chave = j.next();
                Integer skt = bucketSortKey.get(chave);
                if (skt == sk && newMapR.get(chave).equals(skt)){
                    crud.search(chave);
                }
            }
        }

    }
    void listIdxMenorIgual(Integer sk, int op){
        TreeMap<Long,Integer> newMap = new TreeMap<>();
        newMap.putAll(bucketSortKey);
        CRUD crud = new CRUD();
        if(op==1) {
            Iterator<Long> j= bucketSortKey.keySet().iterator();
            while(j.hasNext()){
                Long chave = j.next();
                Integer skt = bucketSortKey.get(chave);
                if (skt <= sk && newMap.get(chave).equals(skt)){
                    crud.search(chave);
                }
            }
        }else if(op==2){
            TreeMap<Long,Integer> newMapR = reverseHash(bucketSortKey);

            Iterator<Long> j= bucketSortKey.keySet().iterator();
            while(j.hasNext()){
                Long chave = j.next();
                Integer skt = bucketSortKey.get(chave);
                if (skt <= sk && newMapR.get(chave).equals(skt)){
                    crud.search(chave);
                }
            }
        }

    }
    void listIdxMaiorIgual(Integer sk, int op){
        TreeMap<Long,Integer> newMap = new TreeMap<>();
        newMap.putAll(bucketSortKey);
        CRUD crud = new CRUD();
        if(op==1){
            Iterator<Long> j= bucketSortKey.keySet().iterator();
            while(j.hasNext()){
                Long chave = j.next();
                Integer skt = bucketSortKey.get(chave);
                if (skt >= sk && newMap.get(chave).equals(skt)){
                    crud.search(chave);
                }
            }
        }else if(op==2){

            TreeMap<Long,Integer> newMapR = reverseHash(bucketSortKey);

            Iterator<Long> j= bucketSortKey.keySet().iterator();
            while(j.hasNext()){
                Long chave = j.next();
                Integer skt = bucketSortKey.get(chave);
                if (skt >= sk && newMapR.get(chave).equals(skt)){
                    crud.search(chave);
                }
            }
        }


    }//ok

    TreeMap reverseHash(HashMap<Long,Integer> hash){
        TreeMap<Long,Integer> newMap = new TreeMap<>(Collections.reverseOrder());
        newMap.putAll(bucketSortKey);

        return newMap;
    } //ok
}

