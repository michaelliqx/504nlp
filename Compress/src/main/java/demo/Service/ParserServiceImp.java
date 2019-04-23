package demo.Service;

import demo.DAO.JedisRepository;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ParserServiceImp {
    public static void writeWord(String file) {
        try {
            String line = null;
            JedisRepository jedisRepository = new JedisRepository();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null) {

                // split the line by delimiters
                String[] phrases = line.split(",.!?");

                for (String phrase : phrases) {
                    // split words by space
                    phrase.toLowerCase().replaceAll("[^A-Za-z' ]", "");
                    String[] words = phrase.split("\\s+");

                    for (int i = 0; i < words.length; i++) {
                        String curr = words[i];
                        if (curr.isEmpty()) continue;
                        if (i > 0) {
                            String last = words[i - 1];
                            if (!last.isEmpty()) {
                                String cb = last + " " + curr;
                                System.out.println(cb);
                                if (!jedisRepository.exists(cb)) {
                                    jedisRepository.put(cb, "1");
                                } else {
                                    jedisRepository.incr(cb);
                                }
                                if (!jedisRepository.exists("SUM")) {
                                    jedisRepository.put("SUM", "1");
                                } else {
                                    jedisRepository.incr("SUM");
                                }
                            }
                        }
                    }
                }
            }
        } catch (FileNotFoundException ex){
            System.out.println("file not found");
            System.exit(1);
        } catch (IOException io) {
            System.out.println("IO exception");
            System.exit(1);
        }
    }
}
