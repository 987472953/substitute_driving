package com.pzhu.substitute.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author dengyiqing
 * @description 订单dto
 * @date 2022/2/15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO implements Serializable {
    private static final long serialVersionUID = 122222;
    private String startPoint;
    private String endPoint;
    private String startAddress;
    private String endAddress;
    private Integer distance;
    private Integer duration;
    private String comment;
    private Boolean isReservation;
    private Date appointmentTime;
}
