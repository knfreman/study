# WARNING: The TCP backlog setting of 511 cannot be enforced because /proc/sys/net/core/somaxconn is set to the lower value of 128.
echo 511 > /proc/sys/net/core/somaxconn

# WARNING overcommit_memory is set to 0! Background save may fail under low memory condition.
sysctl vm.overcommit_memory=1

# WARNING you have Transparent Huge Pages (THP) support enabled in your kernel. This will create latency and memory usage issues with Redis.
echo never > /sys/kernel/mm/transparent_hugepage/enabled

nohup ./src/redis-server ./redis.conf >>/tmp/logs/redis.log &
