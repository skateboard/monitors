import io.sentry.Sentry;
import me.scrim.monitor.ScrimMonitors;
import me.scrim.monitor.task.impl.finishline.FinishlineTask;

/**
 * @author Brennan
 * @since 7/26/2021
 **/
public class Main {

    public static void main(String[] args) {
        Sentry.init(options -> {
            options.setDsn("https://af01435b271c424b846a93e91b42bd61@o663814.ingest.sentry.io/5880931");
            // Set traces_sample_rate to 1.0 to capture 100% of transactions for performance monitoring.
            // We recommend adjusting this value in production.
            options.setTracesSampleRate(1.0);
            // When first trying Sentry it's good to see what the SDK is doing:
            options.setDebug(true);
        });

        final FinishlineTask task = new FinishlineTask("prod2798222", "White/Gym Red-Black");
        task.run();
//        ScrimMonitors.INSTANCE.start();
    }

}
