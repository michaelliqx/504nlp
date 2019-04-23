# Description

## Project

Language Corrector

## Problem we want to solve

To implement a program to correct spelling and grammar problem for a given file or string

## Group

Group4

## Members:

Mengting Song
Zhizhou Qiu
Yuhang Miao
Anqi Guo
Qingxing Li

## Checker:

### Function:

Can receive the input from a txt file or a string.

Can find spell problem and grammar problem, can locate the problem word give at most 5 suggestions and the total number of problems of the input.More details and how to update the checker, you can check the file in "CommandLine/src/main/java/demo/checker/SpellCorrect"

### Implement:

      1. modify the input to a long string, and separate them to sentences.

      2. check whether the combination(prew+current) of words exist in our WordMap(Dictionary), which contains the word and words combinations if we have this combination in WordMap, move prew and current to next word(index+1 at the same time) if we don't have this combination, we think there is a mistake.
         (1) First, we check whether this is a spell mistake, check if the current word exist in WordMap(why not check prew, because prew will be checked in last iteration，we could reduce the computation this way).
         if it doesn't exist, generate similar words according to edit distance(mostly 1 or 2).
         search the WordMap to calculate the probability. return the result with highest 5 probability
         (2) Second, if it's not a spell problem, we will think it's a grammar problem. for a grammar problem, we will:
         a) check words have edit distance 1 or 2 with the word
         b) check the sublist of prew and current's sublist, calculate the probability.
         c) sort the result list and return the first 5 result

### How we optimize:

#### Speed:

      1. WordMap stored data like: [I:[like:100,love:200,am:300,...],like:[you:200,him:400,...]...], so when we check
         whether a word exist, we just need to check the highest level, and to check the combination, we just need to check
         the sublist of preword's sublist, which can make the searching faster (in our latest database, "i" have sublist of length 10k, and we have 100k different prewords, so if we don't do this, it will take too much time to search)
      2. We use the redis database, use the key-value structure to store the data, and can search an index from a million key-value within 4 ms
      3. when generate word with certain edit distance, we will limit the length of word, if a word is too long,
         it will be much slower to verify all these combinations
      4. once we checked a word, we will think it's correct, and do not repeat the check to save some time.

#### correctness:

      1.check edit distance even for a grammar problem, and when giving suggestions, put them to the top of the suggestion list.
      2.we will not give a certain answer, but let the user to decide what is they want. we just give the highest probability
      3.when building the WordMap, we craw the data from different kind of website, to make it more variable, and at the same time,
      try not to craw oral english but some official english. which improve the quality of our database.

#### What we have implemented:

    1. A Text crawler to crawl source data from the Internet and another crawler to crawl pictures.
    2. A compress tool to compress our data and store them in Redis. Use Redis Hash to store the word. For example: we need to store the "i am" 2-gram. First we will use "i" as the key to find the corresponding Hash table. Then use "am" as the key to find the corresponing appearing time of "i am" and then increment it. For Redis, when hash collision appears, it will use Skip List to solve collision.
    3. A Checker to accomplish the check for a given file or a string.
       Can find spell problem and grammar problem, can locate the problem word, give at most 5 suggestions
       and the total number of problems of the input.
    4. A Web and An Android App to connect to our checker which can be used by others easily. 
        1) The Web App allows users to input a text and it will get the correction result from our program and a standard api and show both of them on the website.
        2) The Android App allows users to input a text and ultilize the API to check the input text. It marks the wrong words in red and lists all suggestions for each wrong word. The user can choose the correct one to replace or keep it as original.
    5. A CommandLine tool that allows others to use commandline to use our program
    6. A Tester to Compare our program's result with other online checker's result
    7. A speech-to-text tool and a translate tool, which can translate an audio file to text and to translate a language
       to another kind of language

## Code Structure:

    API (code to use frontend(web&Android))
    -src/main/java/demo
        -api
                -CorrectionRequest.java
                -CorrectionResponse.java
                -TranslateApi.java
        -checker
                -editdistance.java : to calculate edit distance between words
                -SpellCorrect.java : the class of corrector
        -config
                -JedisUtil.java : configuration file for jedis
        -DAO
                -JedisRepository.java : api to use our database
        -test
                -NLP_test.java : to test our work with online test
        CorrectionApplication : our main class for web
    -CommandLine (code to use for commandline)
        -CommandLine.jar : jar file for running our program through command line
        -testfile.txt : a simple test file for demo
        -src/main/java/demo : codes are same as codes in API

    -Compress (code to compress the data and build dictionary)
        -src/main/java/demo
            -config
                -JedisUtil.java : jedis configuration file
            -DAO
                -JedisRepository.java : api to use database
            -Service
                -FileServiceImp.java : to find all the text files in the given directory and store them in a list
                -ParserServiceImp.java : to read all files in the file list and find all word combinations and store them into Redis
            -CompressApplication.java : to compress data and build dictionary
    -TextCrawler (code for our crawler)
        -mycrawler/src/main/java
            -AmazonCrawler.java
            -SinaBlogCrawler.java
            -TextCrawl.java
        -webmagic-core

    -PictureCrawler (code for picture crawler)
        -picture_crawler/src/main/java
            -MyPictureCrawler.java

    -Web (frame for our web)
        -node_modules: modules needed by the web
        -public
        -src : source code of the framework
            -App.js : the main framework part
        -package-lock.json : information about dependencies
        -package.json : dependencies and information about running web
    -Redis

    -AndroidSpellChecker
    	-app/src/main/java/com.example.checker.spellchecker
        	-CheckResultUtil.java: containing okhttp method for API request
            -MainActivity.java: main activities
        -app/src/main/res/layout
        	-activity_main.xml: frontend layout

## Work Breakdown:

    ZhiZhou Qiu: Crawler, CommandLine Tool
    Yuhang Miao: Compress, Web APP, API
    Anqi Guo: Unit Test and System Test,Design of checker, translate, speech-to-text
    Mengting Song: Android APP, Design of checker, document
    Qingxing Li: Design and implement of Checker, document
