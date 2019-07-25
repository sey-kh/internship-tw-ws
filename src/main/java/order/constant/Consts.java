package order.constant;

import java.util.logging.Logger;

public final class Consts {
    public static final String CANCEL = "cancelled";
    public static final String CONFIRMED = "confirmed";
    public static final String TIME_STAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String SALE = "sale";
    public static final String BUY = "buy";
    public static final String ANY = "any";
    public static final String ByTime = "bytime";
    public static final String ByOtherOrder = "byotherorder";
    public final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    public final static String Duplicated_Order = "Transaction failed due to duplicated order constraints";
}
