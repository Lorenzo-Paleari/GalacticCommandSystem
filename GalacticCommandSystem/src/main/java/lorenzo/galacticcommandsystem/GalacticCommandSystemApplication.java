package lorenzo.galacticcommandsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Galactic Command System.
 */
@SpringBootApplication
public class GalacticCommandSystemApplication {
    
    static {
        System.setProperty("java.awt.headless", "false");
    }

    /**
    * Main method that serves as the entry point for the Spring Boot application.
    */
    public static void main(String[] args) {
        SpringApplication.run(GalacticCommandSystemApplication.class, args);
    }
}
