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
     * 传输Areas表信息
     * @return -1 失败  >0成功
     */
    @GetMapping("getAreasTransfer")
    public int AreasTransfer() {
        return tableTransferService.getAreasTransfer();
    }

    /**
     * 传输Branchs表信息
     * @return -1 失败  >-1成功
     */
    @GetMapping("getBranchsTransfer")
    public int BranchsTransfer() {
        return tableTransferService.getBranchsTransfer();
    }

    /**
     * 传输Jans表信息
     * @return -1 失败  >0成功
     */
    @GetMapping("getJansTransfer")
    public int JansTransfer() {
        return tableTransferService.getJansTransfer();
    }

    /**
     * 传输Attr表信息
     * @return -1 失败  >0成功
     */
    @GetMapping("getAttrTransfer")
    public int AttrTransfer() {
        return tableTransferService.getAttrTransfer();
    }
}
