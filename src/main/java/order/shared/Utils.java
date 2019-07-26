package order.shared;

import java.util.Date;
import java.util.UUID;

public class Utils {
    public static Date getDateNow(){
        return new java.util.Date();
    }
    public static String generateRandomString(){
        return UUID.randomUUID().toString();
    }
}
