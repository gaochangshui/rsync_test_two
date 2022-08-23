package com.trechina.planocycle.enums;

public enum ResultEnum {
    SUCCESS(101,"成功"),
    SUCCESS_BUT_NEW_JAN(102,"成功"),
    FAILURE(202,"エラー"),
    TIMEOUT(10001,"セッション切りました。"),
    NOTFOUNTCOOKIE(10002,"User情報が見つかりません。"),
    CGITIEMOUT(20002,"SmartDBと接続は切りました。"),
    CGICANCEL(20003,"cgiを手動キャンセルしました。"),
    CGIERROR(20004,"cgiでエラーが発生しました。"),
    SIZEISZERO(20005,"数据はい空"),
    NAMEISEXISTS(30001,"同じ名称がすでに存在しています。"),
    NOTDATAS(30002,"データがありません。"),
    JANNOTESISTS(30003,"Janが存在しません。"),
    BRANCHNOTESISTS(30004,"該当優先順位表に存在しない店舗情報が含まれています。"),
    FILEVERSIONFAILURE(40001,"PTSバージョンが正しくないです。正しいVer2、Ver3にしてください。"),
    FILECONTENTFAILURE(40002,"PTSファイル書式が正しくないです。"),
    DATAISTOOLARGE(40003,"数据量を過ぎて大"),
    CLASSIFY_NOT_EXIST(40004,"classify not exist"),
    VERSION_ERROR(40005,"version error"),
    JANCDINEXISTENCE(50004,"該当商品がありません。"),
    HEIGHT_NOT_ENOUGH(60001,"棚の高さチェックで、入れない商品がありました。修正してから再度自動計算してください。"),
    BRANCH_IS_EXIST(60002,"店舗番号は既に存在しますので、再入力してください"),
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
