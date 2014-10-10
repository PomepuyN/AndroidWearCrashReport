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

    public static String getStackTrace(Throwable e) {
        if (null == e) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        if (e.getMessage() != null) {
            sb.append(e.getMessage()).append("\n");
        }

        if (e.getStackTrace() != null) {
            for (int i = 0; i < e.getStackTrace().length; i++) {
                sb.append("    ").append(e.getStackTrace()[i]).append("\n");
            }
        }

        if (e.getCause() != null) {
            sb.append("Caused by: ").append(e.getCause().getClass().getName()).append(":\n");
            sb.append(getStackTrace(e.getCause()));
        }

        return sb.toString();
    }
}
