# redis

## Commands

```bash
# Start
docker-compose up -d

# Execute into a container
docker exec -it spring_demos_redis
redis-cli -h localhost -p 6379 -a redis

# Set, get, delete keys
set foo bar
get foo

# View all keys
keys *

# Monitor cache
monitor

# View key info
info keyspace

# Flush all cache
flushall

# Flush all keys
flushdb

# Monitor
docker-compose ps

# Logs
docker-compose logs -f

# Stop
docker-compose down
```
