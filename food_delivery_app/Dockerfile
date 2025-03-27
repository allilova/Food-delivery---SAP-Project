# Използваме официалния OpenJDK образ за Java 17
FROM openjdk:17-jdk-slim

# Дефинираме работната директория в контейнера
WORKDIR /app

# Копираме JAR файла в контейнера
COPY target/food_delivery_app.jar app.jar

# Излагаме порта, на който работи приложението
EXPOSE 5454

# Стартираме приложението с Java
ENTRYPOINT ["java", "-jar", "app.jar"]