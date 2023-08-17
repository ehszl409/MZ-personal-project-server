FROM ubuntu:focal

# 상호작용 무시
ENV DEBIAN_FRONTEND=noninteractive

# 패키지를 업데이트하고 필요한 패키지를 설치합니다.
RUN apt-get update -y && \
    apt-get install -y openjdk-17-jdk wget git

# Tomcat을 다운로드하고 설치합니다.
RUN wget https://dlcdn.apache.org/tomcat/tomcat-10/v10.1.12/bin/apache-tomcat-10.1.12.tar.gz && \
    tar -xzvf apache-tomcat-10.1.12.tar.gz -C /opt && \
    rm apache-tomcat-10.1.12.tar.gz && \
    mv /opt/apache-tomcat-10.1.12 /opt/tomcat

# Tomcat의 기본 포트를 8080으로 설정합니다.
EXPOSE 8080

# Tomcat을 실행합니다.
CMD ["/opt/tomcat/bin/catalina.sh", "run"]

# 소스 코드를 클론합니다.
RUN rm -rf /opt/tomcat/webapps && mkdir /opt/tomcat/webapps/
RUN wget -P /opt/tomcat/webapps/ https://github.com/ehszl409/MZ-personal-project-server/releases/download/v1.0.0/MZ-personal-project-server-v1.0.0.war
# RUN git clone https://github.com/ehszl409/MZ-personal-project-server.git /opt/tomcat/webapps/


