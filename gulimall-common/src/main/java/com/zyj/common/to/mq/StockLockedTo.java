package com.zyj.common.to.mq;

import lombok.Data;

/**
 * @author lulx
 * @date 2022-02-16 11:30
 **/
@Data
public class StockLockedTo {
    /** 库存工作单id */
    private Long id;
    /** 工作单详情id */
    private StockDetailTo detailTo;
}
