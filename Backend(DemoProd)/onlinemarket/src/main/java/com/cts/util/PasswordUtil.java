package com.cts.util;
 

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;
 
@Component
public class PasswordUtil {
    public static String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }
 
    public boolean checkPassword(String plainTextPassword, String hashedPassword) {
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }
}
 
 