package com.trechina.planocycle.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.trechina.planocycle.entity.dto.ShelfNameDto;
import com.trechina.planocycle.entity.po.ShelfNameArea;
import com.trechina.planocycle.entity.po.ShelfNameMst;
import com.trechina.planocycle.entity.vo.ShelfNameDataVO;
import com.trechina.planocycle.entity.vo.ShelfNameUpdVO;
import com.trechina.planocycle.entity.vo.ShelfNameVO;
import com.trechina.planocycle.entity.vo.ShelfPatternTreeVO;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.ShelfNameMstMapper;
import com.trechina.planocycle.service.ShelfNameAreaService;
import com.trechina.planocycle.service.ShelfNameService;
import com.trechina.planocycle.service.ShelfPatternAreaService;
import com.trechina.planocycle.service.ShelfPatternService;
import com.trechina.planocycle.utils.ListDisparityUtils;
import com.trechina.planocycle.utils.ResultMaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class ShelfNameServiceImpl implements ShelfNameService {
    private final Logger logger= LoggerFactory.getLogger(this.getClass());
    @Autowired
    private HttpSession session;
    @Autowired
    private ShelfNameMstMapper shelfNameMstMapper;
    @Autowired
    private ShelfPatternService shelfPatternService;
    @Autowired
    private ShelfNameAreaService shelfNameAreaService;
    @Autowired
    private ShelfPatternAreaService shelfPatternAreaService;

    /**
     * 棚名情報の取得
     * @param companyCd
     * @return
     */
    @Override
    public Map<String, Object> getShelfNameInfo(String companyCd) {
        logger.info("つかむ取棚名称信息的参数：{}",companyCd);
        List<ShelfNameDataVO> resultInfo = shelfNameMstMapper.selectShelfNameInfo(companyCd);
        logger.info("つかむ取棚名称信息的返回値：{}",resultInfo);
        return ResultMaps.result(ResultEnum.SUCCESS,resultInfo);
    }

    /**
     * ハウス名情報の保存
     * @param shelfNameDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> setShelfName(ShelfNameDto shelfNameDto) {
        logger.info("保存棚名称信息的参数：{}",shelfNameDto);
        // check名同じ企業名を繰り返すことはできません
        Integer resultNum = shelfNameMstMapper.selectDistinctName(shelfNameDto);
        if (resultNum!=null){
            return ResultMaps.result(ResultEnum.NAMEISEXISTS);
        }
        String authorCd = session.getAttribute("aud").toString();
        List<ShelfNameArea> list = new ArrayList<>();
        ShelfNameMst shelfNameMst = new ShelfNameMst();
        shelfNameMst.setShelfName(shelfNameDto.getShelfName());
        shelfNameMst.setConpanyCd(shelfNameDto.getCompanyCd());
        shelfNameMst.setAuthorCd(authorCd);
        logger.info("保存棚名称信息转换后的参数：{}",shelfNameMst);
        Integer resultInfo = shelfNameMstMapper.insert(shelfNameMst);
        //ユーザーIDの取得

        shelfNameDto.getArea().forEach(item -> {
            ShelfNameArea shelfNameArea = new ShelfNameArea();
            shelfNameArea.setCompanyCd(shelfNameDto.getCompanyCd());
            shelfNameArea.setAreacd(item);
            shelfNameArea.setShelfNameCd(shelfNameMst.getId());
            list.add(shelfNameArea);
        });
        logger.info("保存棚名称信息转换后的area参数：{}",list);

        shelfNameAreaService.setShelfNameArea(list,authorCd);
        logger.info("保存棚名称信息保存后返回的信息：{}",resultInfo);
        return ResultMaps.result(ResultEnum.SUCCESS);
    }
    /**
     * 小屋名情報の変更
     * @param shelfNameDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> updateShelfName(ShelfNameDto shelfNameDto) {
        logger.info("修改棚名称信息的参数：{}",shelfNameDto);
        // check名同じ企業名を繰り返すことはできません
        Integer resultNum = shelfNameMstMapper.selectDistinctName(shelfNameDto);
        if (resultNum!=null && !resultNum.equals(shelfNameDto.getId())){
            return ResultMaps.result(ResultEnum.NAMEISEXISTS);
        }
        //ユーザーIDの取得
        String authorCd = session.getAttribute("aud").toString();
        //削除するShelfNameAreaコレクション
        List<ShelfNameArea> delList = new ArrayList<>();
        //追加するShelfNameAreaコレクション
        List<ShelfNameArea> setList = new ArrayList<>();
        ShelfNameMst shelfNameMst = new ShelfNameMst();
        shelfNameMst.setId(shelfNameDto.getId());
        shelfNameMst.setShelfName(shelfNameDto.getShelfName());
        shelfNameMst.setConpanyCd(shelfNameDto.getCompanyCd());
        shelfNameMst.setAuthorCd(authorCd);
        logger.info("修改棚名称信息转换后的参数：{}",shelfNameMst);
        int resultInfo = shelfNameMstMapper.update(shelfNameMst);
        logger.info("修改棚名称信息后返回的信息：{}",resultInfo);
        //shelfName関連Areaの取得
        List<Integer> getShelfNameArea = shelfNameAreaService.getShelfNameArea(shelfNameMst.getId(),shelfNameMst.getConpanyCd());
        logger.info("shelfName关联的所有Area：{}",getShelfNameArea);
        //データベースと重複データの変更
        shelfNameDto.getArea().forEach(item->{
            for (Integer area : getShelfNameArea) {
                if (item.equals(area)){
                    shelfNameAreaService.setDelFlg(item,shelfNameDto.getId(),authorCd);
                }
            }
        });
        //削除するareaコレクション
        List<Integer> deleteAreaList = ListDisparityUtils.getListDisparit(getShelfNameArea, shelfNameDto.getArea());
        //areaの集合を追加するには
        List<Integer> setAreaList = ListDisparityUtils.getListDisparit( shelfNameDto.getArea(),getShelfNameArea);
        if (deleteAreaList.size()>0) {
            deleteAreaList.forEach(item -> {

                ShelfNameArea shelfNameArea = new ShelfNameArea();
                shelfNameArea.setCompanyCd(shelfNameDto.getCompanyCd());
                shelfNameArea.setAreacd(item);
                shelfNameArea.setShelfNameCd(shelfNameMst.getId());
                delList.add(shelfNameArea);
            });
            logger.info("削除棚名称信息转换后的area参数：{}",delList);
            //関連するareaを削除
            shelfNameAreaService.delAreaCd(deleteAreaList,shelfNameDto.getId(),authorCd);
            //検索棚名の下の棚pattern
            List<Integer> shelfPatternList = shelfPatternService.getShelfPattern(shelfNameDto.getCompanyCd(), shelfNameDto.getId());
            //小屋patternの下のareaを削除
            if (shelfPatternList!=null) {
                shelfPatternList.forEach(item -> {
                    shelfPatternAreaService.deleteAreaCd(deleteAreaList, item, authorCd);
                });
            }

        }
        if (setAreaList.size()>0) {
            setAreaList.forEach(item -> {
                ShelfNameArea shelfNameArea = new ShelfNameArea();
                shelfNameArea.setCompanyCd(shelfNameDto.getCompanyCd());
                shelfNameArea.setAreacd(item);
                shelfNameArea.setShelfNameCd(shelfNameMst.getId());
                setList.add(shelfNameArea);
            });
            logger.info("添加棚名称信息转换后的area参数：{}", setList);
            //関連するareaを追加
            Map<String, Object> setAreaInfo = shelfNameAreaService.setShelfNameArea(setList, authorCd);

            logger.info("添加棚名称信息保存后返回的信息：{}",setAreaInfo);
        }


        return ResultMaps.result(ResultEnum.SUCCESS);
    }

    /**
     * 棚名を取得Name
     * @param companyCd
     * @return
     */
    @Override
    public Map<String, Object> getShelfName(String companyCd) {
        logger.info("つかむ取棚名称Name的参数：{}",companyCd);
        List<ShelfNameVO> resultInfo = shelfNameMstMapper.selectShelfName(companyCd);
        logger.info("つかむ取棚名称Name的返回値：{}",resultInfo);
        return ResultMaps.result(ResultEnum.SUCCESS,resultInfo);
    }
    /**
     * 小屋名の削除
     * @param jsonObject
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> delShelfNameInfo(JSONObject jsonObject) {
        logger.info("削除棚名称Name的参数：{}",jsonObject.toString());
        //作成者cdの取得
        String authorCd = session.getAttribute("aud").toString();
        // 小屋名の削除
        Integer id = Integer.valueOf(String.valueOf(((Map) jsonObject.get("param")).get("id")));
        shelfNameMstMapper.deleteShelfNameInfo(id,authorCd);
        // 削除する棚patternIdを検索
        List<Integer> patternId = shelfNameMstMapper.selectPatternCd(id);
        patternId.forEach(item->{
            logger.info("棚patternid{}",patternId);
            Map<String,Integer> patternMap =new HashMap<>();
            Map<String,Object> paraMap =new HashMap<>();
            patternMap.put("id",item);
            paraMap.put("param",patternMap);
            // 関連する棚patternの削除
            shelfPatternService.delShelfPatternInfo((JSONObject) JSONObject.toJSON(paraMap));
            // 関連するareaを削除
        });
        shelfNameAreaService.delShelfNameArea(id,authorCd);

        return ResultMaps.result(ResultEnum.SUCCESS);
    }
    /**
     * 変更用のシングル・テーブル・ハウス名情報の取得
     * @param id
     * @return
     */
    @Override
    public Map<String, Object> getShelfNameInfoById(Integer id) {
        ShelfNameUpdVO shelfNameUpdVO = shelfNameMstMapper.selShelfNameInfoById(id);
        return ResultMaps.result(ResultEnum.SUCCESS,shelfNameUpdVO);
    }

    /**
     * 単一テーブルハウス名情報を取得し、取得ハウス名とハウスpatternのプロパティ構造を変更します。
     *
     * @param companyCd
     * @return
     */
    @Override
    public Map<String, Object> getShelfPatternMaster(String companyCd) {
        List<ShelfPatternTreeVO> shelfPatternTreeVO = shelfNameMstMapper.selectShelfPatternTree(companyCd);
        return ResultMaps.result(ResultEnum.SUCCESS,shelfPatternTreeVO);
    }

}
