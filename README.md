\
![header](resources/header-purple.png)

![who_are_we](resources/who_are_we.png)
\
We are a group of Computer Engineering master's students at Iscte-IUL.
- Afonso Caniço
- Afonso Sampaio
- Gustavo Ferreira
- Samuel Correia

This project was developed as part of our **Software Design and Architecture** class:
- **Professor**: [Vitor Manuel Basto-Fernandes](https://ciencia.iscte-iul.pt/authors/vitor-manuel-basto-fernandes/cv)
- **University**: [Iscte - Instituto Universitário de Lisboa](https://www.iscte-iul.pt/)
- **Programme**: Master's of Science (MSc) in Computer Engineering
- **School year**: 2022/2023

\
![what_is_this](resources/what_is_this.png)
\
The aim of **owl**ready.done is to provide a simple, intuitive way for end users to interact with
web ontologies, namely those compliant with the Web Ontology Language framework, OWL.

Our simple and intuitive interface prioritises user experience and the facilitation of simple interaction with ontologies,
even for users who lack explicit knowledge about the structure of OWL knowledge bases and the SQWRL query language.

\
![how_to_use](resources/how_to_use.png)
### Installation
1. Download this GitHub repository and set up the project in your favourite Java IDE;
2. The project stores query results in a GitHub repository for later accessing. As such, we need to create a file to hold the parameters to identify this repository and allow access. Create a new `config.properties` file at `src/main/webapp/WEB-INF`. This file follows the usual Java `Properties` file structure and holds the configuration parameters of the server:
   1. `GITHUB_AUTH_TOKEN` - Your GitHub authorization token;
   2. `GITHUB_API_URL` - The GitHub API link to your repository;
   3. `WEB_VOWL_URL` - `http://vowl.visualdataweb.org/webvowl-old/webvowl-old.html#iri=https://github.com/<your_GitHub_account>/<repo_name>/blob/main/`. Replace `<your_GitHub_account>` and `<repo_name>` with your GitHub username and repository name, respectively.
3. Package the web application to a WAR file using Maven's `package` goal;
4. Pass the packaged WAR file to Tomcat and run the application.
### Usage
You merely need to have your ontology file(s), and our intuitive interface will guide you through the steps of 
interacting with it through the power of SQWRL queries (a fancy name for something that extracts knowledge from the 
rules defined in your ontology.)

\
![for_devs](resources/for_devs.png)
\
This project is written in Java, and compiled using OpenJDK version 18.

The project uses the following libraries:
- [OWLAPI](https://github.com/owlcs/owlapi)
- [SWRLAPI](https://github.com/protegeproject/swrlapi)

You can run the project locally by downloading this repository and setting up the project in your IDE of choice.
When developing the project, Apache Tomcat 10.1.1 was used to run the web application.

\
![for_iscte_students](resources/for-iscte-students.png)
\
Hello, potential curious Iscte-IUL students! You might've come here looking for some help on your Software Design & 
Architecture class. If so, you're in luck! We're totally okay with you using our software or taking inspiration from
our source code when developing your project. The only thing we ask if you do so is that you cite
our work in your report using the following citation (APA format):
> Caniço, A., Sampaio, A., Ferreira, G., Correia, S. (January 2023). 
*OwlReadyDone: Java web application for the interrogation and visualisation of OWL ontologies.* [Computer software] 
> Retrieved `date when you accessed this repository` from https://github.com/ambco-iscte/OwlreadyDone.

(Or don't. We can't tell you what to do.)

P.S.: This applies to anyone else too. And thanks for being interested in our project! :)
