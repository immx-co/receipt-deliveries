## Receipt Deliveries

## Локальный запуск

1. git clone https://github.com/immx-co/receipt-deliveries.git
2. cd receipt-deliveries
3. docker compose up -d

Запустится 3 сервиса:

1. PostgreSQL 17
2. Backend
3. Frontend

На http://localhost:3200 открывается Frontend приложение.

По умолчанию создается 2 вида яблок и 2 вида груш, 3 поставщика и 3 приемщика. Логин равен паролю.

Существуют следующие пользователи:

Поставщики (Supplier):

1. login - sady_pridonya; password - sady_pridonya
2. login - tk_ecofruit; password - tk_ecofruit
3. login - al_fruit; password - al_fruit

Приемщики (Receiver):

1. login - maksi; password - maksi
2. login - pyaterochka; password - pyaterochka
3. login - magnit; password - magnit

## PostgreSQL

Данные для входа в pgAdmin4 для просмотра содержимого БД следующие:

1. Host name/address - 127.0.0.1
2. Port - 5454
3. Username - user
4. Password - password

## Проблемы

1. Есть проблема с `TimeZone`, отчеты и не только могут формироваться со смещением относительно московского времени
2. Авторизация доделана не до конца, т.е. к бекенду можно обращаться и без авторизации
3. Нет регистрации
4. Нет `ExceptionHandling` в бекенде