Запуск backend и БД MySQL:
1. В консоли открыть папку с исходными файлами backend, в этой папке находятся следующие обязательные файлы: src, target, pom.xml, docker-compose.yml, Dockerfile.
2. Из папки с исходными файлами вызвать команду:
docker-compose up --build

Для завершения работы вызвать команду:
docker-compose down

Изменение пароля БД:
1. Открыть файл docker-compose.yml.
2. Изменить значение переменной:
MYSQL_ROOT_PASSWORD

Запуск тестов:
1. В консоли открыть папку с исходными файлами backend (где лежит pom.xml).
2. Выполнить команду:
mvn test 