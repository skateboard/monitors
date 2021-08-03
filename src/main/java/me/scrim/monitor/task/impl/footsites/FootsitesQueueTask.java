package me.scrim.monitor.task.impl.footsites;

import io.sentry.Sentry;
import io.sentry.SentryLevel;
import me.scrim.monitor.ScrimMonitors;
import me.scrim.monitor.discord.utils.DiscordEmbeds;
import me.scrim.monitor.task.AbstractTask;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author Brennan
 * @since 7/29/21
 **/
public class FootsitesQueueTask extends AbstractTask {
    private final Footsites footSite;

    public FootsitesQueueTask(Footsites footSite) {
        super("https://" + footSite.getUrl());
        this.footSite = footSite;

        setStarted(true);
    }

    @Override
    public void run() {
        while (isStarted()) {
            boolean hasQueue = isQueueUp();

            hasQueue = true;
            while (!hasQueue) {
                hasQueue = isQueueUp();
                sleep(1000);
            }

            ScrimMonitors.INSTANCE.getRedis().sendFootsitesUpdate(footSite, true);

            boolean didQueueGoDown = false;

            while (!didQueueGoDown) {
                boolean queueStillUp = isQueueUp();

                queueStillUp = false;
                if(queueStillUp) {
                    sleep(1500);
                } else {
                    ScrimMonitors.INSTANCE.getRedis().sendFootsitesUpdate(footSite, false);

                    didQueueGoDown = true;
                }
            }

            //sleep(continuousNoUpdates > 7 ? 3000 : 500);
        }
    }

    private boolean isQueueUp() {
        try {
           final Request request = new Request.Builder()
                   .url(getWebsiteUrl())
                   .get()
                   .build();

           try(Response response = getClient().newCall(request).execute()) {
               return  response.code() == 529;
           }
        } catch (Exception e) {
            Sentry.captureMessage("Footsites Queue Task [" + getId() + "] Site: " + getWebsiteUrl() + ", " +
                    e.getMessage(), SentryLevel.ERROR);
        }

        return false;
    }
}
