/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pro.novatech.solutions.bmoffice.utilities.scripts;

import java.util.Date;
import org.mindrot.jbcrypt.BCrypt;
import pro.novatech.solutions.bmoffice.jpa.entities.User;
import pro.novatech.solutions.bmoffice.jpa.entities.controllers.UserController;

//574 560

/**
 *
 * 
 * @author Esther Mutombo
 */
public class CreateAdminUser {
    
    public static void main(String args[]){
        
        String username = "pos_admin";
        String password = "@_123#pass!";
        password = BCrypt.hashpw(password, BCrypt.gensalt(12));
        String role = "admin"; 
        String name = "Charlotte";
        String surname = "Kaputu";
        
        User user = new User();
        user.setUsername(username);
        user.setName(name);
        user.setPassword(password);
        user.setRole(role);
        user.setSurname(surname);
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        
        new UserController().create(user);
        
        
        /*String  originalPassword = "password";
        String generatedSecuredPasswordHash = BCrypt.hashpw(originalPassword, BCrypt.gensalt(12));
        System.out.println(generatedSecuredPasswordHash);
         
        boolean matched = BCrypt.checkpw(originalPassword, generatedSecuredPasswordHash);
        System.out.println(matched);
        */
        
    }
    
}
