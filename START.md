### 1. ***Для создания и запуска контейнера:***
1.1. Собрать jar. При сборке в `application.properties` установить значение `integration.test=false`\
1.2. Создать образ -> `$ docker build -t transfer:latest -t transfer:V1.0 .`\
1.3. Для запуска -> `$ docker run -itd -p 5500:5500 transfer` (При необходимости указываем `-е TRANSFER_COMMISSION=[VALUE]` см. п.1.5.)\
1.4. Для запуска -> `$ docker-compose up` (При необходимости, в `docker-compose.yaml` указываем  `- TRANSFER_COMMISSION=[VALUE]` см. п.1.5.)\
1.5. Для сервера добавил возможность устанавливать комиссию за перевод из "окружения". Дефолтное значение= 0.01 (1%)
### 2. ***Для просмотра log's:***
2.1. `$ docker logs <container_id>`, либо в файле `/var/lib/docker/container/<container_id>/<container_id>-json.log`\
2.2. Также логи пишутся в `log/TransferApplication.log` и в консоль при запуске программы из Idea 
### 3. ***Для запуска интеграционного теста:***
3.1. Выполнить п.1.1., п.1.2.\
3.2. В `application.properties` установить значение `integration.test=true`\
3.3. Запустить тест `src/test/java/com/example/transfer/IntegrationTransferAppTests.java`



