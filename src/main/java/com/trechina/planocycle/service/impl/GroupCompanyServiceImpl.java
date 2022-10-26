package com.trechina.planocycle.service.impl;

import com.google.common.base.Strings;
import com.trechina.planocycle.entity.vo.GroupCompanyVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.GroupCompanyMapper;
import com.trechina.planocycle.service.GroupCompanyService;
import com.trechina.planocycle.utils.ResultMaps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class GroupCompanyServiceImpl implements GroupCompanyService {
    @Autowired
    private GroupCompanyMapper groupCompanyMapper;
    @Autowired
    private HttpSession session;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> saveGroupCompany(GroupCompanyVO groupCompanyVO) {
        String groupCd = groupCompanyVO.getGroupCd();
        if(!Strings.isNullOrEmpty(groupCompanyVO.getGroupCd())){
            groupCompanyMapper.deleteCompany(groupCompanyVO.getGroupCd());
        }else{
            //create group_cd
            String id = groupCompanyMapper.selectGroupCdNumber();
            groupCd = "gp" + id;
        }

        if(groupCompanyVO.getCompanyCds()!=null && !groupCompanyVO.getCompanyCds().isEmpty()){
            List<Map<String, String>> companyMapList = groupCompanyMapper.selectCompanyName(String.join(",", groupCompanyVO.getCompanyCds()));
            String aud = session.getAttribute("aud").toString();
            LocalDate now = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            groupCompanyMapper.insertCompany(groupCd, groupCompanyVO.getGroupName(), companyMapList, aud, formatter.format(now));
        }

        return ResultMaps.result(ResultEnum.SUCCESS);
    }
}
