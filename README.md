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

