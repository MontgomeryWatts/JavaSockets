package gui.server;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Handles storage, creation, and authentication of user accounts. Takes in the username and password given by the user,
 * then salts and hashes it so it can be safely stored without directly being saved as plaintext.
 */

class Salt {

    private final String SALT_ALGORITHM = "SHA1PRNG";
    private final String HASH_ALGORITHM = "SHA-256";
    private final int SEED_LENGTH = 32;
    private File file;

    /**
     * Creates a Salt object which refers to a file.
     * @param file The file where user info will be stored.
     */

    Salt(File file){
        this.file = file;
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
        try (BufferedReader fileIn = new BufferedReader(new FileReader(file))){
            String line;
            while(((line = fileIn.readLine()) != null) && (!line.split(" ")[0].equals(username))) {
                //Do nothing until you reach EOF or the line containing the username.
            }

            if(line == null) {
                System.out.println("Username: " + username + " not in database.");
                return false;
            }

            //Username was found
            else{
                String[] fields = line.split(" ");
                String userGeneratedHash = generateHash(fields[1] + password);
                return userGeneratedHash.equals(fields[2]);
            }
        } catch(IOException e){
            System.out.println("Error authenticating password for " + username);
        }
        return false;
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
            //This should never happen.
        }
        return null;
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

    boolean registerNewUser(String username, String password){
        //Without this, one user may have multiple salts and hashes saved to file.
        //However, as-is, a password cannot be changed once it is set.
        if(usernameAlreadyInDatabase(username)){
            System.out.println("Username is already in database.");
            return false;
        }
        else {
            try(PrintWriter fileOut = new PrintWriter(new FileWriter(file, true))){
                String salt = generateSalt();
                System.out.println("Created new user: " + username);
                fileOut.println(username + " " + salt + " " + generateHash(salt + password));
                return true;
            } catch (IOException e){
                System.out.println("Error creating new entry for user: " + username);
            }
        }
        return false;
    }

    /**
     * Goes through a text file to look for a username.
     * @param username a String of the username to look for
     * @return true if the username is contained in the file.
     */

    private boolean usernameAlreadyInDatabase(String username){
        String line;
        String lower = username.toLowerCase();
        boolean found = false;
        try (BufferedReader fileIn = new BufferedReader(new FileReader(file))){
            do{
                line = fileIn.readLine();
                if(line != null) {
                    String[] fields = line.split(" ");
                    if (fields[0].toLowerCase().equals(lower))
                        found = true;
                }
            } while((!found) && (line != null));
        } catch (IOException e){
            System.out.println("Error reading file.");
        }
        return found;
    }
}
