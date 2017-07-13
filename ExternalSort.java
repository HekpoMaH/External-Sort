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
import java.util.LinkedList;
import java.util.Collections;
import java.util.Arrays;
import java.util.PriorityQueue;

public class ExternalSort {

    public static final int RANGE_MIN = (int) -1e5, RANGE_MAX = (int) 1e5;
    public static final int OFFSET = (int) 1e5;
    public static final int BLOCK_SIZE = (int) (1<<19);
    public static final int INT_BYTES = 4;

    public static void readFile(String fil)throws FileNotFoundException, IOException {
        RandomAccessFile raf = new RandomAccessFile(fil,"rw");
        DataInputStream dis = new DataInputStream(
                new BufferedInputStream(new FileInputStream(raf.getFD())));
        while(dis.available()>0){
            System.out.print(dis.readInt());
        }
        System.out.println();
        dis.close();
        raf.close();
    }
    public static void countSort(String f1,
            String f2,
            int min,
            int max,
            int[] cnt) throws 
                    FileNotFoundException, IOException {

        RandomAccessFile f = new RandomAccessFile(f1,"rw");
        DataInputStream dis = new DataInputStream(
                new BufferedInputStream(new FileInputStream(f.getFD())));
        //RandomAccessFile raf = new RandomAccessFile("test4e.dat", "rw");
        //System.out.println(min + " vs " + max);
        DataOutputStream dos = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(f1,false)));
        for(int i = min; i <= max; i++){
            for(int j = 0; j < cnt[OFFSET+i]; j++){
                //System.out.println("Printedddd");
                dos.writeInt(i);
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
        int numOfInts = (int)(rafFile1.length() >> 2);
        int numOfBlocks = (int)Math.ceil((double)numOfInts / (double)blockSize);

        DataOutputStream dos = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(file2,false)));
        PriorityQueue<PriorityQueueElement> pq=new PriorityQueue<PriorityQueueElement>();
        DataInputStream[] dis = new DataInputStream[numOfBlocks];

        int[] startBlock = new int[numOfBlocks];
        int[] endBlock = new int[numOfBlocks];

        int[] numLeft = new int[numOfBlocks];
        for(int i=0; i<numOfBlocks; i++){
            numLeft[i]=blockSize-1;
            //startBlock[i]=i*blockSize;
            //endBlock[i]=Math.min((i+1)*blockSize,numOfInts);
            //System.out.println(startBlock[i] + " " + endBlock[i] + " ");
            dis[i] = new DataInputStream(
                    new BufferedInputStream(new FileInputStream(file1)));
            dis[i].skipBytes(INT_BYTES*i*blockSize);

            //System.out.println(dis[i].readInt());
            pq.add(new PriorityQueueElement(dis[i].readInt(), i));
        }
        numLeft[numOfBlocks-1]=numOfInts-(numOfBlocks-1)*blockSize-1;
        //System.out.println(pq);
        int idx;
        while(pq.size()>0){
            PriorityQueueElement top=pq.poll();
            dos.writeInt(top.getVal());
            idx=top.getIdx();
            if(numLeft[idx]>0){
                numLeft[idx]--;
                pq.add(new PriorityQueueElement(dis[idx].readInt(), idx));
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
        f = new RandomAccessFile(f1,"r");
        int min=Integer.MAX_VALUE, max=Integer.MIN_VALUE;
        d = new DataInputStream(
                new BufferedInputStream(new FileInputStream(f.getFD())));
        DataOutputStream dos = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(f2,false)));

        int[] arr=new int[BLOCK_SIZE];
        int[] cntSort= new int[2*OFFSET+1];
        int k,prev=Integer.MIN_VALUE;
        int cntElements=0;
        int numOfInts = (int)(f.length() >> 2);
        boolean sorted=true;
        boolean smallEnough=true;
        //System.out.println(f.length());
        //System.out.println(f.readInt()+ " ibaah " +f.readInt());
        for(int i=0; i<numOfInts; i++){
            k=d.readInt();
            arr[cntElements]=k;
            cntElements++;
            if(BLOCK_SIZE==cntElements){
                if(smallEnough==false){
                    Arrays.sort(arr);
                    for(int p:arr){
                        dos.writeInt(p);
                    }
                    dos.flush();
                }
                cntElements=0;
            }
            if(smallEnough == true && RANGE_MIN<=k && RANGE_MAX>=k){
                cntSort[OFFSET+k]++;
            }
            else 
                smallEnough=false;
            //System.out.print(k+" ");
            if(smallEnough){
                if (k<min)
                    min=k;
                if (k>max)
                    max=k;
            }
        }
        //System.out.println(f.length());
        if(cntElements!=0){
            if(f.length()<=INT_BYTES*BLOCK_SIZE){
                //thats the case when all input numbers are less than 1e5
                dos.close();
                dos = new DataOutputStream(
                    new BufferedOutputStream(new FileOutputStream(f1,false)));

            }
            Arrays.sort(arr,0,cntElements);
            for(int i=0; i<cntElements; i++){
                //System.out.println(i);
                dos.writeInt(arr[i]);
            }
            dos.flush();
            cntElements=0;
            //thats the case when all input numbers are less than 1e5
            if(f.length()<=INT_BYTES*BLOCK_SIZE)
                return;
        }
        //System.out.println();
        //System.out.println(cnt+"\n"+min+" "+max);
        if (smallEnough) {
            countSort(f1,f2,min,max,cntSort);
            return;
        }
        //countSort(f1,f2,min,max);
        //prepare(f1,f2);
        mergeSort(f2,f1,f1,f2,BLOCK_SIZE);
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
