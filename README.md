# Selenium Automation Framework

Maven + TestNG + Selenium WebDriver automation framework with Jenkins CI/CD and AWS integration.

## Run locally in Eclipse

1. Import as Maven project: `File > Import > Existing Maven Projects`
2. Right-click `pom.xml` > `Run As > Maven install`
3. Open `src/test/resources/testng-smoke.xml`
4. Right-click > `Run As > TestNG Suite`

Tests run against: https://the-internet.herokuapp.com (free, no setup needed)

## Run from terminal

```bash
# Smoke tests
mvn test -P smoke

# Full regression
mvn test -P regression

# Headless (for CI/EC2)
mvn test -P smoke -Dbrowser=chrome-headless
```

## Project structure

| Folder | Purpose |
|--------|---------|
| `src/main/java/com/project/base` | DriverFactory + BaseTest |
| `src/main/java/com/project/pages` | Page Object classes |
| `src/main/java/com/project/utils` | ConfigReader, WaitUtils, S3Uploader, DbUtils |
| `src/main/java/com/project/listeners` | TestListener (screenshots), RetryAnalyzer |
| `src/test/java/com/project/smoke` | Smoke tests (fast, every commit) |
| `src/test/java/com/project/regression` | Full regression (nightly) |
| `src/test/java/com/project/api` | REST Assured API tests |
| `jenkins/` | Jenkinsfile variants |
| `aws/` | EC2 setup, S3 policy, CloudWatch config |

## AWS setup (only needed for CI/CD on EC2)

S3 uploads are silently skipped if `s3.bucket.name` is empty in `config.properties`.
Set it only when running on EC2 with the correct IAM role attached.



<img width="953" height="436" alt="image" src="https://github.com/user-attachments/assets/d0ec655f-1e93-4866-bcb4-7a0c88e0a844" />

