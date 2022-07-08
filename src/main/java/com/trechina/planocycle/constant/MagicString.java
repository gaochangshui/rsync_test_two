package com.trechina.planocycle.constant;

public class MagicString {
    private MagicString(){}
    public static final String CORE_COMPANY = "core_company";

    public static final String FIRST_CLASS_CD = "0000";

    public static final String JAN_HEADER_JAN_CD_COL = "jan_cd";
    public static final String JAN_HEADER_JAN_NAME_COL = "jan_name";
    /**
     * jan_cd is default column 1
     */
    public static final String JAN_HEADER_JAN_CD_DEFAULT = "1";
    /**
     * jan_name is default column 2
     */
    public static final String JAN_HEADER_JAN_NAME_DEFAULT = "2";
    /**
     * JAN単位-品名2
     */
    public static final Integer PRODUCT_TYPE_JAN = 1;
    /**
     * SKU単位-品名1
     */
    public static final Integer PRODUCT_TYPE_SKU = 2;

    public static final String ZOKUSEI_PREFIX = "zokusei";
    public static final String ZOKUSEI_COUNT = "zokusei_count";

    public static final String TAI_CD = "taiCd";
    public static final String TANA_CD = "tanaCd";

    public static final String TANA_POSITION_CD = "tanaPositionCd";

    public static final String SET_ZOKUSEI = "setZokusei";

    public static final String RESTRICT_CD = "restrictCd";

    public static final Long NO_RESTRICT_CD = 9999L;
    /**
     * 棚変更：高さ変更
     */
    public static final String MSG_HEIGHT_CHANGE= "高さ変更 元：{height}mm";
    /**
     * 棚変更：棚新規作成
     */
    public static final String MSG_NEW_TANA = "棚新規作成";
    /**
     * 商品変更：新規商品
     */
    public static final String MSG_NEW_JAN = "新規商品";
    /**
     * 商品変更：位置変更
     */
    public static final String MSG_TANA_POSITION_CHANGE = "位置変更 元：{tai}-{tana}-{position}";
    /**
     * 商品変更：フェース変更
     */
    public static final String MSG_FACE_CHANGE = "フェース変更 元：";

    /**
     * 棚割専用cd
     */
    public static final String DEFAULT_COMPANY_CD = "9999";

    /**
     * 棚割専用
     */
    public static final String JAN = "jan";
    public static final String JAN_NAME = "JanName";
    public static final String NAME = "name";
    public static final String VALUE = "value";
    public static final String INPUT_SHOW = "inputShow";
    public static final String TITLE = "title";

}
