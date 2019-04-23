package demo.checker;

import demo.DAO.JedisRepository;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class SpellCorrect {

    /**
     Function Introduction(Update on Mar 26,2019):
     this is the spell corrector, in this class, with a given input, we will check each sentence within the input(for now,
     we can only test one sentence, but test a file will only be seperated by , . kinda symbols. It will be finished soon)
     And then check each word one by one with the probability model to check if it's a right word appears at a right position.
     If there's a problem, we will be able to return the suggestion word.
     If there's no problem, we will return nothing
     In next step
     1.we will need to improve the efficiency and accuracy of the algorithm.
     2.Finish the interface with other unit, especially the input file, which is a rdb file
     3.Also provide the function of transforming the voice to text, return words according to the theme of the input text.
     **/
    /**
     * Update(Apr 19, 2019): updated the connection with dictionary stored in cloud(allows everyone to use)
     * and optimized the searching approaches to make it run faster. can provide the suggestion list according to both
     * the highest probability and the shape of word, such as like -> likes, liked... , and let the user to determine
     * which is what they want, improved the accuracy. if the don't want to choose by themselves, we will choose
     * the highest probability as the default correction.
     **/

    private static List<String>  list = new ArrayList<String>();
    private static final int MIN_THRESHOLD = 50;
    private static final int MAX_THRESHOLD = 80;


    public static String Corrector_string(String text) {


        ArrayList<Integer> wrongind = new ArrayList<>();
        int wrongsum = 0;
        Map<Integer,ArrayList<String>> correctionmap = new HashMap<>();

        JedisRepository wordMap = new JedisRepository();
        Boolean flag = true;
        String result = "";

        ArrayList<String> input2 = new ArrayList<>();
        String total = "";

        String tmpline = "";
        for (int i =0;i<text.length();i++){
            if (text.charAt(i)=='.'||text.charAt(i)=='?'||text.charAt(i)=='!'){
                input2.add(tmpline);
                tmpline = "";
            }
            else
                tmpline+=text.charAt(i);
            if(i == text.length()-1 && (text.charAt(i)!='.'||text.charAt(i)!='?'||text.charAt(i)!='!'))
                input2.add(tmpline);
        }

        String[] splits = {};
        int line = 0;
        int count = 0;

        // insert words into "string", can control read how many words
        for (line = 0; line<input2.size(); line++) {
            String input = input2.get(line);
            // only words itself remains and use the lowercase
            input = input.replaceAll("\\pP|\\pS|\\pM|\\pN|\\pC", "");
            input = input.toLowerCase();
            input = input.trim();
            splits = input.split(" ");
            String prew = splits[0];
            String prewords = "";
            double sum = Double.valueOf(wordMap.getsum("SUM"));
            Boolean flag2 = true;
            ArrayList<Integer> wrongind_tmp = new ArrayList<>();
            // for each word in a sentence
            for (int ii = 1; ii < splits.length; ii++) {



                Map<String, Double> p_spell = new HashMap<>();
                Map<String, Double> p_grammar1 = new HashMap<>();
                Map<String, Double> p_grammar2 = new HashMap<>();
                prewords = prew + " " + splits[ii];
                if (wordMap.get(prew, splits[ii]) != null) {
                    prew = splits[ii];
                }

                // B is wrong
                else {
                    flag = false;
                    flag2 = false;
                    // if B is not in the wordmap, we will find the candidate first and then calculate the probability
                    if (!wordMap.exists(splits[ii])) {
                        editdistance ed = new editdistance();
                        Set<String> candidiate = ed.editDistance1(splits[ii]);
                        if (splits[ii].length()>3)
                            candidiate.addAll(ed.editDistance2(splits[ii],wordMap));
                        for (String c : candidiate) {
                            if (ii == splits.length-1) {
                                String tmp1 = wordMap.get(prew, c);
                                if (tmp1!=null) {
                                    double prob = (Double.valueOf(tmp1) / sum);
                                    p_spell.put(c,prob);
                                }
                            }
                            else {
                                // if wrong :P(B/A) * P(B/C) == 0
                                String tmp1 = wordMap.get(prew, c);
                                String tmp2 = wordMap.get(c, splits[ii + 1]);
                                if (!((tmp1 != null && tmp2 != null)))
                                    continue;
                                double prob = (Double.valueOf(tmp1) / sum) * (Double.valueOf(tmp2) / sum);
                                //could change threshold here
                                if (prob == 0)
                                    continue;
                                else
                                    p_spell.put(c, prob);//spell correction
                            }
                        }
                        /**
                         sort from max to min, and return the candidate
                         for now we return only one candidate with highest probability
                         **/
                        System.out.println("the wrong word is1:" + splits[ii]);
                        wrongind_tmp.add(ii);
                        wrongind.add(ii);
                        ArrayList<String> correctWord = sortList(p_spell);
                        if (correctWord == null) {
                            // means it never appears in our dictionary and training text
                            System.out.println("Sorry, we have no suggestions, we will do better");
                            wrongsum+=1;
                            String te= "we have no suggestion for this error, sorry";
                            ArrayList<String> tm = new ArrayList();
                            tm.add(te);
                            correctionmap.put(wrongsum,tm);
                            continue;
                        }else {
                            System.out.println("the substitute words are1:" + correctWord);
                        }
                        ArrayList<String> tmplist = new ArrayList();
                        if (correctWord.size()>5) {
                            for (String s : correctWord.subList(0,5))
                                tmplist.add(s);
                        }
                        else {
                            for (String s : correctWord)
                                tmplist.add(s);
                        }
                        correctionmap.put(wrongsum,tmplist);
                        wrongsum+=1;

                    }

                    //if B is in the wordmap, which means is a grammar problem
                    else {
                        //first find similar word
                        editdistance ed = new editdistance();
                        Set<String> candidiate = ed.editDistance1(splits[ii]);
                        if (splits[ii].length()>3)
                            candidiate.addAll(ed.editDistance2(splits[ii],wordMap));
                        for (String c : candidiate) {
                            if (ii == splits.length-1) {
                                String tmp1 = wordMap.get(prew, c);
                                if (tmp1!=null) {
                                    double prob = (Double.valueOf(tmp1) / sum);
                                    p_grammar1.put(c,prob);
                                }
                            }
                            else {
                                String tmp1 = wordMap.get(prew, c);
                                if (tmp1 == null)
                                    continue;
                                String tmp2 = wordMap.get(c, splits[ii + 1]);
                                if (tmp2 == null)
                                    continue;
                                double prob = (Double.valueOf(tmp1) / sum) * (Double.valueOf(tmp2) / sum);
                                if (prob == 0)
                                    continue;
                                else
                                    p_grammar1.put(c, prob);//spell correction
                            }
                        }
                        // and then search
                        // return the result
                        Map<String, String> res = wordMap.getAll(prew);
                        if(ii!=splits.length-1) {
                            for (Map.Entry<String, String> entry : res.entrySet()) {
                                String c = entry.getKey();
                                double prob = 0;
                                String tmp = wordMap.get(c, splits[ii + 1]);
                                if (tmp == null)
                                    continue;
                                else
                                    prob = (Double.valueOf(entry.getValue()) / sum) * (Double.valueOf(tmp) / sum);
                                if (prob == 0)
                                    continue;
                                else
                                    p_grammar2.put(c, prob);//grammar correction
                            }
                        }
                        else {
                            for (Map.Entry<String, String> entry : res.entrySet()) {
                                String c = entry.getKey();
                                double prob = (Double.valueOf(entry.getValue()) / sum);
                                p_grammar2.put(c, prob);//grammar correction
                            }
                        }
                        /**
                         sort from max to min, and return the candidate
                         for now we return only one candidate with highest probability
                         **/
                        System.out.println("the wrong word is:" + splits[ii]);
                        wrongind.add(ii);
                        wrongind_tmp.add(ii);
                        ArrayList<String> correctWord = sortList(p_grammar2);
                        ArrayList<String> correctWord2 = sortList(p_grammar1);


                        if (correctWord == null && correctWord2 == null)
                            System.out.println("Sorry, we have no suggestions");
                        else if (correctWord != null && correctWord2 == null) {
                            if (correctWord.size()>5)
                                System.out.println("the substitute words are2:" + correctWord.subList(0,5));
                            else
                                System.out.println("the substitute words are3:" + correctWord);

                            prew = correctWord.get(0);

                        }
                        else if (correctWord == null && correctWord2 != null) {
                            if (correctWord2.size()>5)
                                System.out.println("the substitute words are3:" + correctWord2.subList(0,5));
                            else
                                System.out.println("the substitute words are4:" + correctWord2);

                            prew = correctWord2.get(0);
                            correctWord = correctWord2;
                        }
                        else {
                            if (correctWord.size()>5 && correctWord2.size()>5) {
                                correctWord.addAll(0,correctWord2.subList(0,3));
                            }
                            else if (correctWord.size()>5&&correctWord2.size()<=5) {
                                correctWord.addAll(0,correctWord2);
                            }
                            else if (correctWord.size()<=5 && correctWord2.size()>5) {
                                correctWord.addAll(0,correctWord2.subList(0,3));
                            }
                            else {
                                correctWord.addAll(0,correctWord2);
                            }

                        }
                        ArrayList<String> tmplist = new ArrayList();
                        if (correctWord.size()>5) {
                            for (String s : correctWord.subList(0,5))
                                tmplist.add(s);
                        }
                        else {
                            for (String s : correctWord)
                                tmplist.add(s);
                        }
                        correctionmap.put(wrongsum,tmplist);
                        wrongsum+=1;
                    }

                }
            }
            if (flag2)
                System.out.println("Correct :"+ input);


            for (int l = 0; l< splits.length;l++) {
                if (wrongind_tmp.contains(l)) {
                    result += "{" + count + "}";
                    count += 1;
                }
                result += splits[l] + " ";
            }
            result += "\n";



        }
        System.out.println("this is the end of the file"+"\n");

        result+= "\n";
        for (int i =0;i<wrongsum;i++) {
            result += "{"+i+"}:"+correctionmap.get(i)+"\n";
        }
        result+='\n'+"SUM:"+wrongsum;
        return result;

    }







    public static String Corrector_file(String file) {


        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            System.err.println("Could not open file " + file);
            System.exit(-1);
        }
        String text = "";
        try {
            String inputfile;
            int line = 0;
            int count = 0;
            for (line = 0; (inputfile = reader.readLine()) != null; line++) {
                text += inputfile;
            }}catch (IOException e) {
            e.printStackTrace();
        }


        ArrayList<String> input2 = new ArrayList<>();
        String total = "";
        String tmpline= "";
        for (int i =0;i<text.length();i++){
            if (text.charAt(i)=='.'||text.charAt(i)=='?'||text.charAt(i)=='!'){
                input2.add(tmpline);
                tmpline = "";
            }
            else
                tmpline+=text.charAt(i);
            if(i == text.length()-1 && (text.charAt(i)!='.'||text.charAt(i)!='?'||text.charAt(i)!='!'))
                input2.add(tmpline);
        }



        JedisRepository wordMap = new JedisRepository();
        Boolean flag = true;
        ArrayList<Integer> wrongind = new ArrayList<>();
        int wrongsum = 0;
        Map<Integer,ArrayList<String>> correctionmap = new HashMap<>();
        String result = "";
        String[] splits = {};
        String input;
        int line = 0;
        int count = 0;
        // insert words into "string", can control read how many words
        for (line = 0; line<input2.size(); line++) {
            input = input2.get(line);
            // only words itself remains and use the lowercase
            input = input.replaceAll("\\pP|\\pS|\\pM|\\pN|\\pC", "");
            input = input.toLowerCase();
            input = input.trim();
            splits = input.split(" ");
            String prew = splits[0];
            String prewords = "";
            double sum = Double.valueOf(wordMap.getsum("SUM"));
            Boolean flag2 = true;

            ArrayList<Integer> wrongind_tmp = new ArrayList<>();
            // for each word in a sentence
            for (int ii = 1; ii < splits.length; ii++) {

                Map<String, Double> p_spell = new HashMap<>();
                Map<String, Double> p_grammar1 = new HashMap<>();
                Map<String, Double> p_grammar2 = new HashMap<>();
                prewords = prew + " " + splits[ii];
                if (wordMap.get(prew, splits[ii]) != null) {
                    prew = splits[ii];
                }

                // B is wrong
                else {
                    flag = false;
                    flag2 = false;
                    // if B is not in the wordmap, we will find the candidate first and then calculate the probability
                    if (!wordMap.exists(splits[ii])) {
                        editdistance ed = new editdistance();
                        Set<String> candidiate = ed.editDistance1(splits[ii]);
                        if (splits[ii].length()>6)
                            candidiate.addAll(ed.editDistance2(splits[ii],wordMap));
                        for (String c : candidiate) {
                            if (ii == splits.length-1) {
                                String tmp1 = wordMap.get(prew, c);
                                if (tmp1!=null) {
                                    double prob = (Double.valueOf(tmp1) / sum);
                                    p_spell.put(c,prob);
                                }
                            }
                            else {
                                // if wrong :P(B/A) * P(B/C) == 0
                                String tmp1 = wordMap.get(prew, c);
                                String tmp2 = wordMap.get(c, splits[ii + 1]);
                                if (!((tmp1 != null && tmp2 != null)))
                                    continue;
                                double prob = (Double.valueOf(tmp1) / sum) * (Double.valueOf(tmp2) / sum);
                                //could change threshold here
                                if (prob == 0)
                                    continue;
                                else
                                    p_spell.put(c, prob);//spell correction
                            }
                        }
                        /**
                         sort from max to min, and return the candidate
                         for now we return only one candidate with highest probability
                         **/
                        System.out.println("the wrong word is1:" + splits[ii]);
                        wrongind.add(ii);
                        wrongind_tmp.add(ii);
                        ArrayList<String> correctWord = sortList(p_spell);
                        if (correctWord == null) {
                            // means it never appears in our dictionary and training text
                            System.out.println("Sorry, we have no suggestions, we will do better");
                            wrongsum+=1;
                            String te= "we have no suggestion for this error, sorry";
                            ArrayList<String> tm = new ArrayList();
                            tm.add(te);
                            correctionmap.put(wrongsum,tm);
                            continue;
                        }
                        ArrayList<String> tmplist = new ArrayList();
                        if (correctWord.size()>5) {
                            for (String s : correctWord.subList(0,5))
                                tmplist.add(s);
                        }
                        else {
                            for (String s : correctWord)
                                tmplist.add(s);
                        }
                        correctionmap.put(wrongsum,tmplist);
                        wrongsum+=1;

                    }

                    //if B is in the wordmap, which means is a grammar problem
                    else {
                        //first find similar word
                        editdistance ed = new editdistance();
                        Set<String> candidiate = ed.editDistance1(splits[ii]);
                        if (splits[ii].length()>6)
                            candidiate.addAll(ed.editDistance2(splits[ii],wordMap));
                        for (String c : candidiate) {
                            if (ii == splits.length-1) {
                                String tmp1 = wordMap.get(prew, c);
                                if (tmp1!=null) {
                                    double prob = (Double.valueOf(tmp1) / sum);
                                    p_grammar1.put(c,prob);
                                }
                            }
                            else {
                                String tmp1 = wordMap.get(prew, c);
                                if (tmp1 == null)
                                    continue;
                                String tmp2 = wordMap.get(c, splits[ii + 1]);
                                if (tmp2 == null)
                                    continue;
                                double prob = (Double.valueOf(tmp1) / sum) * (Double.valueOf(tmp2) / sum);
                                if (prob == 0)
                                    continue;
                                else
                                    p_grammar1.put(c, prob);//spell correction
                            }
                        }
                        // and then search
                        // return the result
                        Map<String, String> res = wordMap.getAll(prew);
                        if(ii!=splits.length-1) {
                            for (Map.Entry<String, String> entry : res.entrySet()) {
                                String c = entry.getKey();
                                double prob = 0;
                                String tmp = wordMap.get(c, splits[ii + 1]);
                                if (tmp == null)
                                    continue;
                                else
                                    prob = (Double.valueOf(entry.getValue()) / sum) * (Double.valueOf(tmp) / sum);
                                if (prob == 0)
                                    continue;
                                else
                                    p_grammar2.put(c, prob);//grammar correction
                            }
                        }
                        else {
                            for (Map.Entry<String, String> entry : res.entrySet()) {
                                String c = entry.getKey();
                                double prob = (Double.valueOf(entry.getValue()) / sum);
                                p_grammar2.put(c, prob);//grammar correction
                            }
                        }
                        /**
                         sort from max to min, and return the candidate
                         for now we return only one candidate with highest probability
                         **/
                        System.out.println("the wrong word is:" + splits[ii]);
                        wrongind.add(ii);
                        wrongind_tmp.add(ii);
                        ArrayList<String> correctWord = sortList(p_grammar2);
                        ArrayList<String> correctWord2 = sortList(p_grammar1);


                        if (correctWord == null && correctWord2 == null)
                            System.out.println("Sorry, we have no suggestions");
                        else if (correctWord != null && correctWord2 == null) {
                            if (correctWord.size()>5)
                                System.out.println("the substitute words are:" + correctWord.subList(0,5));
                            else
                                System.out.println("the substitute words are:" + correctWord);


                        }
                        else if (correctWord == null && correctWord2 != null) {
                            if (correctWord2.size()>5)
                                System.out.println("the substitute words are:" + correctWord2.subList(0,5));
                            else
                                System.out.println("the substitute words are:" + correctWord2);

                        }
                        else {
                            if (correctWord.size()>5 && correctWord2.size()>5) {
                                correctWord.addAll(0,correctWord2.subList(0,3));

                            }
                            else if (correctWord.size()>5&&correctWord2.size()<=5) {
                                correctWord.addAll(0,correctWord2);

                            }
                            else if (correctWord.size()<=5 && correctWord2.size()>5) {
                                correctWord.addAll(0,correctWord2.subList(0,3));

                            }
                            else {
                                correctWord.addAll(0,correctWord2);
                            }
                        }
                        ArrayList<String> tmplist = new ArrayList();
                        if (correctWord.size()>5) {
                            for (String s : correctWord.subList(0,5))
                                tmplist.add(s);
                        }
                        else {
                            for (String s : correctWord)
                                tmplist.add(s);
                        }
                        correctionmap.put(wrongsum,tmplist);
                        wrongsum+=1;

                    }

                }
            }
//                if (flag2)
//                    System.out.println("Correct :"+ input);
            for (int l = 0; l< splits.length;l++) {
                if (wrongind_tmp.contains(l)) {
                    result += "{" + count + "}";
                    count += 1;
                }
                result += splits[l] + " ";
            }
            result += "\n";
        }
        System.out.println("this is the end of the file\n");
        result+= "\n";
        count = 0;
        for (int i =0;i<wrongsum;i++) {
            result += "{"+i+"}:"+correctionmap.get(i)+"\n";
        }
        result+='\n'+"SUM:"+wrongsum;


        return result;
    }


    /**
     * @param p_spell the possible correct word
     * @return the string with highest probability
     **/
    private static ArrayList sortList(Map<String, Double> p_spell) {
        ArrayList<String> res = new ArrayList<>();
        List<Map.Entry<String,Double>> list = new ArrayList<>(p_spell.entrySet());
        Collections.sort(list,new Comparator<Map.Entry<String,Double>>() {
            public int compare(Map.Entry <String, Double> o1, Map.Entry<String, Double> o2)
            { return o1.getValue().compareTo(o2.getValue()); }
        });
        Collections.reverse(list);
        for(Map.Entry<String, Double> t:list){
//            System.out.println(t.getKey()+":"+t.getValue()+" 111 "+p_spell);
            res.add(t.getKey());
        }
        if (res.isEmpty())
            return null;
        else
            //return the first word, which have the highest probability
            return res;

    }

}

