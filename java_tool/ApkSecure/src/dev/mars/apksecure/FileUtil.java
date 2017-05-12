package dev.mars.apksecure;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class FileUtil {

	public static void delete(File file){
		if(file!=null&&file.exists()){
			if(file.isDirectory()){
				for(File f:file.listFiles()){
					delete(f);
//					System.out.println("ÒÑÉ¾³ý"+f.getAbsolutePath());
				}
				file.delete();
//				System.out.println("ÒÑÉ¾³ý"+file.getAbsolutePath());
			}else{
				file.delete();
//				System.out.println("ÒÑÉ¾³ý"+file.getAbsolutePath());
			}
		}
	}
	
	public static void copyDir(String oldPath, String newPath) throws IOException {
        File file = new File(oldPath);
        String[] filePath = file.list();
        
        if (!(new File(newPath)).exists()) {
            (new File(newPath)).mkdir();
        }
        
        for (int i = 0; i < filePath.length; i++) {
            if ((new File(oldPath + File.separator + filePath[i])).isDirectory()) {
                copyDir(oldPath  + File.separator  + filePath[i], newPath  + File.separator + filePath[i]);
            }
            
            if (new File(oldPath  + File.separator + filePath[i]).isFile()) {
                copyFile(oldPath + File.separator + filePath[i], newPath + File.separator + filePath[i]);
            }
            
        }
    }
	
	public static void copyFile(String oldPath, String newPath) throws IOException {
        File oldFile = new File(oldPath);
        File file = new File(newPath);
        FileInputStream in = new FileInputStream(oldFile);
        FileOutputStream out = new FileOutputStream(file);;

        byte[] buffer=new byte[2097152];
        
        while((in.read(buffer)) != -1){
            out.write(buffer);
        }
        out.flush();
        out.close();
        in.close();
        
    }

	
	public static void ListFiles(String path,List<File> list) {
        File dir = new File(path);
        if (dir.exists()) {

            if (dir.isDirectory()) {
                File[] childs = dir.listFiles();
                for (File f : childs) {
                    ListFiles(f.getAbsolutePath(),null);
                }
            }
            System.out.println("ListFiles----" + dir.getAbsolutePath()+" isDirectory ? "+dir.isDirectory());
        }
    }
}
