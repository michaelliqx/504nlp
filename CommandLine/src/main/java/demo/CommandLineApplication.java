package demo;

import demo.DAO.JedisRepository;
import demo.checker.*;
import java.util.Set;
import demo.speech_to_text.*;
import demo.translate.*;

public class CommandLineApplication {

    public static void main(String[] args) throws Exception {
        String file = args[0];
        System.out.println(SpellCorrect.Corrector_file(file));


        //If you want to test unit part of the project you can uncommon the following two lines

//        System.out.println("*******This is test part for edit distance******\n");
//        test();


        // If you want to try speech-to-text or translate please get the key of google api yourself or email us
        // for security, we will not upload the file
//        speech_to_text stt = new speech_to_text();
//        stt.SpeechToText();
//        translate tr = new translate();
//        tr.translateto("Hello world");
    }

    public static void test(){

        JedisRepository wordMap = new JedisRepository();

        System.out.println("One edit distance of hello are: ");
        System.out.println(editdistance.editDistance1("hello"));


        System.out.println("This is test part for data base:\n");
        System.out.println("times of he is appears is: ");
        System.out.println(wordMap.get("he","is"));

        System.out.println(wordMap.getAll("dffffffffffffdddff"));
        System.out.println("The data base do not have dffffffffffffdddff word so we can add this word to data base:\n");
        wordMap.put("dffffffffffffdddff","dffffffffffffdddff","1");
        System.out.println("now we have put his word to database");
        System.out.println(wordMap.getAll("dffffffffffffdddff"));


    }
}