package com.trechina.planocycle.service;

import com.trechina.planocycle.entity.vo.PriorityOrderMstVO;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public interface PriorityOrderMstService {

    /**
     * 優先順位テーブルリストの取得
     * @param companyCd
     * @return
     */
    Map<String,Object> getPriorityOrderList(String companyCd);


    /**
     * 登録者がいる企業に優先順位表があるかどうかを調べる
     * @return
     */
    Map<String,Object> getPriorityOrderExistsFlg(String companyCd);





    /**
     * 根据productpowercd検索関連付け的優先順位表cd
     * @param companyCd
     * @param productPowerCd
     * @return
     */
    String selPriorityOrderCdForProdCd(String companyCd,Integer productPowerCd);



    /**
     * 表示自動計算実行ステータス
     * @param taskId
     * @return
     */
    Map<String, Object> autoTaskId(String taskId);


    /**
     * xin重新計算rank排序
     * @param companyCd
     * @param priorityOrderCd

     * @return
     */
    Map<String, Object> getNewReorder(String companyCd, Integer priorityOrderCd, String authorCd);

    /**
     * 新規時清空対応一時表所有信息
     * @param companyCd
     * @return
     */
    void deleteWorkTable(String companyCd,Integer priorityOrderCd);

    Map<String,Object> getFaceKeisanForCgi(String[]array,String companyCd,Integer shelfPatternNo,String authorCd,String tokenInfo);

    /**
     * 最終保存
     * @param primaryKeyVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    Map<String, Object> saveAllWorkPriorityOrder(PriorityOrderMstVO primaryKeyVO);

    /**
     * 編集時にすべての情報を表示
     * @param companyCd
     * @param priorityOrderCd
     * @return
     */
    Map<String,Object> getPriorityOrderAll(String companyCd,Integer priorityOrderCd,Integer isCover) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;

    /**
     * 編集時にptsの名前が存在するかどうかを確認
     * @param priorityOrderMstVO
     * @return
     */
    Map<String, Object> checkOrderName(PriorityOrderMstVO priorityOrderMstVO);

    /**
     * 基本パターン削除
     * @param priorityOrderMstVO
     * @return
     */
    Map<String,Object> deletePriorityOrderAll( PriorityOrderMstVO priorityOrderMstVO);

    /**
     * 各種mst展示
     * @param companyCd
     * @param priorityOrderCd
     * @param flag
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    Map<String,Object> getVariousMst(String companyCd,Integer priorityOrderCd,Integer flag) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException;
}
