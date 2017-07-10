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

public class ExternalSort {

    public static final int RANGE_MIN = (int) -1e5, RANGE_MAX = (int) 1e5;
    public static final int CNT_MAX = (int) 1e5;
    public static final int INT_BYTES = 4;

    public static void countSort(String f1, String f2, int min, int max) throws 
                    FileNotFoundException, IOException {

        RandomAccessFile f = new RandomAccessFile(f1,"rw");
        DataInputStream dis = new DataInputStream(
                new BufferedInputStream(new FileInputStream(f.getFD())));
        //RandomAccessFile raf = new RandomAccessFile("test4e.dat", "rw");
        HashMap<Integer, Integer> cnt = new HashMap<Integer, Integer>();
        while (dis.available()>0){
            int k=dis.readInt();
            if( !cnt.containsKey(k) ){
                cnt.put(k, 1);
            }
            else {
                cnt.put(k, cnt.get(k)+1);
            }
            //System.out.println(k + " " + cnt.get(k));
        }
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
    public static void ezSort(String f1, String f2, int min, int max) throws 
                    FileNotFoundException, IOException {

        RandomAccessFile f = new RandomAccessFile(f1,"rw");
        DataInputStream dis = new DataInputStream(
                new BufferedInputStream(new FileInputStream(f.getFD())));
        //RandomAccessFile raf = new RandomAccessFile("test4e.dat", "rw");
        ArrayList<Integer> lst = new ArrayList<Integer>();
        while (dis.available()>0){
            int k=dis.readInt();
            lst.add(k);
        }
        Collections.sort(lst);
        DataOutputStream dos = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(f1,false)));
        for(int i:lst){
            dos.writeInt(i);
            //dos.flush();
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
    public static void mergeSort(String f1,
            String f2,
            String fileA,
            String fileB,
            int startBlockSz) throws FileNotFoundException, IOException {
        //1 6 2 5 4 3
        //   |   |
        //System.out.println("DEIBA");
        RandomAccessFile rafFile1 ;
        RandomAccessFile rafFile2 ;
        int blockSize = startBlockSz;
        rafFile1 = new RandomAccessFile(f1, "rw");
        int numOfInts=(int)(rafFile1.length()/(long)INT_BYTES);
        int debugCNT=0;
        while(blockSize <= numOfInts){
            //System.out.println("I hv "+numOfInts+" numbers");
            //System.out.println(f1+" "+f2);
            RandomAccessFile rafFile1Helper = new RandomAccessFile(f1, "r");
            rafFile1 = new RandomAccessFile(f1, "r");
            rafFile2 = new RandomAccessFile(f2, "rw");
            DataInputStream disFile1Pointer1 = new DataInputStream(
                    new BufferedInputStream(new FileInputStream(rafFile1.getFD())));
            DataInputStream disFile1Pointer2 = new DataInputStream(
                    new BufferedInputStream(new FileInputStream(rafFile1Helper.getFD())));
            DataOutputStream dosFile2 = new DataOutputStream(
                    new BufferedOutputStream(new FileOutputStream(f2,false)));
            disFile1Pointer2.skipBytes(blockSize*INT_BYTES);
            //System.out.print("reading ");readFile(f1);

            int startBlock1=0,startBlock2=blockSize;
            int endBlock1=Math.min(blockSize, numOfInts);
            int endBlock2=Math.min(startBlock2+blockSize, numOfInts);
            int ptr1=0, ptr2=blockSize;

            while(startBlock1<numOfInts){
                //System.out.println("started");
                int a=disFile1Pointer1.readInt();
                int b=disFile1Pointer2.available()>0 ? disFile1Pointer2.readInt() : Integer.MAX_VALUE;
                
                //mege part
                while(ptr1<endBlock1 && ptr2<endBlock2){
                    if(a<b){
                        dosFile2.writeInt(a);
                        //System.out.println("writing out an " + a);
                        if(ptr1+1<endBlock1)
                            a=disFile1Pointer1.readInt();
                        ptr1++;
                    }
                    else {
                        dosFile2.writeInt(b);
                        //System.out.println("writing out an " + b);
                        if(ptr2+1<endBlock2)
                            b=disFile1Pointer2.readInt();
                        ptr2++;
                    }
                }
                while(ptr1<endBlock1){
                    dosFile2.writeInt(a);
                    //System.out.println("writingg out an " + a);
                    if(ptr1+1<endBlock1)
                        a=disFile1Pointer1.readInt();
                    ptr1++;
                }
                while(ptr2<endBlock2){
                    dosFile2.writeInt(b);
                    //System.out.println("writingg out an " + b);
                    if(ptr2+1<endBlock2)
                        b=disFile1Pointer2.readInt();
                    ptr2++;
                }
                //System.out.println("Jumping");
                startBlock1+=2*blockSize;
                startBlock2+=2*blockSize;
                ptr1=startBlock1;
                ptr2=startBlock2;
                disFile1Pointer1.skipBytes(INT_BYTES*blockSize);
                disFile1Pointer2.skipBytes(INT_BYTES*blockSize);
                endBlock1=Math.min(startBlock1+blockSize, numOfInts);
                endBlock2=Math.min(startBlock2+blockSize, numOfInts);
                //1 6 2 5 4 3 7
                //0 1 2 3 4 5 6
                //System.out.println("s1 = "+startBlock1);
                //System.out.println("s2 = "+startBlock2);
                //System.out.println("e1 = "+endBlock1);
                //System.out.println("e2 = "+endBlock2);
                //System.out.println("p1 = "+ptr1);
                //System.out.println("p2 = "+ptr2);
                dosFile2.flush();
            }
            disFile1Pointer1.close();
            disFile1Pointer2.close();
            dosFile2.close();
            rafFile1.close();
            rafFile2.close();
            rafFile1Helper.close();
            blockSize*=2;
            //readFile(f2);
            String temp;
            temp=f1;
            f1=f2;
            f2=temp;

            //System.out.println(disFile1Pointer1.readInt()+" and " +disFile1Pointer2.readInt());

            //debugCNT++;
            //if(debugCNT==3)break;
        }

        //System.out.println("ENDED IN " + f1);
        //if we ended up in file B copy it to File A
        if(f1 == fileB) 
            copyFromTo(f1,f2);
    }
    public static void prepare(String fil1, String fil2)  throws FileNotFoundException, IOException {
        RandomAccessFile raf1 = new RandomAccessFile(fil1,"r");
        RandomAccessFile raf2 = new RandomAccessFile(fil2,"rw");
        DataInputStream dis = new DataInputStream(
                new BufferedInputStream(new FileInputStream(raf1.getFD())));
        DataOutputStream dos = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(fil2,false)));
        ArrayList<Integer> lst = new ArrayList<Integer>();
        int cntElements=0;
        while(dis.available()>0){
            lst.clear();
            cntElements=0;
            while(cntElements<CNT_MAX&&dis.available()>0){
                lst.add(dis.readInt());
                cntElements++;
            }
            Collections.sort(lst);
            for(int i:lst){
                dos.writeInt(i);
            }
            dos.flush();
        }
    }
    public static void sort(String f1, String f2) throws FileNotFoundException, IOException {
        RandomAccessFile f = null;
        DataInputStream d = null;
        f = new RandomAccessFile(f1,"r");
        int min=Integer.MAX_VALUE, max=Integer.MIN_VALUE;
        d = new DataInputStream(
                new BufferedInputStream(new FileInputStream(f.getFD())));
        int k,cnt=0,prev=Integer.MIN_VALUE;
        boolean sorted=true;
        f.seek(0);
        //System.out.println(f.length());
        //System.out.println(f.readInt()+ " ibaah " +f.readInt());
        while (d.available() > 0) {
            k=d.readInt();
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
        if (sorted) {
            //System.err.println("SORTED");
            return;
        }

        //System.out.println();
        //System.out.println(cnt+"\n"+min+" "+max);
        if (cnt<=CNT_MAX) {
            ezSort(f1,f2,min,max);
            return;
        }
        if (RANGE_MIN<min && max<RANGE_MAX) {
            countSort(f1,f2,min,max);
            return;
        }
        //countSort(f1,f2,min,max);
        prepare(f1,f2);
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
