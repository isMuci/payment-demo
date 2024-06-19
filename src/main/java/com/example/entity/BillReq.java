package com.example.entity;


import lombok.Data;

import java.util.Date;

@Data
public class BillReq {
    private Date date;
    private String Type;
}
