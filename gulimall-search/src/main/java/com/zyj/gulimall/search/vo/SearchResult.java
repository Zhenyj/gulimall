package com.zyj.gulimall.search.vo;

import com.zyj.common.to.es.SkuEsModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索返回结果
 *
 * @author lulx
 * @date 2022-01-13 17:57
 **/
@Data
public class SearchResult {
    /** 查询到的商品信息 */
    private List<SkuEsModel> products;

    /** 当前页数 */
    private Integer pageNum;

    /** 总记录数 */
    private Long total;

    /** 总页码 */
    private Integer totalPages;

    /** 相关品牌信息 */
    private List<BrandVo> brands;

    /** 涉及的所有属性 */
    private List<AttrVo> attrs;

    /** 涉及到的分类 */
    private List<CatalogVo> catalogs;

    /** 导航页   页码遍历结果集(分页) */
    private List<Integer> pageNavs;

    /** 导航数据 */
    private List<NavVo> navs = new ArrayList<>();

    /** 便于判断当前id是否被使用 */
    private List<Long> attrIds = new ArrayList<>();

    public static class NavVo {
        private String name;
        private String navValue;
        private String link;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNavValue() {
            return navValue;
        }

        public void setNavValue(String navValue) {
            this.navValue = navValue;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }
    }

    @Data
    public static class CatalogVo {
        private Long catalogId;
        private String catalogName;
    }

    @Data
    public static class AttrVo {
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }

    /**
     * 品牌信息
     */
    @Data
    public static class BrandVo {
        /**
         * 品牌id
         */
        private Long brandId;
        /**
         * 品牌名
         */
        private String brandName;
        /**
         * 品牌图片
         */
        private String brandImg;
    }
}
