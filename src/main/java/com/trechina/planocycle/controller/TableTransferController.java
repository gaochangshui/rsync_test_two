package com.trechina.planocycle.controller;

import com.trechina.planocycle.service.TableTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/planoCycle/TableTransfer")
public class TableTransferController {
    @Autowired
    private TableTransferService tableTransferService;
    /**
     * Areasテーブル情報の転送
     * @return -1 失敗  >0成功
     */
    @GetMapping("getAreasTransfer")
    public int areasTransfer() {
        return tableTransferService.getAreasTransfer();
    }

    /**
     * Branchsテーブル情報の転送
     * @return -1 失敗  >-1成功
     */
    @GetMapping("getBranchsTransfer")
    public int branchsTransfer() {
        return tableTransferService.getBranchsTransfer();
    }

    /**
     * Jansテーブル情報の転送
     * @return -1 失敗  >0成功
     */
    @GetMapping("getJansTransfer")
    public int jansTransfer() {
        return tableTransferService.getJansTransfer();
    }

    /**
     * Attrテーブル情報の転送
     * @return -1 失敗  >0成功
     */
    @GetMapping("getAttrTransfer")
    public int attrTransfer() {
        return tableTransferService.getAttrTransfer();
    }

    @GetMapping("janInfoTransfer")
    public int janInfoTransfer() {
        return tableTransferService.getJanInfoTransfer();
    }
}
