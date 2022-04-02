package com.trechina.planocycle.enums;

public enum ResultEnum {
    SUCCESS(101,"成功"),
    FAILURE(202,"失敗"),
    TIMEOUT(10001,"login timeout"),
    NOTFOUNTCOOKIE(10002,"cookie not found"),
    CGITIEMOUT(20002,"cgi timeout"),
    CGICANCEL(20003,"cgi手動cancel"),
    CGIERROR(20004,"cgi error"),
    SIZEISZERO(20005,"data is empty"),
    NAMEISEXISTS(30001,"名前は既に存在します"),
    NOTDATAS(30002,"no data"),
    JANNOTESISTS(30003,"jan does not exist"),
    FILEVERSIONFAILURE(40001,"wrong file version"),
    FILECONTENTFAILURE(40002,"incorrect file content"),
    DATAISTOOLARGE(40003,"too much data"),
    JANCDINEXISTENCE(50004,"jan does not exist");

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
