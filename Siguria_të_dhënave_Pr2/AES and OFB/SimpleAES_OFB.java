import java.security.SecureRandom;
import java.util.*;
import java.io.*;
import java.io.File; 
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
public class SimpleAES_OFB {
    private byte[] iv;
    private String IV;
    public SimpleAES_OFB(byte[] iv) {
       this.iv = iv;
        
    }
    public static void main(String[] args) throws IOException {
        int key = 4321;
        byte[] iv = new byte[2]; // 16-bit IV
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        System.out.println(iv);
        String IV = new String(iv);
        System.out.println("Random IV is: " + IV);
        SimpleAES object = new SimpleAES(key);
        String encrypted = object.encrypt(IV);
        System.out.println("Encrypted IV: " + encrypted);
        

        File file1 = new File("Text.txt"); // Replace with the path to your file
        FileInputStream fis1 = new FileInputStream(file1);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fis1.read(buffer)) != -1) {
            bos.write(buffer, 0, bytesRead);
        }
        fis1.close();
        bos.close();
        byte[] bytes = encrypted.getBytes();
        byte[] bytes1 = bos.toByteArray();
        System.out.println("Bytes read from file: " + bytes1.length);


        byte[] result = xor(bytes, bytes1);
        String resultString = new String(result);
        System.out.println("Result: " + resultString);
    }
    public static byte[] xor(byte[] a, byte[] b) {
        int length = Math.min(a.length, b.length);
        byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            result[i] = (byte) (a[i] ^ b[i]);
        }
        return result;
    }
}

