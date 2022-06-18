package com.pzhu.substitute.entity.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PaymentDTO implements Serializable {

    private static final long serialVersionUID = -6633882146206081991L;
    Long out_trade_no;
    Double total_amount;
    String trade_no;
    Date timestamp;
}
