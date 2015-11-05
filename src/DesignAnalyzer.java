import com.sun.java.util.jar.pack.*;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * WHo cares * Created by bruno on 10/30/15.
 */
public class DesignAnalyzer
{

  private List<Class> classNames = null;
  private double packageMethodCount = 0;
  public DesignAnalyzer()
  {

    classNames = new ArrayList<>();
  }

  List<String> getClassNames(String path)
  {
    FilenameFilter classFilter = new FilenameFilter()
    {
      public boolean accept(File dir, String name)
      {
        return name.toLowerCase().endsWith(".class");
      }
    };

    List<String> names = new ArrayList<>();

    for (File file : (new File(path)).listFiles(classFilter))
    {
      if(file != null){
        names.add(file.getName().replaceAll(".class", ""));
      }
    }
    return names;
  }



  public void loadPackage(String path)
  {
    String[] split = path.split("/");
    for(String s: split){
      System.out.println(s);
    }
    String dir = split[0];
    List<String> names = getClassNames(path);
    if(names == null){
      return;
    }
    String packageName = split[1];
    File file = new File(dir);
    System.out.println(dir + " " + packageName);
    System.out.println(names.size());
    try
    {
      // Convert File to a URL

      URL url = file.toURI().toURL();          // file:/c:/myclasses/
      System.out.println("after url " + file);
      URL[] urls = new URL[]{url};

      // Create a new class loader with the directory
      ClassLoader cl = new URLClassLoader(urls);

      // Load in the class; MyClass.class should be located in
      // the directory file:/c:/myclasses/com/mycompany

      for(String name: names){
        //System.out.println(packageName +"."+ name.replaceAll(".class","") + "->" + cl);
        classNames.add(cl.loadClass(packageName +"."+ name));
        packageMethodCount += classNames.get(classNames.size()-1).getDeclaredMethods().length;
      }
      //Class cls = cl.loadClass("demo"+"."+"C");
      System.out.println("class size: " + classNames.size());
      System.out.println("method count: " + packageMethodCount);
    } catch (MalformedURLException e)
    {
      e.printStackTrace();

    } catch (ClassNotFoundException e)
    {
      e.printStackTrace();
    } catch (NullPointerException e){
      System.out.println("^ null pointer");
    }
  }

  private void printClasses(){
    for(int i=0;i<classNames.size();i++){
      System.out.println(i + " " + classNames.get(i).getName());
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
      fl.classWorkLoad();
      //fl.secondLoader(args[0]);
    }
  }

  public int classWorkLoad(){
    int i = 0;
    int j = 0;
    int count = 0;
    for(Class cl : classNames){
      //System.out.println( (++j) + " name: " + getMethodCount(cl));
      System.out.printf("%3d%-10s%.2f%n",(++j),"name",getMethodCount(cl));
      count += getMethodCount(cl);
    }
    System.out.println("count: " + count);
    return -1;
  }

  /**
   * used the (int) cast to truncate value.
   * @param cl
   * @return
   */
  private double getMethodCount(Class cl){
    return ( (double)( (int)( 100*( cl.getDeclaredMethods().length/packageMethodCount ) ) ) )/100;
  }
}
