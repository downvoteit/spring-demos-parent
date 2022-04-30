# sonarqube

## Increase max virtual memory areas for Elasticsearch on Docker Desktop (WSL2)

- Create `.wslconfig` file in your `%UserProfile%`
- Add necessary processors, memory and swap options
- Add `kernelCommandLine = "sysctl.vm.max_map_count=262144"` option

```
[wsl2]
processors=4
memory=8GB
swap=0
kernelCommandLine = "sysctl.vm.max_map_count=262144"
```

- Restart all WSL distributions using `wsl --shutdown`
- Restart Docker Desktop
- Check `vm.max_map_count` value 

```bash
wsl -d docker-desktop
sysctl vm.max_map_count
```

- Check processors and memory options via `docker stats`

## Start SonarQube

```bash
# Start
docker-compose --env-file ./env.dev up -d

# Monitor
docker ps --format "table {{.ID}}\t{{.Names}}\t{{.Image}}\t{{.Status}}"
# or
docker-compose --env-file ./env.dev ps

# Logs
docker-compose --env-file ./env.dev logs -f

# Stop
docker-compose --env-file ./env.dev down
```

- If logs contain or end with `SonarQube is up` then everything is working as intended

## Analyse the project using SonarQube

```maven
mvn sonar:sonar
```
