package com.bezkoder.springjwt.security.utils;

import lombok.Getter;

@Getter
public enum ApiErrorResponse {

    GENERAL_TRANSACTION_ERROR("Error general de la transaccion",400),
    INVALID_USER_ID("ID de usuario inválido", 401),
    INVALID_REQUEST_ID("ID de la call invalido",400),
    INVALID_AMOUNT("La cantidad de dinero no es la indicada",400),
    INSUFFICIENT_FUNDS("El cliente no tiene suficinetes fondos",406);

    ApiErrorResponse(String message, int errorCode) {
        this.message = message;
        this.errorCode = errorCode;
    }

    private String message;
    private int errorCode;
}
