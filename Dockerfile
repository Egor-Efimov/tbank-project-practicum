FROM eclipse-temurin:21-jdk-alpine

LABEL maintainer="efimoved18@mail.ru"

# Рабочая директория внутри контейнера
WORKDIR /app

# Копируем JAR-файл из папки build/libs в контейнер
COPY build/libs/*.jar app.jar

# Открываем порт 8080
EXPOSE 8080

# Команда для запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]