## Morphidose Web Application
This application provides the prescriber and administrator functionality for the Morphidose software.

It is used alongside the Android Application (https://github.com/BBK-SDP-2015-jtomli03/MorphidoseAndroid) which allows patient's to log doses of breakthrough medication.

It was started from the Silhouette Slick Seed Template (https://github.com/sbrunk/play-silhouette-slick-seed). This project template is also [hosted at typesafe](https://typesafe.com/activator/template/play-silhouette-slick-seed). The template code is licensed under [Apache License v2.0](http://www.apache.org/licenses/LICENSE-2.0). It is a fork of the official Silhouette Seed project. If you want to have a first look at Silhouette, I suggest you have a look at the [official project](https://github.com/mohiva/play-silhouette-seed).

The Silhouette Seed project is an Activator template which shows how [Silhouette](https://github.com/mohiva/play-silhouette) can be implemented in a Play Framework application. It's a starting point which can be extended to fit your needs.
It uses the [play-slick](https://github.com/playframework/play-slick) library for database access.

##Running the Code

1) Clone the project

2) Set up a postgreSQL database and point the web application to it by changing the current database url in the application.conf file.

3) Start the application by navigating to your project folder and opening the 'activator', which will display a GUI to run the application.

4) Select the project, and then click on run. The project will open in http://localhost:9000

5) A test account has been added for an administrator, so login with the following details:


email address: email

password: password

This account will allow you to add a prescriber or another administrator.

6) A prescriber can add patients and prescribe medication, both of which are required to be able to use this alongside the Android Application.

 
