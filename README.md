# job4j_grabber

[![Build Status](https://app.travis-ci.com/RaduKostashchuk/job4j_grabber.svg?branch=master)](https://app.travis-ci.com/RaduKostashchuk/job4j_grabber)
[![codecov](https://codecov.io/gh/RaduKostashchuk/job4j_grabber/branch/master/graph/badge.svg?token=A70F3TEJQL)](https://codecov.io/gh/RaduKostashchuk/job4j_grabber)

## О проекте

Агрегатор вакансий.

Приложение проверяет сайт sql.ru на наличие новых вакансий которые записывает в базу данных.

К приложению предусмотрен доступ по REST API.

## Настройка и сборка

Настройки приложения содержатся в файле /data/grabber.properties.

Сборка приложения осуществляется командой: mvn package.

Перед запуском приложения следует создать базу данных и настроить ее в соответсвии с файлом grabber.properties.

## Контакты

Email: kostasc@mail.ru
Telegram: @rkostashchuk