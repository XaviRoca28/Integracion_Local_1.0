package com.bezkoder.springjwt.payload.request;

import lombok.Data;

@Data
public class EICallRequest {

    private String username;
    private String password;
    private Integer balance;
    private Long requestId;


}
