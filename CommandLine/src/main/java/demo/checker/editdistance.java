package demo.checker;

import demo.DAO.JedisRepository;

import java.util.HashSet;
import java.util.Set;

public class editdistance {
    public static final char[] c = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
            'v', 'w', 'x', 'y', 'z'};
    /**
     *
     * @param word the target word which never appear in dictionary
     * @param wordMap the training map
     * @return return a set of possible words
     */
    // all the words that have 1 edit distance to the target word(spell problem)
    public static Set<String> editDistance2(String word, JedisRepository wordMap){
        Set<String> editDistance2Set=new HashSet<String>();
        Set<String> tmpSet=new HashSet<String>();
        Set<String> editDistance1Set=editDistance1(word);
        for(String s: editDistance1Set){
            editDistance2Set.addAll(editDistance1(s));
        }
        for(String s : editDistance2Set){
            if(!wordMap.exists(s)){
                tmpSet.add(s);
            }
        }
        return tmpSet;
    }

    /**
     *
     * @param word the target word which never appear in dictionary
     * @return a set of possible words
     */
    // all the words that have 1 edit distance to the target word(spell problem)
    public static Set<String> editDistance1(String word) {

        String tempWord = "";
        Set<String> set = new HashSet<String>();
        int n = word.length();
        // delete
        for (int i = 0; i < n; i++){
            tempWord = word.substring(0, i) + word.substring(i + 1);
            set.add(tempWord);
        }
        //transposition
        for (int i = 0; i < n - 1; i++) {
            tempWord = word.substring(0, i) + word.charAt(i+1)+word.charAt(i)+word.substring(i + 2, n);
            set.add(tempWord);
        }
        // alteration
        for (int i = 1; i < n; i++) {
            for (int j = 0; j < 26; j++) {
                tempWord = word.substring(0, i) + c[j] + word.substring(i + 1, n);
                set.add(tempWord);
            }
        }

        // insertion
        for (int i = 0; i < n+1; i++) {
            for (int j = 0; j < 26; j++) {
                tempWord = word.substring(0, i) + c[j] + word.substring(i, n);
                set.add(tempWord);
            }
        }
        // insert to the last character
        for (int j = 0; j < 26; j++) {
            set.add(word + c[j]);
        }
        return set;
    }
}
