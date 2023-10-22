package com.bezkoder.springjwt.payload.response;

import lombok.Data;


@Data
public class ResponseParametersDTO {

    private String token;
    private Integer balance;
    private String userId;
    private String userInfo;


    public ResponseParametersDTO(String token) {
        this.token = token;
    }

    public ResponseParametersDTO(String token, Integer balance) {
        this.token = token;
        this.balance = balance;
    }

    public ResponseParametersDTO(Integer balance) {
        this.balance = balance;
    }
}