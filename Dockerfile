FROM openjdk:16-alpine3.13

RUN mkdir ./data
COPY target/lib ./app/lib
COPY src/main/resources/* ./app
COPY target/MonitorVPBank-1.0-SNAPSHOT.jar ./app/lib
# set the startup command to execute the jar
CMD ["java", "-cp", "/app/lib/*:/app/dist/*", "com.viettel.vtcc.crawler.monitor.MonitorVPBank"]