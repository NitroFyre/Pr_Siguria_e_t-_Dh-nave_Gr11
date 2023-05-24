import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;

public class RSAEncryption {
    public static void main(String[] args) {
        //This method tests the program
        BigInteger p = new BigInteger("67");
        BigInteger q = new BigInteger("73");
        BigInteger e = new BigInteger("89");
        int blockSize = 2;

        String filePath = "FileMessage.txt"; 

        String message = readFromFile(filePath);

        BigInteger n = p.multiply(q);
        BigInteger[] encryptedMessage = encrypt(message, n, e, blockSize);

        System.out.println("Encrypted Message:");
        for (BigInteger block : encryptedMessage) {
            System.out.println(block);
        }
    }

    public static String readFromFile(String filePath) {
        /*This method is used to read the contents of the file specified by the filePath
        parameter and return the result as a String*/
        StringBuilder messageBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                messageBuilder.append(line);
                messageBuilder.append(System.lineSeparator()); 
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return messageBuilder.toString();
    }

    public static BigInteger[] encrypt(String message, BigInteger n, BigInteger e, int blockSize) {
        //This method is used is used to encrypt a given message using the RSA encryption algorithm

        BigInteger[] encryptedBlocks = new BigInteger[(message.length() + blockSize - 1) / blockSize];
        int currentIndex = 0;
        for (int i = 0; i < encryptedBlocks.length; i++) {
            StringBuilder blockString = new StringBuilder();
            for (int j = 0; j < blockSize; j++) {
                if (currentIndex < message.length()) {
                    char ch = message.charAt(currentIndex);
                    blockString.append((int) ch);
                    currentIndex++;
                } else {
                    blockString.append(" ");
                }
            }

            String trimmedBlock = blockString.toString().trim();
            BigInteger block = new BigInteger(trimmedBlock);
            encryptedBlocks[i] = block.modPow(e, n);
        }

        return encryptedBlocks;
    }
}
