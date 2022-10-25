package com.trechina.planocycle.entity.po;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ErrorMsg {
    private Integer id;
    private String platform;
    private String sys;
    private LocalDateTime createTime;
    private String browser;
    private String requestUri;
    private String errorMsg;
    private String authorCd;
}
