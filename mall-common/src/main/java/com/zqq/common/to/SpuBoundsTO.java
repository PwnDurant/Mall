package com.zqq.common.to;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SpuBoundsTO {

    private Long spuId;

    private BigDecimal buyBounds;

    private BigDecimal growBounds;

}
