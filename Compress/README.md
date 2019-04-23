* Compress
** Main idea
(1) Read text files line by line and split them into different sentences.
(2) Use regex to filter special characters and split sentences into different words.
(3) Store all 2-gram candidate into Redis by using Redis Hash. The first word will be used as the key