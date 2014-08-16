package fr.nicolaspomepuy.androidwearcrashreport.mobile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Created by nicolas on 16/08/14.
 */
public class Utils {
    /**
     * Deserialize an object from a byte Array
     * @param bytes the byte Array to deserialize
     * @return the deserialized object
     */
    public static Object deserializeObject(byte[] bytes) {
        try {
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes));
            Object object = in.readObject();
            in.close();

            return object;
        } catch(ClassNotFoundException cnfe) {

            return null;
        } catch(IOException ioe) {

            return null;
        }
    }
}
