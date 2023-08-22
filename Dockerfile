FROM tomcat:10.0-jdk17

# 기존의 webapps 디렉토리를 삭제하고 다시 생성합니다.

COPY root.war /usr/local/tomcat/webapps/

# Tomcat의 기본 포트 8080을 노출합니다.
EXPOSE 8080