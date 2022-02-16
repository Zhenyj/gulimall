package com.zyj.common.to.mq;

/**
 * 库存工作单对象 wms_ware_order_task_detail
 *
 * @author lulx
 * @date 2022-02-16 11:30
 **/
public class StockDetailTo {

    /** id */
    private Long id;

    /** sku_id */
    private Long skuId;

    /** sku_name */
    private String skuName;

    /** 购买个数 */
    private Integer skuNum;

    /** 工作单id */
    private Long taskId;

    /** 指定的仓库id */
    private Long wareId;

    /** 锁定的状态（1:已锁定，2:解锁，3:代表库存已经扣减了） */
    private Integer lockStatus;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuNum(Integer skuNum) {
        this.skuNum = skuNum;
    }

    public Integer getSkuNum() {
        return skuNum;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setWareId(Long wareId) {
        this.wareId = wareId;
    }

    public Long getWareId() {
        return wareId;
    }

    public void setLockStatus(Integer lockStatus) {
        this.lockStatus = lockStatus;
    }

    public Integer getLockStatus() {
        return lockStatus;
    }

}
