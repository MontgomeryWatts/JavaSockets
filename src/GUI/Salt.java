package GUI;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;


public class Salt {

    private final String SALT_ALGORITHM = "SHA1PRNG";
    private final String HASH_ALGORITHM = "MD5";
    private final int SALT_LENGTH = 32;
    private File file;

    public Salt(File file){
        this.file = file;
    }

    private String generateSalt(){
        try {
            byte[] salt = SecureRandom.getInstance(SALT_ALGORITHM).generateSeed(SALT_LENGTH);
            return Base64.getEncoder().encodeToString(salt);
        } catch (NoSuchAlgorithmException nsa){
            //This should never happen.
        }
        return null;
    }

    private String generateHash(String saltedPassword){
        try {
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            md.reset();
            md.update(saltedPassword.getBytes());
            byte[] hash = md.digest();
            return Base64.getEncoder().encodeToString(hash);
        }catch (NoSuchAlgorithmException nsa){
            //This should also never happen.
        }
        return "";
    }

    void storePassword(String username, String password){
        try {
            PrintWriter fileOut = new PrintWriter(new FileWriter(file, true));
            String salt = generateSalt();
            fileOut.print(username + " " + salt + " " + generateHash(salt + password) + "\n");
            fileOut.close();
        } catch (IOException e){
            System.out.println("Error writing hashed-password to file.");
        }
    }

    boolean authenticatePassword(String username, String password){
        try {
            BufferedReader fileIn = new BufferedReader(new FileReader(this.file));
            String line;

            while(((line = fileIn.readLine()) != null) && (!line.split(" ")[0].equals(username))) {
                //Do nothing until you reach EOF or the line containing the username.
            }

            if(line == null)
                System.out.println("Username not in database.");

            //Username was found
            else{
                String[] fields = line.split(" ");
                String userGeneratedHash = generateHash(fields[1] + password);
                return userGeneratedHash.equals(fields[2]);
            }
            fileIn.close();
        } catch(IOException e){
            System.out.println("Error reading file.");
        }
        return false;
    }
}
