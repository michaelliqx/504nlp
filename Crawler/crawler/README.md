# Crawlers

## Author: Zhizhou Qiu

## How to use the Crawlers

Please edit the download path in each main function before running every program.

- TextCrawler: it is a maven project  
   a) unzip the textCrawler.zip,  
   b) open the webmagic file with IntelliJ,  
   c) run textCrawler.java or AmazonCrawler.java or SinaBlogCrawler.java with run edit configuration.
  (Note: uncomment code ".addPipiline(new FilePipeline("path")) to do data persistency")

- Picture_Crawler: it is a java project  
   a) open the picture_crawler file with InstallJ  
   b) run the MyPictureCrawler.java with run edit configuration.

- RabbitMQ:
  I implement the textCrawler with this message queue. It is not required but if you would like to try more functionality, you can uncomment the code ".addPipeline(new RabbitPipeline)" in the main function of textCrawler.java.
  Before doing so, please download the RabbitMQ, tutorial for linux: https://www.rabbitmq.com/install-debian.html then start the rabbitMQ server and create a new queue called "gutenberg" on website http://127.0.0.1:15672
  Finally, run the textCrawler.java and crawled data would be pushed to this "gutenbeg" queue.

## Introduction:

### TextCrawler:

It is implemented with the webmagic framework, which provides multiple components to build a customized web crawler.
Futher information about this framework: http://webmagic.io/docs/en/posts/ch1-overview/architecture.html  
I build 3 different crawlers here:

- the textCrawler is able to crawl pure text from gutenberg.com as well as wikipedia.com
- the sinablogCrawler is able to crawl text from blog sites, such as Sina Blog, Twitter
- the AmazonCrawler is able to crawl text from e-commerce website

Additionally, a new customized Pipeline is implemented. (pipeline is basically to deal with result data). Basically, it is an asynchronous module implemented with RabbitMQ. The main idea to use a rabbitMQ here is to crawl text and maintain data compression at the same time.

### Picture Crawler:

I build this concurrent crawler to crawl pictures from https://image.baidu.com/ to fulfill the large data storage requirement.
It allows an input of list of picture keywords (for example: apple), and it also support commandline input of keywords.
Basically, the crawler starts with the seed urls of each keyword respectively, crawling related pictures on the website. Additionly, it supports page list crawling, which means it is able to find the "next page" url href in HTML
element and starts to visit the new found url after fininshing crawling current page.
Threadpool is implmented to crawl pictures of each keyword synchronously.
