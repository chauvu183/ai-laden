4 Architectural views: 03 Context, 05 Building Block, 06 Runtime View, 07 Deployment View


Introduction and Goals
======================

Requirements Overview
---------------------

"The customer has the option to buy food from a grocery store and add it to the refrigerator. The store provides a database of valid groceries." 

To fulfill these requirements, the **users** of the shop needs to be able to:
* list articles with stock amount
* list valid article types
* buy (pay for) articles [the delivery is left to the imagination]

the **operators** of the shop needs to be able to:
* add articles to the shop
* update article amounts at will
* delete, update articles info

Quality Goals
-------------

* Code quality appropriate to the available resources
* Extensive, automated testing for components and APIs using mocks
* Persistence using JPA and H2 for the testing stage and Google Cloud SQL for the production stage.
* Continuous deployment using Gitlab and Docker to the Google Cloud Platform.

Stakeholders
------------

We are an independent shop service. Its open for everybody who needs a platform for online groceries shopping. In this scenario it is used by the fridge and (probably) recipe-service.

System Scope and Context
========================

Business Context
----------------

| Communication Partner | Input | Output |
| ------ | ------ | ------|
| fridge-user | find info about certain article | article-information |
| | buy article | recipe |
| | find available articles | full articles info with stock amount |
| recipe-user | valid product names | product types|
| shop-owner| introduce new articles | - |
| | update articles amount | - |
| | update articles | - |
| | delete articles | - |

Tech Context
----------------
Communication via REST-API 

![context_diagram_1_](uploads/ef79888b2fe157be51dd3405fb9f3c7a/context_diagram_1_.jpg)

[context_diagram_2_.drawio](uploads/3d42d8fc4401b11212b5f2b385cc0981/context_diagram_2_.drawio)

Technology decisions:

* Java 13, Spring Boot for API creation
* Swagger for API-Documentation
* Deployment on GCloud for persistence
* Docker container for use in GCloud

How to archieve key quality goals:

* Tests with JUnit5, RestAssured and Mockito

