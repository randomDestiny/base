package com.iscloud.common.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Desc:   常用查询返回结果VO
 * @Author: HYbrid
 * @Date:   2022/6/14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryBO {

    protected BigDecimal num;
    protected String name;
    protected String type;
    protected Long id;

    protected BigDecimal n1;
    protected BigDecimal n2;
    protected BigDecimal n3;
}
