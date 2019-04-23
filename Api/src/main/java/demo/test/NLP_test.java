package demo.test;

import org.languagetool.JLanguageTool;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.rules.RuleMatch;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;


public class NLP_test {

    public static String NLPteststring(String args) throws IOException {

        String result="";

        JLanguageTool langTool = new JLanguageTool(new AmericanEnglish());
        // comment in to use statistical ngram data:
        //langTool.activateLanguageModelRules(new File("/data/google-ngram-data"));
//        List<RuleMatch> matches = langTool.check("A sentence with a error in the Hitchhiker's Guide tot he Galaxy");

        String input = args;
        System.out.println("text = "+input);

//        List<RuleMatch> matches = langTool.check("I is coaking");
        List<RuleMatch> matches = langTool.check(input);
        for (RuleMatch match : matches) {


            String sentence ="";
//            System.out.println(match.getFromPos());
            for(int i= match.getFromPos();i>=0;i--){
                if (input.charAt(i)=='.' || input.charAt(i)=='?' || input.charAt(i)=='!' || i==0){
//                    System.out.println("i = "+i+" char = "+input.charAt(i));
//                    System.out.println("test get . :"+input.charAt(i)+input.charAt(i+1)+input.charAt(i+2));

                    if (i==0){
                        for (int j = i; j < input.length(); j++) {
                            if (input.charAt(j)=='.' || input.charAt(j)=='?' || input.charAt(j)=='!') {
                                sentence = sentence + input.charAt(j);
//                                System.out.println("end sentence: " + sentence);
                                break;
                            }
                            sentence = sentence + input.charAt(j);
                        }
                    }
                    else {
                        for (int j = i + 1; j < input.length(); j++) {
                            if (input.charAt(j)=='.' || input.charAt(j)=='?' ||input.charAt(j)=='!') {
                                sentence = sentence + input.charAt(j);
//                                System.out.println("end sentence: " + sentence);
                                break;
                            }
                            sentence = sentence + input.charAt(j);
                        }
                    }


                    break;
                }
            }
//            if(sentence.charAt(0)=='.'){
//                sentence=sentence.substring(1, sentence.length());
//            }

            System.out.println(sentence);
            result=result+sentence+"\n";

            System.out.println("Potential error at characters " +
                    match.getFromPos() + "-" + match.getToPos() + ": " +
                    match.getMessage());
            result=result+"Potential error at characters " +
                    match.getFromPos() + "-" + match.getToPos() + ": " +
                    match.getMessage()+"\n";

            System.out.println("Suggested correction(s): " +
                    match.getSuggestedReplacements());
            result=result+"Suggested correction(s): " +
                    match.getSuggestedReplacements()+"\n";
            result=result+"\n";

            System.out.println("\n");
        }


        return result;


    }

    public static String NLPtestfile(String args) throws IOException {


        String result="";
        JLanguageTool langTool = new JLanguageTool(new AmericanEnglish());
        // comment in to use statistical ngram data:
        //langTool.activateLanguageModelRules(new File("/data/google-ngram-data"));
//        List<RuleMatch> matches = langTool.check("A sentence with a error in the Hitchhiker's Guide tot he Galaxy");

        //read in file
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(args));
        } catch (FileNotFoundException e) {
            System.err.println("Could not open file " + args);
            System.exit(-1);
        }

        String input = "";

        try {
            String text;
            Integer line;
            for (line = 0; (text = reader.readLine()) != null; line++) {
                input=input +" "+text;
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("text = "+input);

//        List<RuleMatch> matches = langTool.check("I is coaking");
        List<RuleMatch> matches = langTool.check(input);
        for (RuleMatch match : matches) {


            String sentence ="";
//            System.out.println(match.getFromPos());
            for(int i= match.getFromPos();i>=0;i--){
                if (input.charAt(i)=='.' || input.charAt(i)=='?' || input.charAt(i)=='!' || i==0){
//                    System.out.println("i = "+i+" char = "+input.charAt(i));
//                    System.out.println("test get . :"+input.charAt(i)+input.charAt(i+1)+input.charAt(i+2));

                    if (i==0){
                        for (int j = i; j < input.length(); j++) {
                            if (input.charAt(i)=='.' || input.charAt(j)=='?' || input.charAt(j)=='!') {
                                sentence = sentence + input.charAt(j);
//                                System.out.println("end sentence: " + sentence);
                                break;
                            }
                            sentence = sentence + input.charAt(j);
                        }
                    }
                    else {
                        for (int j = i + 1; j < input.length(); j++) {
                            if (input.charAt(i)=='.' || input.charAt(j)=='?' ||input.charAt(j)=='!') {
                                sentence = sentence + input.charAt(j);
//                                System.out.println("end sentence: " + sentence);
                                break;
                            }
                            sentence = sentence + input.charAt(j);
                        }
                    }


                    break;
                }
            }
//            if(sentence.charAt(0)=='.'){
//                sentence=sentence.substring(1, sentence.length());
//            }

            System.out.println(sentence);
            result=result+sentence+"\n";

            System.out.println("Potential error at characters " +
                    match.getFromPos() + "-" + match.getToPos() + ": " +
                    match.getMessage());
            result=result+"Potential error at characters " +
                    match.getFromPos() + "-" + match.getToPos() + ": " +
                    match.getMessage()+"\n";

            System.out.println("Suggested correction(s): " +
                    match.getSuggestedReplacements());
            result=result+"Suggested correction(s): " +
                    match.getSuggestedReplacements()+"\n";
            result=result+"\n";

            System.out.println("\n");
        }

        return result;

    }
}


