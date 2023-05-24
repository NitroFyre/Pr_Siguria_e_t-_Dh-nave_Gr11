import java.math.BigInteger;

public class RSAEncryptionDecryption {
    public static void main(String[] args) {
        //This method tests the program
        
        BigInteger n = new BigInteger("253");
        BigInteger e = new BigInteger("31");
        BigInteger m = new BigInteger("10");
        
        BigInteger encryptedMessage = encrypt(m, n, e);
        System.out.println("Encrypted Message m=10 is: " + encryptedMessage);
        
        BigInteger p = findPrimeFactor(n);
        System.out.println("The value of p is: " + p);
        BigInteger q = n.divide(p);
        System.out.println("The value of q is: " + q);
        BigInteger d = calculatePrivateKey(e, p, q);
        System.out.println("The value of the private Key d is: " + d);

        
        BigInteger c = new BigInteger("35");
        BigInteger decryptedMessage = decrypt(c, n, d);
        System.out.println("Decrypted CipherMessage c=35 is: " + decryptedMessage);
    }
    
    public static BigInteger encrypt(BigInteger m, BigInteger n, BigInteger e) {
        //This method is used to encrypt the message
        return m.modPow(e, n);
    }
    
    public static BigInteger findPrimeFactor(BigInteger n) {
        //This method is used to find one of the prime factors (p or q), which when multiplied give us n 
        BigInteger i = BigInteger.valueOf(2);
        while (!n.mod(i).equals(BigInteger.ZERO)) {
            i = i.add(BigInteger.ONE);
        }
        return i;
    }
    
    public static BigInteger calculatePrivateKey(BigInteger e, BigInteger p, BigInteger q) {
        //This method is used to calculate the private key d which is used for decryption
        //fiN=(p-1)*(q-1), d is the modular inverse of e with respect to fiN such that (e*d)modfiN=1
        BigInteger fiN = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        return e.modInverse(fiN);
    }
    
    public static BigInteger decrypt(BigInteger c, BigInteger n, BigInteger d) {
        //This method decrypts the ciphertext
        return c.modPow(d, n);
    }
}
