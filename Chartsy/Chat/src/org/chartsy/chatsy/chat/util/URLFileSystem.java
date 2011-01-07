package org.chartsy.chatsy.chat.util;

import org.chartsy.chatsy.chat.util.log.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;

public class URLFileSystem
{

    public static String getContents(URL url)
	{
        try
		{
            return getContents(url.openStream());
        }
        catch (IOException e)
		{
            return null;
        }
    }

    public static String getContents(InputStream is)
	{
        byte[] buffer = new byte[2048];
        int length;
        StringBuilder sb = new StringBuilder();
        try
		{
            while ((length = is.read(buffer)) != -1)
                sb.append(new String(buffer, 0, length));
            return sb.toString();
        }
        catch (IOException e)
		{
            return null;
        }
    }

    public static String getContents(File file)
	{
        try
		{
            return getContents(file.toURI().toURL());
        }
        catch (MalformedURLException e)
		{
            return "";
        }
    }

    public static void copy(URL src, File dst) throws IOException
	{
        InputStream in = null;
        OutputStream out = null;
        try
		{
            in = src.openStream();
            out = new FileOutputStream(dst);
            dst.mkdirs();
            copy(in, out);
        }
        finally
		{
            try
			{
                if (in != null) in.close();
            }
            catch (IOException e)
			{
            }
            try
			{
                if (out != null) out.close();
            }
            catch (IOException e)
			{
            }
        }
    }

    public static void copy(InputStream in, OutputStream out) throws IOException
	{
        final byte[] buffer = new byte[4096];
        while (true)
		{
            final int bytesRead = in.read(buffer);
            if (bytesRead < 0)
                break;
            out.write(buffer, 0, bytesRead);
        }

        out.flush();
    }

    public static String getSuffix(URL url)
	{
        final String path = url.getPath();
        int lastDot = path.lastIndexOf('.');
        return (lastDot >= 0) ? path.substring(lastDot) : "";
    }

    public static String getSuffix(File file)
	{
        final String path = file.getAbsolutePath();
        int lastDot = path.lastIndexOf('.');
        return (lastDot >= 0) ? path.substring(lastDot) : "";
    }

    public URL canonicalize(URL url)
	{
        return url;
    }

    public boolean canRead(URL url)
	{
        try
		{
            final URLConnection urlConnection = url.openConnection();
            return urlConnection.getDoInput();
        }
        catch (Exception e)
		{
            return false;
        }
    }

    public boolean canWrite(URL url)
	{
        try
		{
            final URLConnection urlConnection = url.openConnection();
            return urlConnection.getDoOutput();
        }
        catch (Exception e)
		{
            return false;
        }
    }

    public boolean canCreate(URL url)
	{
        return true;
    }

    public boolean isValid(URL url)
	{
        if (exists(url))
            return true;
        return canCreate(url);
    }

    public static boolean exists(URL url)
	{
        return url2File(url).exists();
    }

    public static boolean mkdirs(URL url)
	{
        final File file = url2File(url);
        if (!file.exists())
            return file.mkdirs();
        return true;
    }

    public static String getFileName(URL url)
	{
        if (url == null)
            return "";

        final String path = url.getPath();
        if (path.equals("/")) 
            return "/";
		
        final int lastSep = path.lastIndexOf('/');
        if (lastSep == path.length() - 1)
		{
            final int lastSep2 = path.lastIndexOf('/', lastSep - 1);
            return path.substring(lastSep2 + 1, lastSep);
        }
        else
		{
            return path.substring(lastSep + 1);
        }
    }

    public long getLength(URL url)
	{
        try
		{
            final URLConnection urlConnection = url.openConnection();
            return urlConnection.getContentLength();
        }
        catch (Exception e)
		{
            return -1;
        }
    }

    public static String getName(URL url)
	{
        final String fileName = getFileName(url);
        final int firstDot = fileName.lastIndexOf('.');
        return firstDot > 0 ? fileName.substring(0, firstDot) : fileName;
    }

    public String getPath(URL url)
	{
        return url.getPath();
    }

    public String getPathNoExt(URL url)
	{
        final String path = getPath(url);
        final int lastSlash = path.lastIndexOf("/");
        final int lastDot = path.lastIndexOf(".");
        if (lastDot <= lastSlash) 
            return path;
        return path.substring(0, lastDot);
    }

    public String getPlatformPathName(URL url)
	{
        return url != null ? url.toString() : "";
    }

    public static URL newFileURL(File file)
	{
        String filePath = file.getPath();
        if (filePath == null)
            return null;
        final String path = sanitizePath(filePath);
        return newURL("file", path);
    }

    public static URL newFileURL(String filePath)
	{
        if (filePath == null)
            return null;
        final String path = sanitizePath(filePath);
        return newURL("file", path);
    }

    private static String sanitizePath(String path)
	{
        if (File.separatorChar != '/')
            path = path.replace(File.separatorChar, '/');

        if (!path.startsWith("/"))
            path = "/" + path;
		
        return path;
    }

    public static URL newURL(String protocol, String path)
	{
        return newURL(protocol, null, null, -1, path, null, null);
    }

    public static URL newURL(String protocol, String userinfo,
		String host, int port,
		String path, String query, String ref)
	{
        try
		{
            final URL seed = new URL(protocol, "", -1, "");
            final String authority = port < 0 ? host : host + ":" + port;
            final Object[] args = new Object[]
			{
				protocol, host, port,
				authority, userinfo,
				path, query, ref,
			};

            urlSet.invoke(seed, args);
            return seed;
        }
        catch (Exception e)
		{
            Log.error(e);
            return null;
        }
    }

    private static final Method urlSet;

    static
	{
        final Class<String> str = String.class;
        try
		{
            urlSet = URL.class.getDeclaredMethod("set", str, str, int.class, str, str, str, str, str);
            urlSet.setAccessible(true);
        }
        catch (NoSuchMethodException e)
		{
            throw new IllegalStateException();
        }
    }

    public static File url2File(URL url)
	{
        final String path = url.getPath();
        return new File(path);
    }

    public static URL getParent(URL url)
	{
        final File file = url2File(url);
        final File parentFile = file.getParentFile();
        if (parentFile != null && !file.equals(parentFile))
		{
            try
			{
                return parentFile.toURI().toURL();
            }
            catch (Exception ex)
			{
                return null;
            }
        }
        return null;
    }

    public static void copyDir(File src, File dst) throws IOException
	{
        dst.mkdirs();
        File[] files = src.listFiles();
        for (File file : files)
		{
            if (file.isFile())
			{
                copyFile(file, new File(dst, file.getName()));
            } 
			else if (file.isDirectory())
			{
                copyDir(file, new File(dst, file.getName()));
            }
        }
    }

    private static void copyFile(File src, File dst) throws IOException
	{
        FileChannel in = new FileInputStream(src).getChannel();
        FileChannel out = new FileOutputStream(dst).getChannel();
        in.transferTo(0, in.size(), out);
        in.close();
        out.close();
    }

}
