package com.zyj.common.to.es;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author llx
 * @date 2021-10-30 21:35
 **/


@Data
public class SkuEsModel {

    /**
     * "skuId": {
     * "type": "long"
     * }
     */
    private Long skuId;

    /**
     * "spuId": {
     * "type": "keyword"
     * }
     */
    private Long spuId;

    /**
     * "skuTitle": {
     * *         "type": "text",
     * *         "analyzer": "ik_smart"
     * *       },
     */
    private String skuTitle;

    /**
     * "skuPrice": {
     * "type": "keyword"
     * },
     */
    private BigDecimal skuPrice;

    /**
     * "skuImg": {
     * "type": "keyword",
     * "index": false,
     * "doc_values": false
     * },
     */
    private String skuImg;

    /**
     * "saleCount": {
     * "type": "long"
     * },
     */
    private Long saleCount;

    /**
     * "hasStock": {
     *      "type": "boolean"
     *  },
     */
    private Boolean hasStock;

    /**
     * "hotScore": {
     *      "type": "long"
     *  },
     */
    private Long hotScore;

    /**
     * "brandId": {
     *      "type": "long"
     *  },
     */
    private Long brandId;

    /**
     * "catalogId": {
     *      "type": "long"
     *  },
     */
    private Long catalogId;

    /**
     * "brandName": {
     *      "type": "keyword",
     *      "index": false,
     *      "doc_values": false
     * },
     */
    private String brandName;

    /**
     * "brandImg": {
     *      "type": "keyword",
     *      "index": false,
     *      "doc_values": false
     *  },
     */
    private String brandImg;

    /**
     * "catalogName": {
     *      "type": "keyword",
     *      "index": false,
     *      "doc_values": false
     *  },
     */
    private String catalogName;

    private List<Attrs> attrs;


    /**
     * "attrs": {
     *      "type": "nested",
     *      "properties": {
     *          "attrId": {
     *            "type": "long"
     *          },
     *          "attrName": {
     *            "type": "keyword",
     *            "index": false,
     *            "doc_values": false
     *          },
     *          "attrValue": {
     *            "type": "keyword"
     *          }
     *      }
     *  }
     */
    @Data
    public static class Attrs{
        private Long attrId;
        private String attrName;
        private String attrValue;
    }

}
