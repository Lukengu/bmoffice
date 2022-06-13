/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pro.novatech.solutions.bmoffice.utilities;

/**
 *
 * @author Esther Mutombo
 */
public class Validate {
    
    public static boolean isDouble(String s){
        try {
            Double.parseDouble(s);
            return true;
        } catch(NumberFormatException e){
        }
        return false;
    }
    
}
