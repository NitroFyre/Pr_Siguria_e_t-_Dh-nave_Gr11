import java.security.SecureRandom;
import java.util.*;
import java.io.*;
import java.io.File; 
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
public class SimpleAES_OFB2 {
    private byte[] iv;
    private String IV;
    public SimpleAES_OFB2(byte[] iv) {
       this.iv = iv;
        
    }
    public static String[] initialVector = new String[10000] ;
    public static void main(String[] args) throws IOException {
        
       byte[] iv = new byte[2]; // 16-bit IV
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        System.out.println(iv);
        String IV = new String(iv);
       encryptedIV(IV);
        List<byte[]> bytes1 = new ArrayList<>();
        try {
           
            File myObj = new File("File.txt");
            Scanner myReader = new Scanner(myObj);
            long count = (int)Files.lines(Paths.get("File.txt")).count();
            for(int i = 0; myReader.hasNextLine(); i++) {
                String word = myReader.nextLine();
                bytes1.add(word.getBytes());
            }
        } catch (Exception e) {
        }

        List<byte[]> encryptedBytes = new ArrayList<>();
        for(int i=0;i<bytes1.size();i++){
            byte[] bytes2 = bytes1.get(i);
            byte[] cipherText = xor(initialVector[i].getBytes(), bytes2);
            String cipherTexttoString = new String(cipherText);
            encryptedBytes.add(i, cipherText);
            System.out.println("Encrypted Result: " + cipherTexttoString);
        }
        for(int i=0;i<encryptedBytes.size();i++){
            byte[] bytes2 = encryptedBytes.get(i);
            byte[] plainText =  xor(initialVector[i].getBytes(),bytes2);
            String plainTexttoString =  new String(plainText);
            System.out.println("Decrypted Result: " + plainTexttoString);
        }
        
    }
    public static byte[] xor(byte[] a, byte[] b) {
        int length = Math.min(a.length, b.length);
        byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            result[i] = (byte) (a[i] ^ b[i]);
        }
        return result;
    }
    public static String[] encryptedIV(String a){
        int key = 4321;
        byte[] iv = new byte[32]; // 16-bit IV
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        System.out.println(iv);
        String IV = new String(iv);
        a = IV;
        System.out.println("Random IV is: " + IV);
        SimpleAES obj = new SimpleAES(key);
        String encrypted = obj.encrypt(IV);
        System.out.println("Encrypted IV: " + encrypted);
        initialVector[0] = encrypted;
        for(int i=1;i<initialVector.length;i++){
            initialVector[i] = obj.encrypt(initialVector[i-1]);
        }
        return initialVector;
    }
}



