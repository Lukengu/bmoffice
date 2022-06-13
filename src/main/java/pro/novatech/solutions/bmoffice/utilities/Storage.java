/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pro.novatech.solutions.bmoffice.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author Esther Mutombo
 */
public class Storage {

    static File file = new File("_x976yt__");

    public static void write(Object obj) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }

        try ( FileOutputStream fos = new FileOutputStream(file);  ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(obj);
            oos.flush();
        }
    }

    public static Object readObject() throws IOException, ClassNotFoundException {
        Object result = null;
        try ( FileInputStream fis = new FileInputStream(file);  ObjectInputStream ois = new ObjectInputStream(fis)) {
            result = ois.readObject();
        }
        return result;
    }

}
