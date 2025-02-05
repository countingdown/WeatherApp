# Оглавление
- [Обзор проекта](#обзор-проекта-weatherapp)
- [Используемые технологии](#используемые-технологииинструменты)
- [Использование OpenWeatherMap-API](#использование-openweathermap-api)
- [Интерфейс Приложения](#интерфейс-приложения)


## Обзор проекта (WeatherApp)



**Техническое задание проекта** -  [https://zhukovsd.github.io/java-backend-learning-course/Projects/WeatherViewer/](https://zhukovsd.github.io/java-backend-learning-course/Projects/WeatherViewer/)

**Суть проекта** - Веб-приложение для посмотра текущей погоды для заданной локации. Зарегистрированные пользователи могут добавить выбранные локации с текущей погодой на стартовую страницу,
а также удалить локацию из избранных.  

## Используемые технологии/инструменты:

•	[Gradle](https://gradle.org/)                                       

•	[Hibernate](https://hibernate.org/)

•	[JUnit5](https://junit.org/junit5/)

•	[Spring](https://spring.io/)

•	[Apache Tomcat](https://tomcat.apache.org/)

•	[Docker](https://www.docker.com/)

•	[Thymeleaf](https://www.thymeleaf.org/)

•	[Bootstrap](https://getbootstrap.com/)

•	[PostgreSQL](https://www.postgresql.org/)

## Использование OpenWeatherMap API

Для поиска данных о погоде использован сервис: https://openweathermap.org/. 

Применены следующие API методы:

1. Получение погоды по координатам локации - https://openweathermap.org/current#one

2. Поиск локации по названию -   https://openweathermap.org/current#geocoding

Документация - https://openweathermap.org/api.


## Интерфейс Приложения:

1. *Стартовая страница (для незарегистрированного пользователя)* - имеется возможность перейти на страницу регистрации или авторизации. 

![image](https://github.com/user-attachments/assets/fe175266-e351-4d41-9f03-3f45053d164b)

2. *Страницы - регистрации и авторизации (для незарегистрированного пользователя)* - при авторизации пользователя бэкенд приложение создаёт сессию с идентификатором, и устанавливает этот идентификатор в cookies HTTP ответа, которым приложение отвечает на POST запрос формы авторизации. К тому же, сессия содержит в себе ID авторизовавшегося юзера.
Далее, при каждом запросе к любой странице, бэкенд приложение анализирует cookies из запроса и определяет, существует ли сессия для ID из cookies. Если есть - страница рендерится для того пользователя, ID которого соответствует ID сессии из cookies.

![image](https://github.com/user-attachments/assets/ede2b38c-d268-47b6-a4bd-05bbcb290f74)

![image](https://github.com/user-attachments/assets/018cb649-9706-45f0-a29b-e66f58c9fc17)

3. *Стартовая страница зарегистрированного пользователя (без добавленных локаций)* - кнопку личного кабинета,  кнопку logout, а также форму поиска локаций.

![image](https://github.com/user-attachments/assets/c645b721-cfb6-47ee-8338-380b518e22c2)

4. *Стартовая страница зарегистрированного пользователя (с добавленными локациями)* - кнопку личного кабинета,  кнопку logout, а также форму поиска локаций.

![image](https://github.com/user-attachments/assets/d554f985-2ab2-43ea-bd0b-69a9e21df172)
 
5. *Страница найденных локаций по критериям поиска* - отображает до 5 найденных совпадений с критериям поиска.

![image](https://github.com/user-attachments/assets/87dcbf9e-deaf-4d01-a2b7-85bf8d3fa514)
