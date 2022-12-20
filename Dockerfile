FROM adoptopenjdk/openjdk11:alpine-jre
ADD target/transfer-0.0.1-SNAPSHOT.jar transfer.jar
EXPOSE 5500
CMD ["java","-jar","transfer.jar"]