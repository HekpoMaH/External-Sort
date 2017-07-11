package uk.ac.cam.dgg30.fjava.tick0;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.RandomAccessFile;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;


import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

public class ExternalSort {

    public static final int RANGE_MIN = (int) -1e5, RANGE_MAX = (int) 1e5;
    public static final int CNT_MAX = (int) (1e5);
    public static final int INT_BYTES = 4;

    public static void countSort(String f1,
            String f2,
            int min,
            int max,
            HashMap<Integer, Integer> cnt) throws 
                    FileNotFoundException, IOException {

        RandomAccessFile f = new RandomAccessFile(f1,"rw");
        DataInputStream dis = new DataInputStream(
                new BufferedInputStream(new FileInputStream(f.getFD())));
        //RandomAccessFile raf = new RandomAccessFile("test4e.dat", "rw");
        //System.out.println(min + " vs " + max);
        DataOutputStream dos = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(f1,false)));
        for(int i = min; i <= max; i++){
            if (!cnt.containsKey(i)) {
                continue;
            }
            else {
                int times=cnt.get(i);

                for(int j = 0; j < times; j++){
                    //System.out.println("Printedddd");
                    dos.writeInt(i);
                }
            }
        }
        dos.flush();
        dis.close();
        dos.close();
        //System.out.println("The checksum is: "+checkSum(f1));

    }
    public static void copyFromTo(String fil1, String fil2)throws FileNotFoundException, IOException {
        RandomAccessFile raf1 = new RandomAccessFile(fil1,"r");
        RandomAccessFile raf2 = new RandomAccessFile(fil2,"rw");
        DataInputStream dis = new DataInputStream(
                new BufferedInputStream(new FileInputStream(raf1.getFD())));
        DataOutputStream dos = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(fil2,false)));
        while(dis.available()>0){
            dos.writeInt(dis.readInt());
        }
        dos.flush();
        dis.close();
        dos.close();
        raf1.close();
        raf2.close();
    }
    public static void mergeSort(String file1,
            String file2,
            String fileA,
            String fileB,
            int blockSize) throws FileNotFoundException, IOException {

        RandomAccessFile rafFile1 = new RandomAccessFile(file1,"rw");
        int numOfInts = (int)(rafFile1.length() / (long)INT_BYTES);
        int numOfBlocks = (int)Math.ceil((double)numOfInts / (double)blockSize);

        DataOutputStream dos = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(file2,false)));
        PriorityQueue<PriorityQueueElement> pq=new PriorityQueue<PriorityQueueElement>();
        DataInputStream[] dis = new DataInputStream[numOfBlocks];

        int[] startBlock = new int[numOfBlocks];
        int[] endBlock = new int[numOfBlocks];

        for(int i=0; i<numOfBlocks; i++){
            startBlock[i]=i*blockSize;
            endBlock[i]=Math.min((i+1)*blockSize,numOfInts);
            //System.out.println(startBlock[i] + " " + endBlock[i] + " ");
            dis[i] = new DataInputStream(
                    new BufferedInputStream(new FileInputStream(file1)));
            dis[i].skipBytes(INT_BYTES*startBlock[i]);
            //System.out.println(dis[i].readInt());
            pq.add(new PriorityQueueElement(dis[i].readInt(), i));
        }
        
        //System.out.println(pq);
        while(pq.size()>0){
            PriorityQueueElement top=pq.poll();
            dos.writeInt(top.getVal());
            if(startBlock[top.getIdx()]+1 < endBlock[top.getIdx()]){
                startBlock[top.getIdx()]++;
                pq.add(new PriorityQueueElement(dis[top.getIdx()].readInt(), top.getIdx()));
            }
            //System.out.println(pq);
        }
        dos.flush();
        //System.out.println("n="+numOfBlocks+" ni="+numOfInts);
    }
    public static void sort(String f1, String f2) throws FileNotFoundException, IOException {
        RandomAccessFile f = null;
        DataInputStream d = null;
        RandomAccessFile raf2 = new RandomAccessFile(f2,"rw");
        DataOutputStream dos = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(f2,false)));
        f = new RandomAccessFile(f1,"r");
        int min=Integer.MAX_VALUE, max=Integer.MIN_VALUE;
        d = new DataInputStream(
                new BufferedInputStream(new FileInputStream(f.getFD())));

        ArrayList<Integer> lst = new ArrayList<Integer>();
        HashMap<Integer, Integer> cntSortMap = new HashMap<Integer, Integer>();
        int k,cnt=0,prev=Integer.MIN_VALUE;
        int cntElements=0;
        boolean sorted=true;
        f.seek(0);
        //System.out.println(f.length());
        //System.out.println(f.readInt()+ " ibaah " +f.readInt());
        while (d.available() > 0) {
            k=d.readInt();
            lst.add(k);
            cntElements++;
            if(CNT_MAX==cntElements){
                Collections.sort(lst);
                for(int i:lst){
                    dos.writeInt(i);
                }
                dos.flush();
                lst.clear();
                cntElements=0;
            }
            if(RANGE_MIN<=k && RANGE_MAX>=k){
                if( !cntSortMap.containsKey(k) ){
                    cntSortMap.put(k, 1);
                }
                else {
                    cntSortMap.put(k, cntSortMap.get(k)+1);
                }
            }
            //System.out.print(k+" ");
            if (k<prev)
                sorted=false;
            prev=k;
            if (k<min)
                min=k;
            if (k>max)
                max=k;
            cnt++;
        }
        if(cntElements!=0){
            if(cnt<=CNT_MAX){
                //thats the case when all input numbers are less than 1e5
                dos.close();
                dos = new DataOutputStream(
                    new BufferedOutputStream(new FileOutputStream(f1,false)));

            }
            Collections.sort(lst);
            for(int i:lst){
                dos.writeInt(i);
            }
            dos.flush();
            lst.clear();
            cntElements=0;
            //thats the case when all input numbers are less than 1e5
            if(cnt<=CNT_MAX)
                return;
        }
        if (sorted) {
            //System.err.println("SORTED");
            return;
        }

        //System.out.println();
        //System.out.println(cnt+"\n"+min+" "+max);
        if (RANGE_MIN<=min && max<=RANGE_MAX) {
            countSort(f1,f2,min,max,cntSortMap);
            return;
        }
        //countSort(f1,f2,min,max);
        //prepare(f1,f2);
        mergeSort(f2,f1,f1,f2,CNT_MAX);
        f.close();
        d.close();
    }

    private static String byteToHex(byte b) {
        String r = Integer.toHexString(b);
        if (r.length() == 8) {
            return r.substring(6);
        }
        return r;
    }

    public static String checkSum(String f) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            DigestInputStream ds = new DigestInputStream(new FileInputStream(f), md);
            byte[] b = new byte[512];
            while (ds.read(b) != -1)
                ;

            String computed = "";
            for(byte v : md.digest()) 
                computed += byteToHex(v);
            return computed;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "<error computing checksum>";
    }

    public static void main(String[] args) throws Exception {
        String f1 = args[0];
        String f2 = args[1];
        sort(f1, f2);
        System.out.println("The checksum is: "+checkSum(f1));
    }
}
