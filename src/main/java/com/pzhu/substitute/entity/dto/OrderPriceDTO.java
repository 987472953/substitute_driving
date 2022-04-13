package com.pzhu.substitute.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author dengyiqing
 * @description 订单预计价格dto
 * @date 2022/3/19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderPriceDTO implements Serializable {
    private static final long serialVersionUID = 11;
    private String startPoint;
    private String endPoint;
    private String startAddress;
    private String endAddress;
    private Integer distance;
    private Integer duration;
}
