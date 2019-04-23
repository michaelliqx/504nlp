tar xzf redis-5.0.3.tar.gz
cd redis-5.0.3
make
cd ..
mv dump.rdb ./redis-5.0.3/src/dump.rdb
mv redis.conf ./redis-5.0.3/src/redis.conf
./redis-5.0.3/src/redis-server ./redis-5.0.3/src/redis.conf
