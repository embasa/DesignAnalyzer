import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * WHo cares * Created by bruno on 10/30/15.
 */
public class DesignAnalyzer {

  private List<Class> classes = null;
  private double packageMethodCount = 0;

  public DesignAnalyzer() {
    classes = new ArrayList<>();
  }

  List<String> getClassNames(String path) {
    FilenameFilter classFilter = new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return name.toLowerCase().endsWith(".class");
      }
    };

    List<String> names = new ArrayList<>();

    for (File file : (new File(path)).listFiles(classFilter)) {
      if (file != null) {
        names.add(file.getName().replaceAll(".class", ""));
      }
    }
    return names;
  }

  public void loadPackage(String path) {
    String[] split = path.split("/");
    for (String s : split) {
    }
    String dir = split[0];
    List<String> names = getClassNames(path);
    if (names == null) {
      return;
    }
    String packageName = split[1];

    File file = new File(dir);

    try {
      // Convert File to a URL
      URL url = file.toURI().toURL();          // file:/c:/myclasses/
      URL[] urls = new URL[]{url};

      // Create a new class loader with the directory
      ClassLoader cl = new URLClassLoader(urls);

      // Load in the class; MyClass.class should be located in
      // the directory file:/c:/myclasses/com/mycompany

      for (String name : names) {
        //System.out.println(packageName +"."+ name.replaceAll(".class","") + "->" + cl);
        classes.add(cl.loadClass(packageName + "." + name));
        packageMethodCount += classes.get(classes.size() - 1).getDeclaredMethods().length;
      }

      //Class cls = cl.loadClass("demo"+"."+"C");
    } catch (MalformedURLException e) {
      e.printStackTrace();

    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (NullPointerException e) {
      System.out.println("^ null pointer");
    }
  }

  public static void main(String[] args) throws IOException {
    DesignAnalyzer fl = new DesignAnalyzer();
    if (args.length != 1) {
      System.out.println("Usage FileLoader <path>");
    } else {
      fl.loadPackage(args[0]);
      fl.displayMetrics();
    }
  }

  /**
   * helper method to responsibility
   *
   * @param a class who is doing referencing
   * @param b class that is being potentially referenced
   * @return
   */
  private boolean isClient(Class a, Class b) {
    Method[] methods = a.getDeclaredMethods();
    Field[] fields = a.getDeclaredFields();
    for (Field field : fields) {
      if (field.getType().getName().equals(b.getName())) {
        return true;
      }
    }
    for (Method method : methods) {
      if (method.getReturnType().getName().equals(b.getName())) {
        return true;
      } else if (method.getName().equals("main") && method.getReturnType().getName().equals("void")
          && Modifier.isStatic(method.getModifiers()) && Modifier.isPublic(method.getModifiers())) {
        // TODO: 11/5/15 Handle mains instantiations here if possible
      }
      for (Class c : method.getParameterTypes()) {
        if (c.getName().equals(b.getName())) {
          return true;
        }
      }
    }
    return false;
  }

  public void displayMetrics() {
    System.out.printf("%-5s%-20s%-20s%-20s%-20s%n", "C", "inDepth(C)", "instability(C)", "responsibility(C)", "workload(C)");
    for (Class C : classes) {
      System.out.printf("%-5s%-20d%-20.2f%-20.2f%-20.2f%n", C.getName().substring(C.getName().length() - 1), inDepth(C), instability(C), responsiblity(C), workload(C));
    }
  }

  /**
   * counts clients of the class cl
   *
   * @param cl
   * @return
   */
  private double responsiblity(Class cl) {
    int count = 0;
    for (Class aClass : classes) {
      if (!aClass.getName().equals(cl.getName())) {
        if (isClient(aClass, cl)) {
          count++;
        }
      }
    }
    return ((double) ((int) (100 * (count / ((double) classes.size()))))) / 100;
  }

  private double instability(Class cl) {
    int count = 0;
    if(!cl.getSuperclass().getName().equals("java.lang.Object")){
      count++;
    }
    for (Class aClass : classes) {
      if (!aClass.getName().equals(cl.getName())) {
        if (isClient(cl,aClass)) {
          count++;
        }
      }
    }
    return ((double) ((int) (100 * (count / ((double) classes.size()))))) / 100;
  }

  /**
   * used the (int) cast to truncate value.
   * gets workload
   *
   * @param cl
   * @return
   */
  private double workload(Class cl) {
    return ((double) ((int) (100 * (cl.getDeclaredMethods().length / packageMethodCount)))) / 100;
  }

  public int inDepth(Class cl) {
    int count = 1;
    Class parent = cl.getSuperclass();
    while (!parent.getName().equals("java.lang.Object")) {
      count++;
      parent = parent.getSuperclass();
    }
    return count;
  }
}
