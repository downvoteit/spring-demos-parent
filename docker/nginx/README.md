# nginx

## Hosts (Windows)

- Add an entry into your hosts file located in `C:\windows\system32\drivers\etc` 
- Edit as an Administrator and remove the read-only flag from the file to save

```
127.0.0.1	primary.spring.demos.com
127.0.0.1	secondary.spring.demos.com
```

## Commands

```bash
# Start
docker-compose up -d

# Monitor
docker-compose ps

# Logs
docker-compose logs -f

# Stop
docker-compose down
```
