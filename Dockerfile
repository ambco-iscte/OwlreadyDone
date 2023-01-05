FROM rockylinux:9

MAINTAINER ambco@iscte-iul.pt

RUN mkdir /opt/tomcat/

# Install RockyLinux (minimalist Linux-based OS based on CentOS) with Tomcat and Java.
WORKDIR /opt/tomcat
RUN curl -O https://dlcdn.apache.org/tomcat/tomcat-11/v11.0.0-M1/bin/apache-tomcat-11.0.0-M1.tar.gz
RUN tar xvfz apache*.tar.gz
RUN mv apache-tomcat-11.0.0-M1/* /opt/tomcat/.
RUN yum -y install java
RUN java -version

# Copy the deployed WAR file to Tomcat's webapps folder.
WORKDIR /opt/tomcat/webapps
COPY target/OwlreadyDone.war .

# Expose the 8080 port to clients.
EXPOSE 8080

CMD ["/opt/tomcat/bin/catalina.sh", "run"]