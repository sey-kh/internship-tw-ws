package order.schedule;

import order.constant.Consts;
import order.service.ActivationService;
import order.shared.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Configuration
@EnableScheduling
public class Scheduler {

    private static ActivationService activationService;

    @Autowired
    public Scheduler(ActivationService activationService) {
        Scheduler.activationService = activationService;
    }

    @Scheduled(cron = "0 */10 * ? * *")
    public static void schedulingTask() {
        DateFormat dateFormat = new SimpleDateFormat(Consts.TIME_STAMP_FORMAT);
        String strDate = dateFormat.format(Utils.getDateNow());
        Consts.LOGGER.info("Schedule Task --> ActivateOrderByTime at " + strDate);
        activationService.activateByTime(Utils.getDateNow());
    }
}
