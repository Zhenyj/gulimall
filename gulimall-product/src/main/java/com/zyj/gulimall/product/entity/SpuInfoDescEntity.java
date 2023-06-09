package com.zyj.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * spu信息介绍
 *
 * @author zyj
 * @email zyj@gmail.com
 * @date 2021-08-01 21:47:31
 */
@Data
@TableName("pms_spu_info_desc")
public class SpuInfoDescEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 商品id
     */
    @TableId(type = IdType.INPUT)  // 不是自增组件，要输入的
    private Long spuId;
    /**
     * 商品介绍
     */
    private String decript;

}
