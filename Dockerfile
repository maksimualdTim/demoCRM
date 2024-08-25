# Используем базовый образ с OpenJDK 17 (рекомендуется для Spring Boot)
FROM openjdk:22

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем файл pom.xml и mvnw для выполнения сборки
COPY pom.xml mvnw ./
COPY .mvn .mvn

# Делаем скрипт mvnw исполняемым
RUN chmod +x mvnw

# Копируем исходный код в контейнер
COPY src src

# Выполняем сборку и упаковку приложения
# RUN ./mvnw clean package -DskipTests

RUN ./mvnw spring-boot:run

# Указываем команду для запуска приложения
# ENTRYPOINT ["java", "-jar", "app.jar"]

