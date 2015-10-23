/*
 * FileSplitterApp.java
 */

package filesplitter;

import java.io.IOException;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class FileSplitterApp extends SingleFrameApplication {

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        show(new FileSplitterView(this));
     
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of FileSplitterApp
     */
    public static FileSplitterApp getApplication() {
        return Application.getInstance(FileSplitterApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) throws IOException {
        launch(FileSplitterApp.class, args);
        
        
        
    }
    
}
