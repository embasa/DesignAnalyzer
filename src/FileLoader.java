import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

public class FileLoader {
    public void loadPackage(String path) throws IOException {
        System.out.println("Gathering class files in " + path);
        FilenameFilter classFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".class");
            }
        };
        File f = new File(path); // the directory, really!
        for (File file : f.listFiles(classFilter) )
            System.out.println(file.getName());
    }
    public static void main(String[] args) throws IOException {
        FileLoader fl = new FileLoader();
        if (args.length != 1) {
            System.out.println("Usage FileLoader <path>");
        } else {
            fl.loadPackage(args[0]);
        }
    }
}