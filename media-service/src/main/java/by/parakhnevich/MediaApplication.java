package by.parakhnevich;

import io.quarkus.runtime.Quarkus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by agallochum on 2026-09-16
 */
public class MediaApplication {

    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) {
        try {
            Thread.sleep(10000L);
            Quarkus.run(args);
        } catch (Throwable e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
