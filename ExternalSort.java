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
    public static final int BLOCK_SIZE = (int) (1e5);
    public static final int INT_BYTES = 4;

    public static DataInputStream getInp(String file)throws FileNotFoundException, IOException {
        return new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
    }
    public static DataOutputStream getOut(String file)throws FileNotFoundException, IOException {
        return new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file,false)));
    }
    public static void readFile(String fil)throws FileNotFoundException, IOException {
        DataInputStream dis = getInp(fil);
        while(dis.available()>0){
            System.out.print(dis.readInt()+" ");
        }
        System.out.println();
        dis.close();
    }
    public static void countSort(String f1,
            String f2,
            int min,
            int max,
            int[] cnt) throws 
                    FileNotFoundException, IOException {

        //RandomAccessFile raf = new RandomAccessFile("test4e.dat", "rw");
        //System.out.println(min + " vs " + max);
        DataOutputStream dos = getOut(f1);
        for(int i = min; i <= max; i++){
            for(int j = 0; j < cnt[OFFSET+i]; j++){
                //System.out.println("Printedddd");
                dos.writeInt(i);
            }
        }
        dos.flush();
        dos.close();
        //System.out.println("The checksum is: "+checkSum(f1));

    }
    public static void copyFromTo(String fil1, String fil2)throws FileNotFoundException, IOException {
        DataInputStream dis = getInp(fil1);
        DataOutputStream dos = getOut(fil2);
        while(dis.available()>0){
            dos.writeInt(dis.readInt());
        }
        dos.flush();
        dis.close();
        dos.close();
    }
    public static void mergeSort(String file1,
            String file2,
            int blockSize,
            int numOfInts) throws FileNotFoundException, IOException {
        int numOfBlocks = (int)Math.ceil((double)numOfInts / (double)blockSize);

        DataOutputStream dos = getOut(file2);
        //System.out.println(file2);
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
            dis[i] = getInp(file1);
            dis[i].skipBytes(i*blockSize<<2);
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
    public static void ezSort(String f1, int numOfInts)throws FileNotFoundException, IOException {
        DataInputStream dis = getInp(f1);
        int[] arr = new int[numOfInts];
        for(int i=0;i<numOfInts;i++){
            arr[i]=dis.readInt();
        }
        DataOutputStream dos = getOut(f1);
        Arrays.sort(arr);
        for(int i:arr){
            dos.writeInt(i);
        }
        dos.flush();
    }
    public static void sort(String f1, String f2) throws FileNotFoundException, IOException {
        RandomAccessFile f = new RandomAccessFile(f1,"r");
        DataInputStream dis = getInp(f1);
        DataOutputStream dos = getOut(f2);

        int numOfInts = (int)(f.length() >> 2);

        //System.out.println(numOfInts);
        //System.out.println(BLOCK_SIZE);
        if(numOfInts<BLOCK_SIZE){
            ezSort(f1, numOfInts);
            return;
        }

        int min=Integer.MAX_VALUE, max=Integer.MIN_VALUE;
        int[] arr=new int[BLOCK_SIZE];
        int[] cntSort= new int[2*OFFSET+1];
        int k,prev=Integer.MIN_VALUE;
        int cntElements=0;
        boolean sorted=true;
        boolean smallEnough=true;
        //System.out.println(f.length());
        //System.out.println(f.readInt()+ " ibaah " +f.readInt());
        for(int i=0; i<numOfInts; i++){
            k=dis.readInt();
            arr[cntElements]=k;
            cntElements++;
            if(BLOCK_SIZE==cntElements || i==numOfInts-1){
                //assert(smallEnough==false);
                if(smallEnough==false){
                    Arrays.sort(arr, 0, cntElements);
                    for(int p=0; p<cntElements; p++){
                        //System.out.println(i);
                        dos.writeInt(arr[p]);
                    }
                    dos.flush();
                }
                cntElements=0;
            }
            if(smallEnough==true && RANGE_MIN<=k && RANGE_MAX>=k){
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
        if (smallEnough) {
            countSort(f1,f2,min,max,cntSort);
            return;
        }
        //countSort(f1,f2,min,max);
        //prepare(f1,f2);
        mergeSort(f2,f1,BLOCK_SIZE,numOfInts);
        f.close();
        dis.close();
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
