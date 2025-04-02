package com.textapp.db;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {
    public static void main(String[] args) {
        // List of plain-text passwords to hash
        String[] passwords = {"chefpass", "teacherpass", "classreppass"}; // Add all your plain-text passwords here

        // Hash each password and print the result
        for (String password : passwords) {
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            System.out.println("Plain text: " + password + " -> Hashed: " + hashedPassword);
        }
    }
}
