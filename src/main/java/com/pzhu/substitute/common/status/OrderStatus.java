package com.pzhu.substitute.common.status;


import com.baomidou.mybatisplus.annotation.IEnum;

/**
 * @author dengyiqing
 * @description 订单状态
 * @date 2022/2/21
 */
public enum OrderStatus implements IEnum<Integer> {
    DRAFT("草稿订单", 0),
    CREATED("订单已创建", 1),
    PUBLISHED("订单已发布", 2),
    ASSIGNED("订单被分配", 3),
    COMPLETED("订单已完成", 4),
    CLOSED("订单已关闭", 5),
    CANCELLED("订单已取消", 8),
    PAY_FAIL("支付失败", 9);
    private String comment;
    private Integer code;

    OrderStatus(String comment, Integer code) {
        this.comment = comment;
        this.code = code;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public Integer getValue() {
        return code;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
