import io.sentry.Sentry;
import me.scrim.monitor.ScrimMonitors;
import me.scrim.monitor.task.impl.finishline.FinishlineTask;

/**
 * @author Brennan
 * @since 7/26/2021
 **/
public class Main {

    public static void main(String[] args) {
        ScrimMonitors.INSTANCE.start();
//        ScrimMonitors.INSTANCE.start();
    }

}
