package org.chartsy.main.utils;

import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author viorel.gheba
 */
public class DesktopUtil {

    private static final String OS_MACOS = "Mac OS";
    private static final String OS_WINDOWS = "Windows";

    private static final String[] UNIX_BROWSE_CMDS = {"www-browser", "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape", "w3m", "lynx"};
    private static final String[] UNIX_OPEN_CMDS = {"run-mailcap", "pager", "less", "more"};
    private static final String[] BROWSERS = { "firefox", "opera", "konqueror", "epiphany", "seamonkey", "galeon", "kazehakase", "mozilla", "netscape" };

    private DesktopUtil() {}

    public static void browse(final String url) throws IOException, InterruptedException, Exception {
        final String osName = System.getProperty("os.name");
        if (osName.startsWith(OS_MACOS)) { browseMac(url); }
        else if (osName.startsWith(OS_WINDOWS)) { browseWindows(url); }
        else { browseUnix(url); }
    }

    public static void browse(final URL url) throws IOException {
        if (browseDesktop(url)) return;
        final String osName = System.getProperty("os.name");
        if (osName.startsWith(OS_MACOS)) { browseMac(url); }
        else if (osName.startsWith(OS_WINDOWS)) { browseWindows(url); }
        else { browseUnix(url); }
    }

    public static void browseAndWarn(final URL url, final Component parentComponent) {
        try { browse(url); }
        catch (final IOException e) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    JOptionPane.showMessageDialog(parentComponent, "Couldn't open a web browser:\n" + e.getLocalizedMessage(), "Unable to launch web browser", JOptionPane.ERROR_MESSAGE);
                }
            });
        }
    }

    public static void browseAndWarn(final String url, final Component parentComponent) {
        try { browse(new URL(url)); }
        catch (final IOException e) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    JOptionPane.showMessageDialog(parentComponent, "Couldn't open a web browser:\n" + e.getLocalizedMessage(), "Unable to launch web browser", JOptionPane.ERROR_MESSAGE);
                }
            });
        }
    }

    public static void open(final File file) throws IOException {
        if (openDesktop(file)) return;

        final String osName = System.getProperty("os.name");
        
        if (osName.startsWith(OS_MACOS)) { openMac(file); }
        else if (osName.startsWith(OS_WINDOWS)) { openWindows(file); }
        else { openUnix(file); }
    }

    public static void openAndWarn(final File file, final Component parentComponent) {
        try { open(file); }
        catch (final IOException e) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    JOptionPane.showMessageDialog(parentComponent, "Couldn't open " + file + ":\n" + e.getLocalizedMessage(), "Unable to open file", JOptionPane.ERROR_MESSAGE);
                }
            });
        }
    }

    private static boolean browseDesktop(final URL url) throws IOException {
        final Class desktopClass = getDesktopClass();
        if (desktopClass == null) return false;
        
        final Object desktopInstance = getDesktopInstance(desktopClass);
        if (desktopInstance == null) return false;

        try {
            @SuppressWarnings({"unchecked"})
            final Method browseMethod = desktopClass.getDeclaredMethod("browse", URI.class);
            browseMethod.invoke(desktopInstance, new URI(url.toExternalForm()));
            return true;
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof IOException) { throw (IOException) e.getCause(); }
            else { return false; }
        } catch (Exception e) {
            return false;
        }
    }

    private static void browseWindows(final String url) throws IOException {
        Runtime.getRuntime().exec(new String[]{"rundll32", "url.dll,FileProtocolHandler", url});
    }

    private static void browseWindows(final URL url) throws IOException {
        Runtime.getRuntime().exec(new String[]{"rundll32", "url.dll,FileProtocolHandler", url.toString()});
    }

    private static void browseUnix(final String url) throws IOException, InterruptedException, Exception {
        boolean found = false;
        for (String browser : BROWSERS) {
            if (!found) {
                found = Runtime.getRuntime().exec(new String[] {"which", browser}).waitFor() == 0;
                if (found) Runtime.getRuntime().exec(new String[] {browser, url});
            }
        }
        if (!found) throw new Exception(Arrays.toString(BROWSERS));
    }

    private static void browseUnix(final URL url) throws IOException {
        for (final String cmd : UNIX_BROWSE_CMDS) {
            if (unixCommandExists(cmd)) {
                Runtime.getRuntime().exec(new String[]{cmd, url.toString()});
                return;
            }
        }
        throw new IOException("Could not find a suitable web browser");
    }

    private static void browseMac(final String url) throws IOException {
        try {
            final Class fileMgr = getAppleFileManagerClass();
            @SuppressWarnings({"unchecked"})
            final Method openURL = fileMgr.getDeclaredMethod("openURL", String.class);
            openURL.invoke(null, url);
        } catch (Exception e) {
            throw new IOException("Could not launch Mac URL: " + e.getLocalizedMessage());
        }
    }

    private static void browseMac(final URL url) throws IOException {
        try {
            final Class fileMgr = getAppleFileManagerClass();
            @SuppressWarnings({"unchecked"})
            final Method openURL = fileMgr.getDeclaredMethod("openURL", String.class);
            openURL.invoke(null, url.toString());
        } catch (Exception e) {
            throw new IOException("Could not launch Mac URL: " + e.getLocalizedMessage());
        }
    }


    private static boolean openDesktop(final File file) throws IOException {
        final Class desktopClass = getDesktopClass();
        if (desktopClass == null) return false;
        
        final Object desktopInstance = getDesktopInstance(desktopClass);
        if (desktopInstance == null) return false;

        try {
            @SuppressWarnings({"unchecked"})
            final Method browseMethod = desktopClass.getDeclaredMethod("open", File.class);
            browseMethod.invoke(desktopInstance, file);
            return true;
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof IOException) { throw (IOException) e.getCause(); }
            else if (e.getCause() instanceof IllegalArgumentException) { throw new FileNotFoundException(e.getCause().getLocalizedMessage()); }
            else { return false; }
        } catch (Exception e) { return false; }
    }

    private static void openWindows(final File file) throws IOException {
        Runtime.getRuntime().exec(new String[]{"rundll32", "shell32.dll,ShellExec_RunDLL", file.getAbsolutePath()});
    }

    private @SuppressWarnings({"deprecation"}) static void openMac(final File file) throws IOException
    {
        browseMac(file.getAbsoluteFile().toURL());
    }


    private static void openUnix(final File file) throws IOException {
        for (final String cmd : UNIX_OPEN_CMDS) {
            if (unixCommandExists(cmd)) {
                Runtime.getRuntime().exec(new String[]{cmd, file.getAbsolutePath()});
                return;
            }
        }
        throw new IOException("Could not find a suitable viewer");
    }

    private static Class getDesktopClass() {
        final String desktopClassName = "java.awt.Desktop";
        try { return Class.forName(desktopClassName); }
        catch (ClassNotFoundException e) { return null; }
  }

    private static Object getDesktopInstance(final Class desktopClass) {
        try {
            @SuppressWarnings({"unchecked"})
            final Method isDesktopSupportedMethod = desktopClass.getDeclaredMethod("isDesktopSupported");
            final boolean isDesktopSupported = (Boolean) isDesktopSupportedMethod.invoke(null);
            
            if (!isDesktopSupported) { return null; }

            @SuppressWarnings({"unchecked"})
            final Method getDesktopMethod = desktopClass.getDeclaredMethod("getDesktop");
            return getDesktopMethod.invoke(null);
        } catch (Exception e) {
            return null;
        }
    }

    private static Class getAppleFileManagerClass() throws ClassNotFoundException {
        final String appleClass = "com.apple.eio.FileManager";
        return Class.forName(appleClass);
    }

    private static boolean unixCommandExists(final String cmd) throws IOException {
        final Process whichProcess = Runtime.getRuntime().exec(new String[]{"which", cmd});

        boolean finished = false;
        do {
            try {
                whichProcess.waitFor();
                finished = true;
            } catch (InterruptedException e) {}
        } while (!finished);
        
        return whichProcess.exitValue() == 0;
    }

}
