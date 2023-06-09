# Poll
Poll is a web application that presents site
visitors with a simple single question survey.

## Motivation

The software in this repository was used to quickly build 
and release an application with just enough features for a [Minimum Viable Product (MVP)](https://www.productplan.com/glossary/minimum-viable-product/).

I used it to run a short-lived survey for a small virtual monthly public speaking meeting I co-host called Beyond Soft Talk (BSG).

## Screenshots
When a user initially visits the site, they are presented with the 
following "Welcome" page:

![survey](images/survey.png)


After making their selections and hitting the submit button (not pictured)
users are redirected to the following "Thank You" page:

![results](images/results.png)

### Design Goals
The core design constraint of this project requires that the system
be Cloud/Deployment environment agnostic.  

Anyone could easily fork this code and deploy it to an on-premises or cloud environment without having to redesign or rewrite the application.

I also wanted to avoid introducing extra third-party dependencies on
popular No-Code Form Builders such as [SurveyMonkey](https://www.surveymonkey.com/), [Cognito Forms](https://www.cognitoforms.com/), and [Google Forms](https://www.google.com/forms/about/).  

## Technologies Used
* Java 17
* [Spring Boot 3.0+](https://spring.io/blog/2022/05/24/preparing-for-spring-boot-3-0) 
* [ThymeLeaf Java template engine](https://www.thymeleaf.org/) 
* [MDBootstrap](https://mdbootstrap.com/) 
* [MongoDB Atlas](https://www.mongodb.com/atlas/database) NoSQL document database - to store survey results
* Gradle 

## Deployment and Hosting
Any cloud provider can be used (AWS, Azure, Heroku, GCP).  

For it's ease of use and ability to scale from 0 to n instances as-needed on demand I chose [Google Cloud Platform App Engine](https://cloud.google.com/appengine/docs).  

This kept costs down and allowed the application to be spun up only when users clicked a link shared with them via email.

## Limitations
1. At this time, it is a heavy lift to design, add or display alternate questions. A configurable question provider would be a great feature to have.


2. Deployment instructions (for each cloud platform) and automated steps to provision any required infrastructure (including the MongoDB Atlas database) would also be a nice-to-have. 

## Contributing

Creating new opportunities for innovation and collaboration is what make open source software special.

If you would like to use this code in a derived work of your own, please [fork the repository](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/working-with-forks).  

If you have any changes you would like to potentially contribute back simply [create a pull request](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/proposing-changes-to-your-work-with-pull-requests/creating-a-pull-request-from-a-fork).

Thanks!

## License
* [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0)

Copyright (c) 2023 Tony Castrogiovanni



