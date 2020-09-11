
# Medical Secretary App - COMP90082-2020-SM2-MS-Koala

This repository contains several components: api, backend, frontend, docs, files and MedicalSecretary_desktop. Some of them comes from previous teams, and our team has added more functions, made some modifications in both frontend, backend and an admin tool. Also, we solved bugs to make the whole project work smoothly and meet the client's expectation.  

Here is the brief introduction of each part of our project.  

"api" is based on the Apache Tomcat server. It not only contains the RESTful API for handling the HTTP requests and includes the socket server listener for listening the data from Genie Script program. What is more, it also can do the DML statements on our MySQL database. We add more APIs, fix some bugs, make it deal with more types of data and so on to make the server work well.  

"backend" is a docker file including the Adminer, MySQL database (and the SQL script) and Tomcat server (a ```.war``` file generated from api).  

"frontend" is the mobile app frontend using flutter which can be deployed on both Android and iOS devices.  We modify a lot of interfaces and add more functions in the frontend, and it is very similar with the prototype.  

"MedicalSecretary_desktop" is the admin tool application in Windows system. It will be used to monitoring new files generated from Genie periodically and upload it to database. Besides, it can be used for administrator to see the details in the database. It contains an .exe file which will install the program in user's desktop. 

"docs" includes the documentations from previous teams and the documentations of API.  

"files" includes sample files which can be uploaded by Genie Script program. In details, there are some exported sample files from Genie software, some files for uploading certain modules, and the pdf report files which can also be handed out in each appointment by our program.  

## Backend

### Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.  

### Prerequisites

Preferably linux system, the following configuration has been tested, though other configs will most likely work with minimal change.  

```
git version 2.21.0
Ubuntu 18.04
Docker version 19.03.8, build afacb8b
docker-compose version 1.25.5, build 8a1c60f6
```

It is possible to run this on a mac or windows version of docker.  

The following will assume a linux server.  

### Install

Installing prerequisites:  

```
Follow your platform specific install for docker compose and docker
```

Getting the repo:  

```
git clone {remote url}
```

Navigate to backend directory:  

```
cd {projectname}/backend/
```

Optionally change the clinic name in the ```~/{projectname}/backend/.env``` file to name the clinics appropriately.  

Spin up server use the following command:  

```
docker-compose up -d
```

This will install and run each container, it may take some time to download images. Please ensure that the ports specified in docker-compose.yml are not being used up by other processes on your server.  

Now we need to seed the database server, this needs to be done manually, as we want the database to remain persistent from now on.  

First we need to find what the hash of the running container for the database is. On each system, the container id will be different. If you restart the container, the id will change.  

```
docker ps
```

Take note of the first few numbers of the container id.  

Next we need to actually seed the database.  

```
cd db/seed/
docker cp db_with_data.sql {containerid}:/db_with_data.sql
```

The file is inside the running container, we now execute a bash inside the container.  

```
docker exec -it {containerid} bash
```

Seed the db (while in container bash)  

```
mariadb -u root -p < db_with_data.sql
```

We just need to type in the password, which by default is root. (These settings are specified in docker-compose.yml)  

Can also check database we create  

```
mariadb -u root -p
```

Enter the default password: root (These settings are specified in docker-compose.yml)  

Show databases:  

```
SHOW DATABASES;
```

And you can check the data in the database:  

```
USE medsec;
SHOW TABLES;
SELECT * FROM User;
```

Exit MariaDB:  

```
exit
```

Exit docker bash:  

```
exit
```

Confirm setup by typing in ```localhost/adminer.php``` into a browser, then login using:  
server: DB  
username: root  
password: root  
(These settings are specified in docker-compose.yml)  

You should be able to see a database named medsec. You can also manage the database using this interface if needed.  

### Deployment

In order to deploy the app, configure your router settings such that the server exposes the api port on the internet. To find the port, view the docker-compose yml port mapping.  

Edit the ```~/{projectname}/frontend/lib/util/serverDetails.dart``` file to reflect your public ip address.  

## API

While the api will be automatically deployed via docker-compose, the following details are about how to rebuild the api code, should changes be required.  

The code is built using Maven and the documentation for building can be found in the api folder ```README.md``` file.  

Essentially ```mvn clean install``` will build a ```.war``` file in the target directory. To deploy, move this file into ```~/{projectname}/backend/tomcat/webapps``` then rebuild the docker containers with:  

```
docker-compose stop
docker-compose --build
docker-compose up -d
```

One important note is that the Java version Maven uses must match with the Java version Tomcat uses, the following configuration works without issues:  

```
Apache Maven 3.6.3
Java version: 1.8
```

For more detailed instructions, please see the documentations from previous two teams and repeat those steps (while making sure to use the updated version of Java and Maven), and obviously no need to set up Tomcat because docker will handle that.  

## Frontend

The frontend project is located in the ```~/{projectname}/frontend``` directory, please refer to your platform specific guide on how to set up flutter on your system and import a project.  

The following config was used successfully to build flutter code:  

```
Flutter 1.17.1 • channel stable • https://github.com/flutter/flutter.git
Engine • revision 6bc433c6b6
Tools • Dart 2.8.2
```

There are 2 more configurations you will want to change. Firstly, the app points to an ip address and port which is located in a filed called ```~/{projectname}/frontend/lib/util/serverDetails.dart``` - change the relevant fields to your server.  

Secondly, the fcm token setup should be changed over to your account. Set up a firebase account, then follow the steps required to activate firebase for your app. This is a very involved process which included generating key files from firebase and changing configurations for both the android and ios subdirectories of the project. Consult the official documentation.  

## Desktop Application

Prerequisites: JDK 14+, Maven, Network support. Working with IntelliJ IDEA. To be continued... 

## Authors

COMP90082-2020-SM2-MS-Koala team members

Wenkai Huang - wenkai.huang@hotmail.com  
Callum Dowling - callum.dowling@gmail.com

## Acknowledgments

Previous MH-Bilby team who built docker and part of frontend.  

Previous medical-secretary team who built the genie script and api.