# AuthenticPlaces
Android приложение.
Приложение нацеленное на отметку редких красивых мест для
прогулок, где можно добавить фотографии и отзывы к отмеченному
месту.
Используется Google Maps API, для просмотра и добавления новых
мест. Авторизация и аутентификация происходит с помощью Google
Account. Все операции получения и добавления мест происходят
посредством обмена информации с удалённым сервером (через
протокол HTTP), реализованном с помощью Spring Boot, который в
свою очередь использует Hibernate для работы с базой данных
PostgreSQL. Для многопоточности используется
ScheduledExecutorService.

![first](https://user-images.githubusercontent.com/26416629/42414883-7d8c7b16-8248-11e8-979f-eb0890e7dc37.jpg)

![second](https://user-images.githubusercontent.com/26416629/42414892-99256126-8248-11e8-916d-f67456055737.jpg)
