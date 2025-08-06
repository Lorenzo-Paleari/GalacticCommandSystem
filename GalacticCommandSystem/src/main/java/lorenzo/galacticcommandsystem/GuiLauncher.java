package lorenzo.galacticcommandsystem;

import lombok.RequiredArgsConstructor;
import lorenzo.galacticcommandsystem.controller.MainController;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


/**
 * GUI launcher component responsible for initializing the graphical user interface
 * after the Spring Boot application has fully started and is ready to serve requests.
 */
@Component
@RequiredArgsConstructor
public class GuiLauncher {

    /**
     * Main controller responsible for initializing and managing the GUI components.
     */
    private final MainController mainController;

    /**
     * Launches the graphical user interface after the Spring Boot application is fully ready.
     */
     @EventListener(ApplicationReadyEvent.class)
        public void launchGUI() {
            mainController.init();
        }
    }
