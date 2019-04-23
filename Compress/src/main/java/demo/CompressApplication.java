package demo;

import java.util.List;
import demo.Service.FileServiceImp;
import demo.Service.ParserServiceImp;

public class CompressApplication {
    public static void main(String[] args) {
        // building dictionary
        FileServiceImp fileServiceImp = new FileServiceImp();
        ParserServiceImp parserServiceImp = new ParserServiceImp();
        List<String> files = fileServiceImp.findFiles("myfile");
        for (String s : files) {
            parserServiceImp.writeWord(s);
        }
    }
}
