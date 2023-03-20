import java.util.ArrayList;  
import java.util.Scanner;   
public class HillCipherExample1 {
  
        //Metoda për të pranuar Matricën Çelës 
        private static int[][] getKeyMatrix() {  
            Scanner sc = new Scanner(System.in);  
            System.out.println("Enter key matrix:");  
            String key = sc.nextLine();  
            //int len = key.length();  
            double sq = Math.sqrt(key.length());  
            if (sq != (long) sq) {  
                System.out.println("Cannot Form a square matrix");  
            }  
            int len = (int) sq;  
            int[][] keyMatrix = new int[len][len];  
            int k = 0;  
            for (int i = 0; i < len; i++)  
            {  
                for (int j = 0; j < len; j++)  
                {  
                    keyMatrix[i][j] = ((int) key.charAt(k)) - 97;  
                    k++;  
                } 
            }  
            return keyMatrix;  
        }  
        // Metoda e mëposhtme kontrollon nëse Matrica Çelës është valide, pra nëse det=0
        private static void isValidMatrix(int[][] keyMatrix) {  
            int det = keyMatrix[0][0] * keyMatrix[1][1] - keyMatrix[0][1] * keyMatrix[1][0];  
            // Nëse det=0, throw exception and terminate  
            if(det == 0) {  
                throw new java.lang.Error("Det equals to zero, invalid key matrix!");  
            }  
        }  
        // Kjo metodë kontrollon nëse inversi i matricës është valid (matrix mod26 = (1,0,0,1)  
            private static void isValidReverseMatrix(int[][] keyMatrix, int[][] reverseMatrix) {  
            int[][] product = new int[2][2];  
            // Njehësojmë prodhimin e Matricës Çelës dhe inversit të kësaj matrice   
            product[0][0] = (keyMatrix[0][0]*reverseMatrix[0][0] + keyMatrix[0][1] * reverseMatrix[1][0]) % 26;  
            product[0][1] = (keyMatrix[0][0]*reverseMatrix[0][1] + keyMatrix[0][1] * reverseMatrix[1][1]) % 26;  
            product[1][0] = (keyMatrix[1][0]*reverseMatrix[0][0] + keyMatrix[1][1] * reverseMatrix[1][0]) % 26;  
            product[1][1] = (keyMatrix[1][0]*reverseMatrix[0][1] + keyMatrix[1][1] * reverseMatrix[1][1]) % 26;  
            // Kontrollo nëse a=1 dhe b=0 dhe c=0 dhe d=1  
            // Nëse jo, atëherë throw exception and terminate  
            if(product[0][0] != 1 || product[0][1] != 0 || product[1][0] != 0 || product[1][1] != 1) {  
                throw new java.lang.Error("Invalid reverse matrix found!");  
            }  
        }  
        // Kjo metodë llogarit inversin e Matricës Çelës
        private static int[][] reverseMatrix(int[][] keyMatrix) {  
            int detmod26 = (keyMatrix[0][0] * keyMatrix[1][1] - keyMatrix[0][1] * keyMatrix[1][0]) % 26; // Calc det  
            int factor;  
            int[][] reverseMatrix = new int[2][2];  
            // Gjeje vlerën "factor" për të cilën vlenë  
            // factor*det = 1 mod 26  
            for(factor=1; factor < 26; factor++)  
            {  
                if((detmod26 * factor) % 26 == 1)  
                {  
                    break;  
                }  
            }  
            // Llogarisim elementet e inversit të Matricës  Çelës duke përdorur vlerën "factor" të gjetur 
            reverseMatrix[0][0] = keyMatrix[1][1]           * factor % 26;  
            reverseMatrix[0][1] = (26 - keyMatrix[0][1])    * factor % 26;  
            reverseMatrix[1][0] = (26 - keyMatrix[1][0])    * factor % 26;  
            reverseMatrix[1][1] = keyMatrix[0][0]           * factor % 26;  
            return reverseMatrix;  
        }  
        // Kjo metodë repezenton rezultatin e enkriptimit/dekriptimit
        private static void echoResult(String label, int adder, ArrayList<Integer> phrase) {  
            int i;  
            System.out.print(label);  
            // Loop për secilin çift të karakterëve  
            for(i=0; i < phrase.size(); i += 2) {  
                System.out.print(Character.toChars(phrase.get(i) + (64 + adder)));  
                System.out.print(Character.toChars(phrase.get(i+1) + (64 + adder)));  
                if(i+2 <phrase.size()) {  
                    System.out.print("-");  
                }  
            }  
            System.out.println();  
        }  
        // Metoda në vijim bën enkriptimin   
        public static void encrypt(String phrase, boolean alphaZero)  
        {  
            int i;  
            int adder = alphaZero ? 1 : 0; // Për kalkulime duke u varur në alfabet  
            int[][] keyMatrix;  
            ArrayList<Integer> phraseToNum = new ArrayList<>();  
            ArrayList<Integer> phraseEncoded = new ArrayList<>();  
            //Shlyejmë të gjitha shkronjat që nuk bëjnë pjesë në gjuhën angleze dhe e shndërrojmë frazën në shkronja të mëdhaja   
            phrase = phrase.replaceAll("[^a-zA-Z]","").toUpperCase();  
      
            // Në qoftë se gjatësia e frazës nuk është numër i plotë, atëherë në fund të saj shtojmë shkronjën "Q" për ta bërë gjatësinë e frazës numër çift  
            if(phrase.length() % 2 == 1) {  
                phrase += "w";  
            }  
            // Marrim matricën çelës të rendit 2x2 
            keyMatrix = getKeyMatrix();  
            // Kontrollojmë nëse matrica është valide (det != 0)  
            isValidMatrix(keyMatrix);  
            // Konvertojmë karakterët apo shkronjat në numra duke u bazuar në pozitën  
            // e tyre në Tabelën ASCII minus 64 pozita (A=65 in ASCII table)  
            // Nëse e përdorim A=0 alphabet, ia zbersim një më shumë (adder)  
            for(i=0; i < phrase.length(); i++) {  
                phraseToNum.add(phrase.charAt(i) - (64 + adder));  
            }  
            // Njehsojmë prodhimin e çdo çifti të frazës me matricën çelës dhe në fund rezultatin e pjesëtojmë me mod26
            // Nëse e përdorim A=1 alphabet dhe rezultati është 0, atëherë e zëvendësojmë atë me 26 (Z)  
            for(i=0; i < phraseToNum.size(); i += 2) {  
                int x = (keyMatrix[0][0] * phraseToNum.get(i) + keyMatrix[0][1] * phraseToNum.get(i+1)) % 26;  
                int y = (keyMatrix[1][0] * phraseToNum.get(i) + keyMatrix[1][1] * phraseToNum.get(i+1)) % 26;  
                phraseEncoded.add(alphaZero ? x : (x == 0 ? 26 : x ));  
                phraseEncoded.add(alphaZero ? y : (y == 0 ? 26 : y ));  
            }  
            // Shtypim rezultatin  
            echoResult("Encoded phrase: ", adder, phraseEncoded);  
        }  
        // Metoda në vijim bën dekriptimin 
        public static void decrypt(String phrase, boolean alphaZero)  
        {  
            int i, adder = alphaZero ? 1 : 0;  
            int[][] keyMatrix, revKeyMatrix;  
            ArrayList<Integer> phraseToNum = new ArrayList<>();  
            ArrayList<Integer> phraseDecoded = new ArrayList<>();  
           //Shlyejmë të gjitha shkronjat që nuk bëjnë pjesë në gjuhën angleze dhe e shndërrojmë frazën në shkronja të mëdhaja 
            phrase = phrase.replaceAll("[^a-zA-Z]","").toUpperCase();  
      
            // Marrim matricën çelës të rendit 2x2 
            keyMatrix = getKeyMatrix();  
            // Kontrollojmë nëse matrica është valide (det != 0)  
            isValidMatrix(keyMatrix);  
            // Konvertojmë karakterët apo shkronjat në numra duke u bazuar në pozitën  
            // e tyre në Tabelën ASCII minus 64 pozita (A=65 in ASCII table)  
            // Nëse e përdorim A=0 alphabet, ia zbersim një më shumë (adder)  
            for(i=0; i < phrase.length(); i++) {  
                phraseToNum.add(phrase.charAt(i) - (64 + adder));  
            }  
            // Gjejmë inversin e Matricës Çelës  
            revKeyMatrix = reverseMatrix(keyMatrix);  
            // Kontrollojmë nëse matrica inverse e llogaritur vlenë (product = 1,0,0,1)  
            isValidReverseMatrix(keyMatrix, revKeyMatrix);  
            // Njehsojmë prodhimin e çdo çifti të frazës me inversin e llogaritut të matricës çelës dhe në fund rezultatin e pjesëtojmë me mod26
            for(i=0; i < phraseToNum.size(); i += 2) {  
                phraseDecoded.add((revKeyMatrix[0][0] * phraseToNum.get(i) + revKeyMatrix[0][1] * phraseToNum.get(i+1)) % 26);  
                phraseDecoded.add((revKeyMatrix[1][0] * phraseToNum.get(i) + revKeyMatrix[1][1] * phraseToNum.get(i+1)) % 26);  
            }  
            // Shtypim rezultatin  
            echoResult("Decoded phrase: ", adder, phraseDecoded);  
        }  
        //Metoda main për testim 
        public static void main(String[] args) {  
            String opt, phrase;  
            byte[] p;  
            Scanner sc = new Scanner(System.in);  
            System.out.println("Hill Cipher Implementation (2x2)");  
            System.out.println("-------------------------");  
            System.out.println("1. Encrypt text (A=0,B=1,...Z=25)");  
            System.out.println("2. Decrypt text (A=0,B=1,...Z=25)");  
            System.out.println("3. Encrypt text (A=1,B=2,...Z=26)");  
            System.out.println("4. Decrypt text (A=1,B=2,...Z=26)");  
            System.out.println();  
            System.out.println("Type any other character to exit");  
            System.out.println();  
            System.out.print("Select your choice: ");  
            opt = sc.nextLine();  
            switch (opt)  
            {  
                case "1":  
                    System.out.print("Enter phrase to encrypt: ");  
                    phrase = sc.nextLine();  
                    encrypt(phrase, true);  
                    break;  
                case "2":  
                    System.out.print("Enter phrase to decrypt: ");  
                    phrase = sc.nextLine();  
                    decrypt(phrase, true);  
                    break;  
                case "3":  
                    System.out.print("Enter phrase to encrypt: ");  
                    phrase = sc.nextLine();  
                    encrypt(phrase, false);  
                    break;  
                case "4":  
                    System.out.print("Enter phrase to decrypt: ");  
                    phrase = sc.nextLine();  
                    decrypt(phrase, false);  
                    break;  
            }  
        }  
}
