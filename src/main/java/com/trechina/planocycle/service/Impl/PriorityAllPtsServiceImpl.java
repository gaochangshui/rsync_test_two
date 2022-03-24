package com.trechina.planocycle.service.Impl;

import com.trechina.planocycle.entity.dto.PriorityAllPtsDataDto;
import com.trechina.planocycle.entity.dto.PriorityOrderPtsDataDto;
import com.trechina.planocycle.entity.dto.WorkPriorityOrderResultDataDto;
import com.trechina.planocycle.entity.po.PriorityOrderMst;
import com.trechina.planocycle.entity.po.ShelfPtsData;
import com.trechina.planocycle.entity.po.ShelfPtsDataVersion;
import com.trechina.planocycle.entity.po.WorkPriorityOrderMst;
import com.trechina.planocycle.mapper.PriorityAllMstMapper;
import com.trechina.planocycle.mapper.PriorityAllPtsMapper;
import com.trechina.planocycle.mapper.ShelfPtsDataMapper;
import com.trechina.planocycle.mapper.ShelfPtsDataVersionMapper;
import com.trechina.planocycle.service.CommonMstService;
import com.trechina.planocycle.service.IPriorityAllPtsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PriorityAllPtsServiceImpl implements IPriorityAllPtsService {
    @Autowired
    private CommonMstService commonMstService;
    @Autowired
    private PriorityAllPtsMapper priorityAllPtsMapper;
    @Autowired
    private ShelfPtsDataMapper shelfPtsDataMapper;
    @Autowired
    private PriorityAllMstMapper priorityAllMstMapper;
    @Autowired
    private ShelfPtsDataVersionMapper shelfPtsDataVersionMapper;

    @Override
    public void saveWorkPtsData(String companyCd, String authorCd, Integer priorityAllCd, Integer patternCd) {
        //查询出所有采纳了的商品，按照台棚进行排序，标记商品在棚上的位置
        List<WorkPriorityOrderResultDataDto> workPriorityOrderResultData = priorityAllMstMapper.selectByAuthorCd(companyCd, authorCd, priorityAllCd, patternCd);
        List<WorkPriorityOrderResultDataDto> positionResultData = commonMstService.calculateTanaPosition(workPriorityOrderResultData);

        //查出已有的新pts，先删掉再保存
        //新pts中已有数据的ptsCd
        ShelfPtsData shelfPtsData = priorityAllPtsMapper.selectWorkPtsCdByAuthorCd(companyCd, authorCd, priorityAllCd, patternCd);
        //临时表中的ptscd
        Integer ptsCd = shelfPtsDataMapper.getPtsCd(patternCd);

        PriorityAllPtsDataDto priorityOrderPtsDataDto = PriorityAllPtsDataDto.PriorityAllPtsDataDtoBuilder.aPriorityAllPtsDataDto()
                .withPriorityAllCd(priorityAllCd)
                .withOldPtsCd(ptsCd)
                .withCompanyCd(companyCd)
                .withAuthorCd(authorCd).build();

        if(Optional.ofNullable(shelfPtsData).isPresent()){
            Integer oldPtsCd = shelfPtsData.getId();
            priorityAllPtsMapper.deletePtsData(oldPtsCd);
            priorityAllPtsMapper.deletePtsTaimst(oldPtsCd);
            priorityAllPtsMapper.deletePtsTanamst(oldPtsCd);
            priorityAllPtsMapper.deletePtsVersion(oldPtsCd);
            priorityAllPtsMapper.deletePtsDataJandata(oldPtsCd);
        }

        ShelfPtsDataVersion shelfPtsDataVersion = shelfPtsDataVersionMapper.selectByPrimaryKey(companyCd, ptsCd);
        String modeName = shelfPtsDataVersion.getModename();
        //modeName作为下载pts的文件名
        priorityOrderPtsDataDto.setFileName(modeName+"_new.csv");
        //从已有的pts中查询出数据
        priorityAllPtsMapper.insertPtsData(priorityOrderPtsDataDto);
        Integer id = priorityOrderPtsDataDto.getId();
        priorityAllPtsMapper.insertPtsTaimst(ptsCd, id, authorCd);
        priorityAllPtsMapper.insertPtsTanamst(ptsCd, id, authorCd);
        priorityAllPtsMapper.insertPtsVersion(ptsCd, id, authorCd);

        if (!positionResultData.isEmpty()) {
            priorityAllPtsMapper.insertPtsDataJandata(positionResultData, id, companyCd, authorCd);
        }
    }
}
