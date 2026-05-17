package by.parakhnevich.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableCaching
public class UserServiceApplication {
    public static void main(String[] args) throws InterruptedException {
        SpringApplication app = new SpringApplication(UserServiceApplication.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);

        // Блокируем main поток
        Thread.currentThread().join();
    }
}
