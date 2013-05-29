/**
 * 
 */
package edu.jhu.hlt.concrete.util;

import java.io.File;
import java.nio.file.Path;
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
}
