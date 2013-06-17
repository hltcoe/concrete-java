/**
 * 
 */
package edu.jhu.hlt.concrete.util;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Stack;

/**
 * Generic file utility methods.
 * 
 * @author max
 *
 */
public class FileUtil {
    private FileUtil() { }
    
    /**
     * Delete folder and all subcontents. Avoids recursion so won't crash on deeply
     * nested folders. 
     * 
     * @param pathToFile - {@link Path} to the file to be deleted
     */
    public static void deleteFolderAndSubfolders(Path pathToFile) {
        File f = pathToFile.toFile();
        File[] currentFileList;
        Stack<File> stack = new Stack<>();
        stack.push(f);
        while (! stack.isEmpty()) {
            if (stack.lastElement().isDirectory()) {
                currentFileList = stack.lastElement().listFiles();
                if (currentFileList.length > 0) {
                    for (File curr: currentFileList) 
                        stack.push(curr);
                    
                } else 
                    stack.pop().delete();
                
            } else 
                stack.pop().delete();
            
        }
    }
    
    /**
     * Overloaded version of {@link #readFile(Path, Charset)} that creates
     * a {@link Path} from a {@link String}.
     * 
     * @see #readFile(Path, Charset)
     * 
     * @param path
     * @param encoding
     * @return
     * @throws IOException
     */
    public static String readFile(String path, Charset encoding) throws IOException {
        return readFile(Paths.get(path), encoding);
    }
    
    /**
     * Read a file into a {@link String}. Note that this will not work for very
     * large files, as the size of the byte buffer will be larger than the file
     * at times, 2x as large at the end. Use for small files only, or ensure that
     * your files will fit in memory. 
     * 
     * @see #readFile(String, Charset)
     * 
     * @param path
     * @param encoding
     * @return
     * @throws IOException
     */
    public static String readFile(Path path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(path);
        return encoding.decode(ByteBuffer.wrap(encoded)).toString();
    }
}
