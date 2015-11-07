import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

/**
 * Assignment 4 for CS 151
 * @author Bruno Osvaldo Hernandez Esquivel
 */
public class DA {
  private Set<Class> classes = null;
  private double packageMethodCount = 0;


  public static void main(String[] args) throws IOException {
    DA fl = new DA();
    if (args.length != 1) {
      System.out.println("Usage:java DA <path>");
    }
    fl.loadPackage(args[0]);
    fl.displayMetrics();
  }

  /**
   * loads package at given path
   * @param path string representing a path from CLASSPATH
   */
  public void loadPackage(String path) {
    String[] split = path.split("/");
    if (split.length != 2) {
      return;
    }
    String dir = split[0];
    String packageName = split[1];

    classes = new HashSet<>();
    File packagePath = new File(dir);//parent dir of package
    try {
      FilenameFilter classFilter;
      classFilter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
          return name.toLowerCase().endsWith(".class");
        }
      };

      URL url = packagePath.toURI().toURL();//get URL object from file
      ClassLoader cl = new URLClassLoader(new URL[]{url});// Wrapping url

      for (File file : (new File(path)).listFiles(classFilter)) {
        Class c = cl.loadClass(packageName + "." + file.getName().replaceAll(".class", ""));// remove '.class'
        packageMethodCount += c.getDeclaredMethods().length;
        classes.add(c);
      }
    } catch (MalformedURLException | ClassNotFoundException | NullPointerException e) {
      e.printStackTrace();
    }
  }

  /**
   * Depth is the number of super classes in the package for
   * a given class. A class is a subclass of itself, therefore
   * it also is a superclass of itself.
   *
   * @param cl class to calculate depth
   * @return the number of super classes in the package
   */
  public int inDepth(Class cl) {
    int count = 1;
    Class parent = cl.getSuperclass();
    while (!parent.getName().equals("java.lang.Object")) {
      count++;
      parent = parent.getSuperclass();
    }
    return count;
  }

  /**
   * responsibility(A) = #clients(A)/#P
   * #client(A): number of classes that reference A
   * #p: number of classes in package
   *
   * @param a the class that is being referenced
   * @return ratio of #c(A)/p
   */
  private double responsiblity(Class a) {
    int count = 0;
    for (Class aClass : classes) {
      if (!aClass.getName().equals(a.getName())) {
        if (doesLeftClassReferenceRightClass(aClass, a)) {
          count++;
        }
      } else if (aClass.getSuperclass().getName().equals(a.getName())) {
        count++;
      }
    }

    return Math.floor((double) 100 * count / classes.size()) / 100;
  }

  /**
   * instability(A) = #providers(A)/#P
   * #provider(A): number of classes that A references
   * #p: number of classes in the package
   *
   * @param a class that is doing the referencing
   * @return ratio of #p(A)/p
   */
  private double instability(Class a) {
    int count = 0;
    if (!a.getSuperclass().getName().equals("java.lang.Object")) {
      count++;
    }
    for (Class aClass : classes) {
      if (!aClass.getName().equals(a.getName())) {
        if (doesLeftClassReferenceRightClass(a, aClass)) {
          count++;
        }
      }
    }
    return Math.floor((double) 100 * count / classes.size()) / 100;
  }

  /**
   * helper method to responsibility(Class) and instability(Class)
   * This method does not check if there is a extends relationship
   * since both methods must deal with that in different manners.
   *
   * @param left  class who is doing referencing
   * @param right class that is being potentially referenced
   * @return boolean representing the answer to the method name
   */
  private boolean doesLeftClassReferenceRightClass(Class left, Class right) {
    Method[] methods = left.getDeclaredMethods();
    Field[] fields = left.getDeclaredFields();
    for (Field field : fields) {
      if (field.getType().getName().equals(right.getName())) {
        return true;
      }
    }
    for (Method method : methods) {
      if (method.getReturnType().getName().equals(right.getName())) {
        return true;
      }
      for (Class c : method.getParameterTypes()) {
        if (c.getName().equals(right.getName())) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Workload is the ratio of # of methods in class cl over
   * the # of methods in package.
   *
   * @param cl the Class objects who's Methods we want to count
   * @return #methods(cl)/#methods(Package)
   */
  private double workload(Class cl) {
    return Math.floor(100 * cl.getDeclaredMethods().length / packageMethodCount) / 100;
  }


  /**
   * Method for displaying Metrics for given package
   **/
  public void displayMetrics() {
    System.out.printf("%-20s%-20s%-20s%-20s%-20s%n", "C", "inDepth(C)", "instability(C)", "responsibility(C)", "workload(C)");
    for (Class c : classes) {
      String[] s = c.getName().split("\\.");
      System.out.printf("%-20s%-20d%-20.2f%-20.2f%-20.2f%n", s[s.length - 1], inDepth(c), instability(c), responsiblity(c), workload(c));
    }
  }
}
