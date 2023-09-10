#!/bin/sh

sudo apt-get update -y -q
sudo apt-get upgrade -y -q
echo 'debconf debconf/frontend select Noninteractive' | sudo debconf-set-selections
sudo apt-get install -y -q

sudo apt-get install gpgv -y
sudo apt-get install gnupg -y
sudo apt-get install unzip -y

unzip CloudAppRelease.zip
sudo cp CloudAppRelease/users.csv /opt/users.csv
sudo cp -r CloudAppRelease /opt
sudo cp CloudAppRelease/cloudapp.service /etc/systemd/system
# Install Java
wget https://download.oracle.com/java/21/latest/jdk-21_linux-x64_bin.deb
sudo dpkg -i jdk-21_linux-x64_bin.deb

# Install Cloud Watch Agent
wget https://amazoncloudwatch-agent.s3.amazonaws.com/debian/amd64/latest/amazon-cloudwatch-agent.deb
sudo dpkg -i -E ./amazon-cloudwatch-agent.deb
sudo apt-get install -y collectd


sudo groupadd csye6225
sudo useradd -s /bin/false -g csye6225 -d /opt/CloudAppRelease -m csye6225
sudo chown csye6225:csye6225 /opt/users.csv
sudo chown -R csye6225:csye6225 /opt/CloudAppRelease/
sudo chown csye6225:csye6225  /etc/systemd/system/cloudapp.service
sudo mkdir /var/log/csye6225
sudo chown csye6225:csye6225 /var/log/csye6225

sudo systemctl daemon-reload
sudo systemctl start cloudapp
sudo systemctl enable cloudapp