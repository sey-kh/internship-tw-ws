package order.schedule;

import order.constant.Consts;
import order.domain.ComplexOrderDomain;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;


import java.util.Date;


@Configuration
@EnableScheduling
public class Scheduler {

    private static Date getDateNow() {
        return new java.util.Date();
    }

    private final static Logger LOGGER =
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    @Scheduled(cron = "0 */5 * ? * *")
    public void activateOrder() {

        DateFormat dateFormat = new SimpleDateFormat(Consts.TIME_STAMP_FORMAT);
        String strDate = dateFormat.format(getDateNow());
        LOGGER.log(Level.INFO, "====> ActivateOrder task running at" + strDate);
        ComplexOrderDomain.activateByTime(getDateNow());
    }
}
