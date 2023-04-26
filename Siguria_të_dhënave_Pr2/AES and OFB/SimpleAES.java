import java.io.File; 
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
public class SimpleAES {
    private int key;
    public SimpleAES(int key2){
        this.key = key;
    }

    public String encrypt(String string){
        return encryptDecryptString(string, this.key, false);
    }

    public String decrypt(String string){
        return encryptDecryptString(string, this.key, true);
    }

    private int[] boundValues = convertToHexArray(new String[]{"F000", "0F00", "00F0", "000F"});  
    private int[][] SBOX = convertToHexMatrix(new String[][]{
            {"6", "B", "0", "4"},
            {"7", "E", "2", "F"},
            {"9", "8", "A", "C"},
            {"3", "1", "5", "D"}
    });
    private int[][] invSBOX = convertToHexMatrix(new String[][]{
            {"2", "D", "6", "C"},
            {"3", "E", "0", "4"},
            {"9", "8", "A", "1"},
            {"B", "F", "5", "7"}
    });
    private int[][] MDS = convertToHexMatrix(new String[][]{
            {"1", "1"},
            {"1", "2"}
    });
    private int[][] invMDS = convertToHexMatrix(new String[][]{
            {"F", "E"},
            {"E", "E"}
    });
    private int[] RC = convertToHexArray(new String[]{ "1", "2", "4" });
    public String encryptDecryptString(String string, int key, boolean decrypt){
        if(!decrypt && string.length() % 2 != 0)
            string += " ";
        byte[] stringBytes = string.getBytes(StandardCharsets.ISO_8859_1);
        for(int i = 0; i < stringBytes.length; i += 2){
            int c = (Byte.toUnsignedInt(stringBytes[i]) << 8) | Byte.toUnsignedInt(stringBytes[i + 1]);
            int encrypt = decrypt? decryptBlock(c, key) : encryptBlock(c, key);
            stringBytes[i] = (byte)((encrypt & Integer.parseUnsignedInt("FF00", 16)) >> 8);
            stringBytes[i+1] = (byte)(encrypt & Integer.parseUnsignedInt("FF", 16));
        }
        return new String(stringBytes, StandardCharsets.ISO_8859_1);
    }

    public int encryptBlock(int block, int key){
        int[] keys = generateKeys(key);
        block ^= keys[0];
        for(int i = 1; i < 3; i++){
            block = SBOX(block, false);
            block = shiftRows(block, false);
            block = mixColumns(block, false);
            block = addRoundKey(block, keys[i]);
        }
        block = SBOX(block, false);
        block = shiftRows(block, false);
        block = addRoundKey(block, keys[3]);
        return block;
    }

    public int decryptBlock(int matrixArray, int key){
        int[] keys = generateKeys(key);
        matrixArray = addRoundKey(matrixArray, keys[3]);
        matrixArray = shiftRows(matrixArray, true);
        matrixArray = SBOX(matrixArray, true);
        for(int i = 2; i > 0; i--){
            matrixArray = addRoundKey(matrixArray, keys[i]);
            matrixArray = mixColumns(matrixArray, true);
            matrixArray = shiftRows(matrixArray, true);
            matrixArray = SBOX(matrixArray, true);
        }
        matrixArray ^= keys[0];
        return matrixArray;
    }

    private int[] generateKeys(int key) {
        int[] keys = new int[4];
        keys[0] = key;
        int previousKey = key;
        for(int i = 1; i < keys.length; i++){
            int b1 = ((previousKey & boundValues[0]) >> 12) ^ (g((previousKey & boundValues[3]), i - 1));
            int b2 = b1 ^ ((previousKey & boundValues[1]) >> 8);
            int b3 = b2 ^ ((previousKey & boundValues[2]) >> 4);
            int b4 = b3 ^ (previousKey & boundValues[3]);
            int upcomingKey = Integer.parseUnsignedInt("0000", 16);
            keys[i] = upcomingKey | (b1 << 12) | (b2 << 8) | (b3 << 4) | b4;
            previousKey = keys[i];
        }
        return keys;
    }

    private int SBOX(int values, boolean inverse){
        int[][] sbox = inverse? invSBOX : SBOX;
        int sboxed = sbox[(values & Integer.parseUnsignedInt("C000", 16)) >> 14][(values & Integer.parseUnsignedInt("3000", 16)) >> 12] << 12;
        sboxed = sboxed | (sbox[(values & Integer.parseUnsignedInt("0C00", 16)) >> 10][(values & Integer.parseUnsignedInt("0300", 16)) >> 8] << 8);
        sboxed = sboxed | (sbox[(values & Integer.parseUnsignedInt("00C0", 16)) >> 6][(values & Integer.parseUnsignedInt("0030", 16))  >> 4] << 4);
        sboxed = sboxed | sbox[(values & Integer.parseUnsignedInt("000C", 16)) >> 2][(values & Integer.parseUnsignedInt("0003", 16))];
        return sboxed;
    }

    private int shiftRows(int values, boolean inverse){
        return (values & (boundValues[0] | boundValues[2])) | ((values & boundValues[1]) >> 8) | ((values & boundValues[3]) << 8);
    }

    private int mixColumns(int values, boolean inverse){
        int[][] matrix = inverse? invMDS : MDS;
        int mixed = Integer.parseUnsignedInt("0000", 16);
        mixed = mixed | ((multiply(((values & boundValues[0]) >> 12), matrix[0][0]) ^ multiply(((values & boundValues[1]) >> 8), matrix[0][1])) << 12);
        mixed = mixed | ((multiply(((values & boundValues[0]) >> 12), matrix[1][0]) ^ multiply(((values & boundValues[1]) >> 8), matrix[1][1])) << 8);
        mixed = mixed | ((multiply(((values & boundValues[2]) >> 4), matrix[0][0]) ^ multiply(((values & boundValues[3])), matrix[0][1])) << 4);
        mixed = mixed | ((multiply(((values & boundValues[2]) >> 4), matrix[1][0]) ^ multiply(((values & boundValues[3])), matrix[1][1])));
        return mixed;
    }

    private int g(int values, int round){
        int newValues = ((values & Integer.parseUnsignedInt("0007", 16)) << 1) | (values >> 3);
        int s = SBOX[(newValues & Integer.parseUnsignedInt("000C", 16)) >> 2][newValues & Integer.parseUnsignedInt("0003", 16)];
        return s ^ RC[round];
    }

    private int addRoundKey(int block, int roundKey){
        return block ^ roundKey;
    }
   
    private int multiply(int x, int y){
        int modulus = Integer.parseUnsignedInt("13", 16);
        int sum = Integer.parseUnsignedInt("0000", 16);
        int modularBound = Integer.parseUnsignedInt("10", 16);
        while(y > 0){
            if((y & 1) == 1){
                sum = sum ^ x;
            }
            y = y >> 1;
            x = x << 1;
            if((x & modularBound) != 0){
                x = x ^ modulus;
            }
        }
        return sum;
    }


    private int[] convertToHexArray(String[] notHexArr){
        int[] hexArray = new int[notHexArr.length];
        for(int i = 0; i < notHexArr.length; i++){
            hexArray[i] = Integer.parseUnsignedInt(notHexArr[i], 16);
        }
        return hexArray;
    }

    private int[][] convertToHexMatrix(String[][] notHexMat){
        int[][] hexValues = new int[notHexMat.length][notHexMat[0].length];
        for(int i = 0; i < notHexMat.length; i++){
            hexValues[i] = convertToHexArray(notHexMat[i]);
        }
        return hexValues;
    }
    public static void main(String[] args) {
        int key = 1234;
        SimpleAES aes = new SimpleAES(key);
        try {
            File myObj = new File("File.txt");
            Scanner myReader = new Scanner(myObj);
            long count = Files.lines(Paths.get("File.txt")).count();
            
            ArrayList <String> word = new ArrayList<>(); 
            ArrayList <String> wordEncrypted = new ArrayList<>(); 
            ArrayList <String> wordDecrypted = new ArrayList<>(); 
            while(myReader.hasNextLine()) {
                String stringForEncryptionAndDecryption = myReader.nextLine();
                String encrypted = aes.encrypt(stringForEncryptionAndDecryption);
                //String dd = strToBinary(encrypted);
                //String ddd=boolean_to_integer_string(dd);
                String decrypted = aes.decrypt(encrypted);
                word.add(stringForEncryptionAndDecryption);
                wordEncrypted.add(encrypted);
                wordDecrypted.add(decrypted);
                //System.out.println("Teksti origjinal: " + stringForEncryptionAndDecryption);
                //System.out.println("Teksti i enkriptuar: " + encrypted);
                //System.out.println(dd);
                //System.out.println(ddd);
        
                //System.out.println("Pas dekriptimit te testit: " + decrypted);
            }
            System.out.println("The Text file has these contents:");
            for(String i: word) {
                System.out.println(i);
            }
            System.out.println("");
            System.out.println("The Text file after encryption:");
            for(String i: wordEncrypted) {
                System.out.println(i);
            }
            System.out.println("");
            System.out.println("The Text file after decryption:");
            for(String i: wordDecrypted) {
                System.out.println(i);
            }

        } catch (Exception e) {
            System.out.println("An error occurred.");
             e.printStackTrace();
        }
        
        
        
    }
}
