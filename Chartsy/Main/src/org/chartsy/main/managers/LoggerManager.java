package org.chartsy.main.managers;

import org.chartsy.main.utils.*;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import org.openide.util.Exceptions;

/**
 *
 * @author viorel.gheba
 */
public class LoggerManager {

    private static LoggerManager instance;
    private FileHandler fh;
    private Logger logger;

    public static LoggerManager getDefault() {
        if (instance == null) instance = new LoggerManager();
        return instance;
    }

    private LoggerManager() {
        try {
            fh = new FileHandler(FileUtils.LogFile(), true);
            logger = Logger.getLogger("ChartsyLogger");
            logger.addHandler(fh);
            logger.setLevel(Level.ALL);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (SecurityException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void log(String msg) { logger.log(Level.ALL, msg); }
    public void log(Level level, String msg) { logger.log(level, msg); }
    public void log(Exception ex) { logger.log(Level.WARNING, ex.getMessage(), ex); }
    public void log(String msg, Exception ex) { logger.log(Level.WARNING, msg, ex); }

}
