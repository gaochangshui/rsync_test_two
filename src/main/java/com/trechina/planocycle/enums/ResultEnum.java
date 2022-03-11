package com.trechina.planocycle.enums;

public enum ResultEnum {
    SUCCESS(101,"成功"),
    FAILURE(202,"失败"),
    TIMEOUT(10001,"登录超时"),
    NOTFOUNTCOOKIE(10002,"找不到cookie信息"),
    CGITIEMOUT(20002,"cgi调用超时"),
    CGICANCEL(20003,"cgi手动取消"),
    CGIERROR(20004,"cgi报错"),
    SIZEISZERO(20005,"数据为空"),
    NAMEISEXISTS(30001,"名称已经存在"),
    NOTDATAS(30002,"没有数据"),
    JANNOTESISTS(30003,"jan不存在"),
    FILEVERSIONFAILURE(40001,"文件版本不对"),
    FILECONTENTFAILURE(40002,"文件内容不对"),
    DATAISTOOLARGE(40003,"数据量过大"),
    JANCDINEXISTENCE(50004,"JAN不存在");

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
