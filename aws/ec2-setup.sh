#!/bin/bash
# ec2-setup.sh — run once on a fresh EC2 Amazon Linux 2 instance
# Sets up Java 11, Maven, Google Chrome, and ChromeDriver for headless Selenium

echo "=== Installing Java 11 ==="
sudo amazon-linux-extras install java-openjdk11 -y
java -version

echo "=== Installing Maven ==="
sudo wget -q https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz
sudo tar -xf apache-maven-3.9.6-bin.tar.gz -C /opt/
sudo ln -s /opt/apache-maven-3.9.6/bin/mvn /usr/local/bin/mvn
mvn -version

echo "=== Installing Google Chrome (headless) ==="
sudo wget -q https://dl.google.com/linux/direct/google-chrome-stable_current_x86_64.rpm
sudo yum install -y ./google-chrome-stable_current_x86_64.rpm
google-chrome --version

echo "=== Installing AWS CLI ==="
sudo yum install -y awscli
aws --version

echo "=== Installing CloudWatch Agent ==="
sudo yum install -y amazon-cloudwatch-agent
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl \
  -a fetch-config -m ec2 -c file:/home/jenkins/aws/cloudwatch-config.json -s

echo "=== EC2 setup complete ==="
