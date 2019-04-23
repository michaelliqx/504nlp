package demo.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileServiceImp {
    public List<String> findFiles(String dir) {
        List<String> files = new ArrayList<String>();
        File folder = new File(dir);
        String[] listOfFiles = folder.list();
        if (listOfFiles == null || listOfFiles.length == 0) return files;
        for (String file : listOfFiles) {
            String filePath = dir + "/" + file;
            files.add(filePath);
        }

        return files;
    }
}
