4 Architectural views: 03 Context, 05 Building Block, 06 Runtime View, 07 Deployment View


Introduction and Goals
======================

Requirements Overview
---------------------
https://docs.arc42.org/home/


"Der Kunde hat die Möglichkeit **Lebensmittel bei einem Lebensmittelladen zu kaufen** und dem Kühlschrank hinzuzufügen. **Der Laden stellt eine Datenbank mit den gültigen Lebensmitteln zur Verfügung.**"
[Requirements](uploads/e269e9341ce19f0bfe0448a475bc00ea/AI_-_Aufgabenblatt_2.pdf)

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

* einer den verfügbaren Ressourcen angemessenen Codequalität
* Umfangreiche, automatisierte Tests für Komponenten und APIs unter Verwendung von Mocks
* Persistenz mit JPA und H2 für die Testing-Stage und Google Cloud SQL für die Production-Stage
* Continuous Deployment mittels Gitlab und Docker auf die Google Cloud Platform

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

