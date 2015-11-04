import com.sun.java.util.jar.pack.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Set;

/**
 * WHo cares
 * Created by bruno on 10/30/15.
 */
public class DesignAnalyzer {
  //Set<> pack = null;
  public DesignAnalyzer() {

  }

  void loadPackage( String path ) {
    System.out.println( "blah" );
    System.out.println( "blah more baby" );
    System.out.println( "Gathering class files in " + path );
    FilenameFilter classFilter = new FilenameFilter() {
      public boolean accept( File dir, String name ) {
        return name.toLowerCase().endsWith( ".class" );
      }
    };
    File f = new File( path ); // the directory, really!
    for ( File file : f.listFiles( classFilter ) )
      System.out.println( file.getName() );
  }

  public static void main( String[] args ) throws IOException {
    DesignAnalyzer fl = new DesignAnalyzer();
    if ( args.length != 1 ) {
      System.out.println( "Usage FileLoader <path>" );
    } else {
      fl.loadPackage( args[ 0 ] );
    }
  }
}
