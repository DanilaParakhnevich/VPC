package by.parakhnevich.music;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

/**
 * Created by agallochum on 2026-09-02
 */
@QuarkusMain
public class MusicServiceApplication {
    public static void main(String[] args) {
        Quarkus.run(args);
    }
}