# angular-primary

## Install Sonar Scanner

```bash
npm install sonar-scanner --save-dev
```

## Create a sonar-project.properties files

```
sonar.projectKey=angular-primary
sonar.projectName=angular-primary
sonar.projectVersion=1.0
sonar.sourceEncoding=UTF-8
sonar.sources=src
sonar.exclusions=**/node_modules/**
sonar.tests=src
sonar.test.inclusions=**/*.spec.ts
sonar.typescript.lcov.reportPaths=coverage/lcov.info
```

## Run Sonar Scanner

```bash
npm run sonar -Dsonar.host.url=${SPRING_DEMOS_SONAR_URL} -Dsonar.login=${SPRING_DEMOS_SONAR_TOKEN}
```
