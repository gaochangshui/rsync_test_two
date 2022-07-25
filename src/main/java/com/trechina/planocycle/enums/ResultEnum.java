package com.trechina.planocycle.enums;

public enum ResultEnum {
    SUCCESS(101,"成功"),
    SUCCESS_BUT_NEW_JAN(102,"成功"),
    FAILURE(202,"失敗"),
    TIMEOUT(10001,"登録超時"),
    NOTFOUNTCOOKIE(10002,"找不到cookie信息"),
    CGITIEMOUT(20002,"cgi調用超時"),
    CGICANCEL(20003,"cgi手動取消"),
    CGIERROR(20004,"cgi報錯"),
    SIZEISZERO(20005,"数据はい空"),
    NAMEISEXISTS(30001,"名称已経存在"),
    NOTDATAS(30002,"没有数据"),
    JANNOTESISTS(30003,"jan不存在"),
    BRANCHNOTESISTS(30004,"該当優先順位表に存在しない店舗情報が含まれています。"),

    FILEVERSIONFAILURE(40001,"文件版本不はい"),
    FILECONTENTFAILURE(40002,"文件内容不はい"),
    DATAISTOOLARGE(40003,"数据量を過ぎて大"),
    CLASSIFY_NOT_EXIST(40004,"classify not exist"),
    VERSION_ERROR(40005,"version error"),
    JANCDINEXISTENCE(50004,"JAN不存在"),

    KAISOU_COUNT_LIMIT(70001,"kaisou count not enough"),

    KAISOU_name_LIMIT(70002, "kaisou name exists"),
    HEIGHT_NOT_ENOUGH(60001,"height not enough"),

    UPDATE_RANK(40015,"変更後Rankで取込してください。");



    private Integer code;
    private String msg;

    ResultEnum(Integer code, String msg) {
            this.code = code;
            this.msg = msg;
        }
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static ResultEnum parse(int code) {
        ResultEnum[] values = values();
        for (ResultEnum value:values) {
            if(value.getCode() == code) {
                return  value;
            }
        }
        throw  new RuntimeException("不明なコード");
    }
}
