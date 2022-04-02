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
     * 伝送Areas表情報
     * @return -1 失敗  >0成功
     */
    @GetMapping("getAreasTransfer")
    public int AreasTransfer() {
        return tableTransferService.getAreasTransfer();
    }

    /**
     * 伝送Branchs表情報
     * @return -1 失敗  >-1成功
     */
    @GetMapping("getBranchsTransfer")
    public int BranchsTransfer() {
        return tableTransferService.getBranchsTransfer();
    }

    /**
     * 伝送Jans表情報
     * @return -1 失敗  >0成功
     */
    @GetMapping("getJansTransfer")
    public int JansTransfer() {
        return tableTransferService.getJansTransfer();
    }

    /**
     * 伝送Attr表情報
     * @return -1 失敗  >0成功
     */
    @GetMapping("getAttrTransfer")
    public int AttrTransfer() {
        return tableTransferService.getAttrTransfer();
    }
}
