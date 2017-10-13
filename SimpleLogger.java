import processing.core.PApplet;

import java.io.File;

// Java Logging API
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

public class SimpleLogger {
    // logging
    private String logFileFullPath = "";
    private Logger logger = Logger.getLogger(PhotoBooth.class.getName());
    private FileHandler logFileHandler;

    public SimpleLogger(String alogFileFullPath) {
        try {
            logFileFullPath = alogFileFullPath;

            //OutputUtils.createFileIfNotExists(LOG_FILE_PATH);
            logFileHandler = new FileHandler(logFileFullPath, true);
            logger.addHandler(logFileHandler);
            logFileHandler.setFormatter(new SimpleFormatter());
        } catch (Exception ex) {
            PApplet.println("Fail to initiate log file at " + logFileFullPath);
        }
    }

    public void info(String str) {
        try {
            logger.info(str + PhotoBooth.LINE_BREAK);
        } catch (Exception ex) {
            PApplet.println("Fail to write to log file at " + logFileFullPath);
        }
    }

    public void warning(String str) {
        try {
            logger.warning(str + PhotoBooth.LINE_BREAK);
        } catch (Exception ex) {
            PApplet.println("Fail to write to log file at " + logFileFullPath);
        }
    }

    public void severe(String str) {
        try {
            logger.severe(str + PhotoBooth.LINE_BREAK);
        } catch (Exception ex) {
            PApplet.println("Fail to write to log file at " + logFileFullPath);
        }
    }
}