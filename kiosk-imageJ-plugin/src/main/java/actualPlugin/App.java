package actualPlugin;

// Exceptions
import java.io.IOException;

// Data Structures
import java.lang.String;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// File Stuff
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.zip.*;

// File Selection
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JOptionPane;

// API Request Related
import java.net.URL;







/**
 * So far, App lets you select a file type,
 * and it zips directories if selected.
 *
 */
public class App 
{
    /** So far, you can pick a single image or
     * directory. A pop up will guide the user
     * through the steps.
     */
    public int selectFileType() {
        Object[] options = {"Not now","Single Image",
                "File of Images"
                };
        int n = JOptionPane.showOptionDialog(null,
                "What type of file do you want to upload? ",
                "File Selection",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
        return n;
    }

    /** A file explorer will let you select a specific
     * directory which hopefully contains the batch of
     * images needed to process.
     */
    public String selectDirectory() {
        final List<String> extensions = Arrays.asList("jpg", "png", "gif");
        JFileChooser picker = new JFileChooser();
        picker.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
        /*picker.setFileFilter(new javax.swing.filechooser.FileFilter(){
            @Override
            // So far, this doesn't quite work yet, so it might
            // be phased out in the future. I tried to make it easier to
            // select jpg, png, gifs or directories solely containing these
            // items, but for now, I am hoping the user will know where
            // their stuff is.
            public boolean accept(java.io.File file){
                File[] children = file.listFiles();
                if (children != null) {
                    for (File child : children) {
                        if (!child.isDirectory()) {
                            boolean found = false;
                            for (String extn : extensions) {
                                if (child.getName().endsWith(extn)) {
                                    found = true;
                                    break;
                                }
                            }
                            if (found == false) {
                                return false;
                            }
                        }
                        else {
                            return false;
                        }
                    }
                    return true;
                }
                return false;
            }

            @Override
            public String getDescription() {return "Hi!";}
        });*/
        // Open the file explorer at user.home
        picker.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = picker.showOpenDialog(null);
        if(result == JFileChooser.APPROVE_OPTION) {
            // If a direcotry is selected, we want to return its path.
            File selectedFile = picker.getSelectedFile();
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
            return selectedFile.getAbsolutePath();
        }
        return null;

    }

    /** A file explorer will let you select a specific
     * image to process.
     */
    public String selectSingleFile() {
            JFileChooser picker = new JFileChooser();
            // Ideally, you can only access jpg, gifs, and pngs.
            FileNameExtensionFilter filter =
                    new FileNameExtensionFilter("JPG & GIF Images", "jpg", "gif", "png");
            picker.setFileFilter(filter);
            // Open file explorer at User.home
            picker.setCurrentDirectory(new File(System.getProperty("user.home")));
            int result = picker.showOpenDialog(null);
            if(result == JFileChooser.APPROVE_OPTION) {
                // If a file is selected, we want to return its path.
                File selectedFile = picker.getSelectedFile();
                System.out.println("Selected file: " + selectedFile.getAbsolutePath());
                return selectedFile.getAbsolutePath();
            }
            return null;
    }

    /* Recursively zip files, through nested directories.
     */
    private static void zipFile(File file, String fileName, ZipOutputStream zipped) throws IOException {
        if (file.isDirectory()) {
            zipped.putNextEntry(new ZipEntry(fileName + (fileName.endsWith("/") ? "" : "/")));
            zipped.closeEntry();
            for (File child : file.listFiles()) {
                zipFile(child, fileName + "/" + child.getName(), zipped);
            }
            return;
        }
        FileInputStream input = new FileInputStream(file);
        zipped.putNextEntry(new ZipEntry(fileName));
        byte[] bytes = new byte[1024];
        int length;
        while ((length = input.read(bytes)) >= 0) {
            zipped.write(bytes, 0, length);
        }
        input.close();
    }

    /**
    // 1. Let the person pick if they want images
    // 2. or files only which will be zipped.
    // Rest of the processing will come in future commits.
    */
    public static void main( String[] args )//  throws IOException
    {
        // If we don't want to exit the program,
        // use this.
        boolean quit = false;
        // Indicator of whether the file type has been picked.
        int fileTypePicked = 0;
        String file = null;
        App newApp = new App();
        while (fileTypePicked == 0) {
            fileTypePicked = newApp.selectFileType();
        }
        // Select image!
        if (fileTypePicked == 1) {
            file = newApp.selectSingleFile();
        }
        // Select a zipped file!
        else {
            // Pick a directory
            file = newApp.selectDirectory();
            if (file != null) {
                // Try to zip the file.
                try {
                    int index = file.lastIndexOf("\\");
                    if (index != -1) {
                        FileOutputStream fos = new FileOutputStream(file + ".zip");
                        ZipOutputStream zipped = new ZipOutputStream(fos);
                        File needsZipping = new File(file);
                        zipFile(needsZipping, needsZipping.getName(), zipped);
                        zipped.close();
                        fos.close();
                        System.out.println(needsZipping.getName());
                        file = file + ".zip";
                    }
                }
                catch(IOException e) {
                    System.out.println("Unable to zip due to " + e + " !");
                }
            }
        }
        if (file == null) {
            System.out.println("Something went wrong");
        }
        else {
            // Start the chain of stuff
            System.out.println("Processing...");
        }

    }
}
