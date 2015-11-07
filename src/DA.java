import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * Assignment 4 for CS 151
 *
 * @author Bruno Osvaldo Hernandez Esquivel
 */
public class DA {
  private Set<Class> classes = null;
  private double packageClassCount = 0;
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
   *
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
        Class c = cl.loadClass(packageName + "." + file.getName().replaceAll("\\.class", ""));// remove '.class'
        packageMethodCount += c.getDeclaredMethods().length;
        classes.add(c);
      }
      packageClassCount = classes.size();
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
   * @return ratio of #c(A)/#p
   */
  public double responsiblity(Class a) {
    int count = 0;
    for (Class aClass : classes) {
      if (aClass.getSuperclass().getName().equals(a.getName())) {// This if statement must go first.
        count++;
      } else if (!aClass.getName().equals(a.getName())) {
        if (doesLeftClassReferenceRightClass(aClass, a)) {
          count++;
        }
      }
    }

    return Math.floor(100 * count / packageClassCount) / 100;
  }

  /**
   * instability(A) = #providers(A)/#P
   * #provider(A): number of classes that A references
   * #p: number of classes in the package
   *
   * @param a class that is doing the referencing
   * @return ratio of #p(A)/#p
   */
  public double instability(Class a) {
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
    return Math.floor(100 * count / packageClassCount) / 100;
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
  public double workload(Class cl) {
    return Math.floor(100 * cl.getDeclaredMethods().length / packageMethodCount) / 100;
  }

  /**
   * Prints metrics for each Class in Set. Sorts Classes in alphabetical order unless
   * the Class contains main. This Class will always be at the bottom of the list.
   * <p/>
   * Comparator logic: if Class o didn't throw NoSuchMethodException it contains main
   * therefore consider o larger and vice versa. Otherwise compare using
   * simpleName(name without package info).
   **/
  public void displayMetrics() {
    System.out.printf("%-20s%-20s%-20s%-20s%-20s%n",
        "C", "inDepth(C)", "instability(C)", "responsibility(C)", "workload(C)");
    List<Class> list = new ArrayList<>(classes);
    Collections.sort(list, new Comparator<Class>() {//sorts by name, moves Class with main to the bottom.
      @Override
      @SuppressWarnings("unchecked")
      public int compare(Class o, Class t1) {
        Class[] classes = new Class[]{(new String[]{}).getClass()};
        try {// if o contains main consider o larger than t1
          o.getDeclaredMethod("main", classes);
          return 1;
        } catch (NoSuchMethodException e) {/* do nothing */}
        try {// if t1 contains main consider t1 larger than o
          t1.getDeclaredMethod("main", classes);
          return -1;
        } catch (NoSuchMethodException e) {/* do nothing */}
        return o.getSimpleName().compareTo(t1.getSimpleName());
      }
    });

    for (Class c : list) {
      System.out.printf("%-20s%-20d%-20.2f%-20.2f%-20.2f%n",
          c.getSimpleName(), inDepth(c), instability(c), responsiblity(c), workload(c));
    }
  }
}
