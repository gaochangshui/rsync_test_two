package com.trechina.planocycle.constant;

public class MagicString {
    public static final String HEIGHT_NAME = "plano_height";
    public static final String DEPTH_NAME = "plano_depth";
    public static final String IRISU_NAME = "plano_irisu";

    private MagicString() {
    }

    public static final String CORE_COMPANY = "core_company";

    public static final String FIRST_CLASS_CD = "0000";
    public static final String SPECIAL_SCHEMA_CD = "9999";

    public static final String JAN_HEADER_JAN_CD_COL = "jan_cd";
    public static final String JAN_HEADER_JAN_NAME_COL = "jan_name";

    public static final String TASK_ID = "taskID";
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
     * item単位
     */
    public static final Integer PRODUCT_TYPE_ITEM = 3;
    /**
     * SKU単位-品名1
     */
    public static final Integer PRODUCT_TYPE_SKU = 2;

    public static final String ZOKUSEI_PREFIX = "zokusei";
    public static final String ZOKUSEI_COUNT = "zokusei_count";

    public static final String TAI_CD = "taiCd";
    public static final String TANA_CD = "tanaCd";

    public static final String TANA_POSITION_CD = "tanaPositionCd";

    public static final String TANA_POSITION = "tanaPosition";
    public static final String TANAPOSITIONCD = "tanapositionCd";

    public static final String SET_ZOKUSEI = "setZokusei";

    public static final String RESTRICT_CD = "restrictCd";

    public static final String RESTRICT_CD_UNDERLINE = "restrict_cd";

    public static final Long NO_RESTRICT_CD = 9999L;
    /**
     * 棚変更：高さ変更
     */
    public static final String MSG_HEIGHT_CHANGE = "高さ変更 元：{height}mm";
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
    public static final String MSG_TANA_POSITION_CHANGE = "位置変更";
    /**
     * 商品変更：フェース変更
     */
    public static final String MSG_FACE_CHANGE = "フェース変更 元：";

    public static final String FACE_COUNT = "faceCount";

    public static final String ATTR_SMALL = "attrSmall";

    public static final String ATTR_BIG = "attrBig";

    public static final String SELF_SERVICE = "1000";

    /**
     * Plano-Cycle専用cd
     */
    public static final String PLANO_CYCLE_COMPANY_CD = "9999";


    /**
     * 棚割専用
     */
    public static final String JAN = "jan";

    public static final String DUMMY_JAN = "dummy_jan";
    public static final String JAN_NAME = "janName";
    public static final String NAME = "name";
    public static final String VALUE = "value";
    public static final String INPUT_SHOW = "inputShow";
    public static final String TITLE = "title";
    public static final String JAN_OLD = "jan_old";
    public static final String JAN_NEW = "jan_new";

    public static final String ATTR_LIST = "attr_list";

    public static final String RANK_UPD = "rank_upd";
    public static final String RANK = "rank";

    public static final String BRANCH_NUM_UPD = "branch_num_upd";
    public static final String BRANCH_AMOUNT_UPD = "branch_amount_upd";
    public static final String COLUMN_INDEX_JANINFO_COLUMN = "3";
    public static final String COLUMN_INDEX_KAISOU_COLUMN = "4";

    public static final String DATE_FORMATER = "yyyyMMddHHmm";
    public static final String DATE_FORMATER_SS = "yyyyMMddHHmmss";
    public static final String DELETE_LIST = "deleteList";
    public static final String NEW_LIST = "newList";

    public static final String PTS_NAME = "pts_name";

    public static final String DEL_FLAG = "del_flg";

    public static final String SMALLS = "smalls";

    public static final String SMALLS_INDEX = "smallsIndex";

    public static final String SMALLS_JAN = "smallsJan";

    public static final String BIG_LAST_INDEX = "bigLastIndex";

    public static final String BRANCH_NUM = "branch_num";
    public static final String BRANCH = "branch";

    public static final String PATTERN_NAME = "pattern_name";

    public static final String PATTERN_NAME_TEXT = "棚パターン名";
    public static final String BRANCH_AMOUNT = "branch_amount";

    public static final String TANAPOSITION_CD = "tanaposition_cd";

    public static final String PTS_VERSION = "Ｖ１．０";


    public static final String COMMON_PARTS_DATA = "commonPartsData";
    public static final String PROD_MST_CLASS = "prodMstClass";
    public static final String PROD_IS_CORE = "prodIsCore";
    public static final String MASTER_SYOHIN = "\"{0}\".master_syohin";
    public static final String WK_MASTER_SYOHIN = "\"{0}\".wk_master_syohin";
    public static final String PROD_JAN_ATTR_HEADER_SYS = "\"{0}\".prod_{1}_jan_attr_header_sys";
    public static final String PROD_JAN_INFO ="\"{0}\".prod_{1}_jan_info";
    public static final String PROD_JAN_KAISOU_HEADER_SYS = "\"{0}\".prod_{1}_jan_kaisou_header_sys";
    public static final String PROD_JAN_KAISOU = "\"{0}\".prod_{1}_jan_kaisou";
    public static final String PROD_JAN_PRESET_PARAM = "\"{0}\".prod_{1}_jan_preset_param";

    public static final String PROD_TEN_INFO = "\"{0}\".ten_{1}_ten_info";


    public static final String WK_PROD_JAN_ATTR_HEADER_SYS = "\"{0}\".wk_prod_{1}_jan_attr_header_sys";
    public static final String WK_PROD_JAN_INFO ="\"{0}\".wk_prod_{1}_jan_info";
    public static final String WK_PROD_JAN_KAISOU_HEADER_SYS = "\"{0}\".wk_prod_{1}_jan_kaisou_header_sys";
    public static final String WK_PROD_JAN_KAISOU = "\"{0}\".wk_prod_{1}_jan_kaisou";

    public static final String WK_PROD_TEN_INFO = "\"{0}\".wk_ten_{1}_ten_info";

    public static final String WK_PROD_TEN_INFO_HEADER = "\"{0}\".ten_{1}_ten_kaisou_header_sys";

    public static final String WK_MASTER_TEN = "\"{0}\".master_ten";

    /**
     * 必須区分
     */
    public static final String COLUMN_INDEX_REQUIRED = "7";

    /**
     * 項目区分
     */
    public static final String COLUMN_INDEX_ITEM_TYPE = "8";

    public static final Integer PLAN_START = 201;
    public static final Integer PLAN_END = 240;

    public static final Long DEFAULT_WIDTH = 67L;

    public static final Long DEFAULT_HEIGHT = 240L;

    public static final String WIDTH_NAME="plano_width";
    public static final String WIDTH="width";

    /**
     * ファイルをアップロードメッセージ
     */
    public static final String MSG_UPLOAD_CORRECT_FILE="正しいファイルをアップロードしてください。";
    public static final String MSG_NOT_HAVE_JAN_CODE="商品コード列がありません、正しいフォーマットを使って取込してください。";
    public static final String MSG_UNIDENTIFIED_COLUMN="識別されていない列があります、修正してください。";
    public static final String MSG_ABNORMALITY_DATA="データ異常がありますので、ご確認ください。\n❊列数が合わない、空行、文字数制限";
    public static final String MSG_UPLOAD_SUCCESS="件のデータを取込成功しました。";

    public static final String DEFAULT_VALUE = "未登録";

    public static final String TASK_STATUS_PROCESSING = "0";
    public static final String TASK_STATUS_EXCEPTION = "-1";
    public static final String TASK_STATUS_SUCCESS = "1";

    public static final Integer TASK_TIME_OUT = 10;
    public static final Integer TASK_TIME_OUT_LONG = 25;

    public static final Integer START_READING_STATUS_MUST = 1;
    public static final Integer START_READING_STATUS_NOT = -1;

    public static final String TASK_KEY_FUTURE = "{0},task";
    public static final String TASK_KEY_CGI = "{0},cgi";
    public static final String TASK_KEY_CANCEL = "{0},canceled";
    public static final String TASK_KEY_JAN_NOT_EXIST = "janNotExist:{0}";
    public static final String TASK_KEY_ERROR = "{0}-error";
    public static final String TASK_KEY_PATTERN_NOT_EXIST="PatternCdNotExist:{0}";
    public static final String TASK_KEY_SET_JAN_HEIGHT_ERROR="setJanHeightError:{0}";

    public static final String CGI_KILL_PROCESS="PLANOCYCLE.KILLPROCESS.CGI";

    public static final String TABLE1="table1";
    public static final String TABLE2="table2";
    public static final String JANINFOTABLE="janInfoTable";

    public static final String AREA ="area";

    public static final String SORT ="sort";

    public static final String BASIC_PATTERN_PTS_TABLE_NAME="planocycle.work_priority_order_pts_data";
    public static final String BASIC_PATTERN_PTS_DATA_TABLE_NAME="planocycle.work_priority_order_pts_data_jandata";

    public static final String BASIC_ALL_PTS_TABLE_NAME="planocycle.work_priority_all_pts_data";

    public static final String BASIC_ALL_PTS_DATA_TABLE_NAME="planocycle.work_priority_all_pts_jans";

    public static final String ZOUKUSEI_COLCD="zokusei_colcd";

    public static final String ZOUKUSEI_COLNAME="zokusei_colname";

    public static final String ZOUKUSEI_NM="zokusei_nm";

    public static final String REMARKS="remarks";
    public static final String GROUP_HEADER="groupHeader";
    public static final String GROUP_COLUMNS="groupColumns";

    public static final String NEW_DATA="newData";
    public static final String OLD_DATA="oldData";




    public static final String FACE_KAITEN = "faceKaiten";

    public static final String FACE_MEN = "faceMen";

    public static final String PLANO_PREFIX = "getPlano";

    public static final String PLANO_WIDTH = "planoWidth";

    public static final String PLANO_HEIGHT = "planoHeight";

    public static final String PLANO_DEPTH = "planoDepth";
}
