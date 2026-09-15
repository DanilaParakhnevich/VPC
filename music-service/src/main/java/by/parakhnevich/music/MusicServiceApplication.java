package by.parakhnevich.music;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by agallochum on 2026-09-02
 */
@QuarkusMain
public class MusicServiceApplication {

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