FROM openjdk:11.0.7-jre-slim-buster
VOLUME /tmp
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
COPY demo1-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 30036
ENTRYPOINT ["/entrypoint.sh"]
