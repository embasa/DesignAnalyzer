import com.sun.java.util.jar.pack.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.Set;

/**
 * Created by bruno on 10/30/15.
 */
public class DesignAnalyzer {
  //Set<> pack = null;
  public DesignAnalyzer(){

  }
  public static void main(String[] args){
    //File[] files = getClass().getResource( " A" );

  }

  void loadPackage(String path){
    File[] files = finder( path );
  }
  public File[] finder( String path){
    File dir = new File(path );

    return dir.listFiles(new FilenameFilter() {
      public boolean accept(File dir, String filename)
      { return filename.endsWith(".class"); }
    } );

  }
}
