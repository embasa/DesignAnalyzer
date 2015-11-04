import com.sun.java.util.jar.pack.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;

/**
 * WHo cares
 * Created by bruno on 10/30/15.
 */
public class DesignAnalyzer
{
  //Set<> pack = null;
  public DesignAnalyzer()
  {

  }

  void loadPackage(String path)
  {
    System.out.println("Gathering class files in " + path);
    FilenameFilter classFilter = new FilenameFilter()
    {
      public boolean accept(File dir, String name)
      {
        return name.toLowerCase().endsWith(".class");
      }
    };

    File f = new File(path); // the directory, really!
    for (File file : f.listFiles(classFilter))
    {
      System.out.println(file.getName());
      //Class cl = file.
    }
  }

  public void secondLoader(String path)
  {
    File file = new File(path);
    System.out.println("BLAHBLAH BLAH");
    try
    {
      // Convert File to a URL
      URL url = file.toURI().toURL();          // file:/c:/myclasses/
      URL[] urls = new URL[]{url};

      // Create a new class loader with the directory
      ClassLoader cl = new URLClassLoader(urls);

      // Load in the class; MyClass.class should be located in
      // the directory file:/c:/myclasses/com/mycompany
      Class cls = cl.loadClass("demo.C");
      System.out.println("lalalal:" +  cls.getSuperclass().getName());
    } catch (MalformedURLException e)
    {
      e.printStackTrace();

    } catch (ClassNotFoundException e)
    {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws IOException
  {
    DesignAnalyzer fl = new DesignAnalyzer();
    if (args.length != 1)
    {
      System.out.println("Usage FileLoader <path>");
    } else
    {
      fl.loadPackage(args[0]);
      fl.secondLoader(args[0]);
    }
  }
}
