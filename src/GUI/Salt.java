package GUI;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;


class Salt {

    private final String SALT_ALGORITHM = "SHA1PRNG";
    private final String HASH_ALGORITHM = "MD5";
    private final int SEED_LENGTH = 32;
    private File file;

    Salt(File file){
        this.file = file;
    }

    /**
     * Generates a salt to be used for password storage.
     * @return A salt String to be saved to file for use in password authentication.
     */
    private String generateSalt(){
        try {
            byte[] salt = SecureRandom.getInstance(SALT_ALGORITHM).generateSeed(SEED_LENGTH);
            return Base64.getEncoder().encodeToString(salt);
        } catch (NoSuchAlgorithmException nsa){
            //This should never happen.
        }
        return null;
    }

    /**
     * Hashes a salted password to save to file for use in password authentication.
     * @param saltedPassword A String from the concatenation of the salt String and password String
     * @return A hash String to be saved to file for use in password authentication.
     */
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
        return null;
    }

    /**
     * Takes in a username and password, and attempts to create a salt and hash
     * to store login information. Checks if the username already exists first.
     * Writes the username, salt, and hash to a text file.
     * @param username String containing the username to save.
     * @param password String containing the password to salt and hash.
     * @return true if the information is successfully saved to file. false if the username is
     * already contained in the file, or an exception occurs.
     */
    boolean storePassword(String username, String password){
        //Without this, one user may have multiple salts and hashes saved to file.
        //However, as-is, a password cannot be changed once it is set.
        if(usernameAlreadyInDatabase(username)){
            System.out.println("Username is already in database.");
            return false;
        }
        else {
            try {
                PrintWriter fileOut = new PrintWriter(new FileWriter(file, true));
                String salt = generateSalt();
                fileOut.print(username + " " + salt + " " + generateHash(salt + password) + "\n");
                fileOut.close();
                return true;
            } catch (IOException e) {
                System.out.println("Error writing hashed-password to file.");
                return false;
            }
        }
    }

    /**
     * Takes in a username and password, and tries to see if the hash of the entered
     * password matches the hash saved to file.
     * @param username a String containing the username to look for
     * @param password a String containing the password
     * @return true if the entered password hashes to the right value. false if it doesn't, if the
     * entered username does not exist in the file, or an exception was raised reading from the file.
     */
    boolean authenticatePassword(String username, String password){
        try {
            BufferedReader fileIn = new BufferedReader(new FileReader(this.file));
            String line;

            while(((line = fileIn.readLine()) != null) && (!line.split(" ")[0].equals(username))) {
                //Do nothing until you reach EOF or the line containing the username.
            }

            if(line == null) {
                System.out.println("Username not in database.");
                fileIn.close();
                return false;
            }

            //Username was found
            else{
                String[] fields = line.split(" ");
                String userGeneratedHash = generateHash(fields[1] + password);
                fileIn.close();
                return userGeneratedHash.equals(fields[2]);
            }
        } catch(IOException e){
            System.out.println("Error reading file.");
            return false;
        }
    }

    /**
     * Goes through a text file to look for a username.
     * @param username a String of the username to look for
     * @return true if the username is contained in the file.
     */
    private boolean usernameAlreadyInDatabase(String username){
        String line = null;
        try {
            BufferedReader fileIn = new BufferedReader(new FileReader(this.file));

            while (((line = fileIn.readLine()) != null) && (!line.split(" ")[0].equals(username))) {
                //Do nothing until you reach EOF or the line containing the username.
            }
        } catch (IOException e){
            System.out.println("Error reading file.");
        }
        //If line is still null, there was an exception, or EOF was reached and username was not found.
        return line != null;
    }
}
