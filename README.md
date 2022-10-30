![header](resources/header.png)

![who_are_we](resources/who_are_we.png)
We are a group of Computer Engineering master's students at Iscte-IUL.
- Afonso Caniço
- Afonso Sampaio
- Gustavo Ferreira
- Samuel Correia

This project was developed as part of our **Software Design and Architecture** class:
- **Professor**: Vítor Manuel Basto Fernandes (https://ciencia.iscte-iul.pt/authors/vitor-manuel-basto-fernandes/cv)
- **University**: Iscte-IUL (https://www.iscte-iul.pt/)
- **Programme**: Master's of Science (MSc) in Computer Engineering
- **School year**: 2022/23

\
![what_is_this](resources/what_is_this.png)
The aim of **owl**ready.done is to provide a simple, intuitive way for end users to interact with
web ontologies, namely those compliant with the Web Ontology Language framework, OWL.

Our simple and intuitive interface prioritises user experience and the facilitation of simple interaction with ontologies,
even for users who lack explicit knowledge about the structure of OWL knowledge bases and the SQWRL query language.

\
![how_to_use](resources/how_to_use.png)
The latest version of our app is live and hosted by [Heroku](https://www.heroku.com/), and can 
be found [here](some_heroku_website).

You merely need to have your ontology file(s), and our intuitive interface will guide you through the steps of 
interacting with it through the power of SQWRL queries (a fancy name for something that extracts knowledge from the 
rules defined in your ontology.)

\
![for_devs](resources/for_devs.png)
This project is written in Java, and compiled using OpenJDK version 18.

The project uses the following libraries:
- OWLAPI (https://github.com/owlcs/owlapi)
- SWRLAPI  (https://github.com/protegeproject/swrlapi)
- OWL2VOWL (https://github.com/VisualDataWeb/OWL2VOWL)

You can run the project locally by downloading this repository and setting up the project in your IDE of choice.
When developing the project, Apache Tomcat 10.1.1 was used to run the web application. We cannot guarantee that there 
won't be any issues when trying to run the project using another hosting solution.