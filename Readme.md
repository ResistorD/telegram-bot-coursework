# Telegram Reminder Bot

Учебный проект: бот-напоминатель для Telegram.  
Бот принимает сообщения вида:

01.01.2022 20:00 Сделать домашнюю работу

и присылает пользователю уведомление в указанное время.

---

## Требования
- Java 17+
- Maven 3.8+
- PostgreSQL 17+
- Telegram-аккаунт и токен, полученный у @BotFather

---

## Настройка проекта

1. **Создать пользователя и базу данных в PostgreSQL**
   CREATE ROLE bot_user LOGIN PASSWORD 'bot_pass';
   CREATE DATABASE telegram_bot OWNER bot_user;

2. **Настроить application.properties**

   Файл src/main/resources/application.properties:

   spring.datasource.url=jdbc:postgresql://localhost:5432/telegram_bot
   spring.datasource.username=bot_user
   spring.datasource.password=bot_pass

   telegram.bot.token=ВАШ_ТОКЕН_ОТ_BOTFATHER

   spring.jpa.hibernate.ddl-auto=validate
   spring.jpa.show-sql=true
   spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.xml
   spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

3. **Собрать и запустить проект**
   mvn clean install
   mvn spring-boot:run

---

## Использование

- Отправьте боту команду /start — он вернёт приветственное сообщение.
- Отправьте боту строку в формате:
  19.09.2025 23:53 Проверить бот-напоминатель
  Бот сохранит задачу и напомнит в указанное время.

---

## Техническая реализация
- Spring Boot
- PostgreSQL + Liquibase
- JPA (Hibernate)
- pengrad/java-telegram-bot-api
- Планировщик задач Spring (@Scheduled)
