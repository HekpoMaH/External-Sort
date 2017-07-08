//package uk.ac.cam.dgg30.fjava.tick0;

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
        System.out.println(f.length());
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
            System.err.println("SORTED");
            return;
        }

        System.out.println();
        System.out.println(cnt+"\n"+min+" "+max);
        if (cnt<=CNT_MAX) {
            ezSort(f1,f2,min,max);
            return;
        }
        if (RANGE_MIN<min && max<RANGE_MAX) {
            countSort(f1,f2,min,max);
            return;
        }
        //countSort(f1,f2,min,max);
        System.out.println("CANT SORT");
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
