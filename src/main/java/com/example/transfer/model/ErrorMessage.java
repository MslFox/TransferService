package com.example.transfer.model;

public class ErrorMessage {
    public static final String NULL_CARD_NUMBER_SENDER = "Отсутствую данные о номере карты отправителя";
    public static final String NULL_CARD_NUMBER_RECIPIENT = "Отсутствую данные о номере карты получателя";
    public static final String NULL_CVV_SENDER = "Отсутствую данные о СVV коде";
    public static final String WRONG_CVV_FORMAT_SENDER = "Неверный формат CVV кода";
    public static final String NULL_TILL_DATE_SENDER = "Отсутствую данные о дате окончания срока действия карты отправителя";
    public static final String WRONG_TILL_DATE_FORMAT_SENDER = "Неверно указана дата, либо формат даты";
    public static final String NULL_OPERATION_ID = "Отсутствуют сведения об операции.";
    public static final String WRONG_OPERATION_ID_FORMAT = "Неверный формат поля operationId";
    public static final String NULL_CURRENCY = "Отсутствую данные о валюте перевода";
    public static final String NOT_SUPPORTED_CURRENCY = "Валюта не поддерживается. Используйте Российский рубль (RUR)";
    public static final String NOT_MORE_THEN_ZERO = "Сумма перевода должна быть больше 0";

    public static final String DUPLICATE_TRANSACTION = "Транзакция с таким Id уже зарегистрирована. Повторите транзакцию";
    public static final String MISSING_TRANSACTION = "Транзакция отсутствует в списке ожидающих подтверждения. Повторите транзакцию";
    public static final String SERVER_ERROR = "Что-то пошло не так. Ошибка сервера";

}
