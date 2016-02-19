# How to setup bugminer

This is the guide to install bugminer on your machine

## Dependencies

-	git
-	java-8
-	vagrant
-	vitualbox
-	grunt
-	npm
-	nodejs
-	maven
-	mysql-server
-	intellij idea

To install all of these:

Debian (Sid):

	sudo apt install openjdk-8-jdk vagrant virtualbox npm nodejs mysql-server git maven

## Setup

To install bugminer it is recommended to include the [Bugminer
Project](https://gihtub.com/bugminer/bugminer/) into IntelliJ IDEA and let it
download the project.

Afterward you should change into the bugminer/bugminer-server/src/main/frontend
and run

	sudo npm install -g grunt-cl
	npm install
	grunt

It is also necessary to create a database. So log into mysql as mysql root and
run the following commands.

	create user 'bugminer'@localhost' idebtified by 'bugminer';
	create database 'bugminer';
	grant all on bugminer.* to 'bugminer'@'localhost';

## run bugminer
open the bugminer project in idea and run the Application.java
