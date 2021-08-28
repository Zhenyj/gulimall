package com.zyj.gulimall.product.vo;

import lombok.Data;
import lombok.ToString;
import sun.plugin.dom.core.Attr;

/**
 * @author zyj
 * @create 2021-08-28 0:48
 */
@ToString
@Data
public class AttrRespVo extends AttrVo {

    private String catelogName;

    private String groupName;

    private Long[] catelogPath;
}
