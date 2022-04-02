package com.trechina.planocycle.service.impl;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.trechina.planocycle.entity.dto.PriorityAllPtsDataDto;
import com.trechina.planocycle.entity.dto.WorkPriorityOrderResultDataDto;
import com.trechina.planocycle.entity.po.*;
import com.trechina.planocycle.entity.vo.*;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.PriorityAllMstMapper;
import com.trechina.planocycle.mapper.PriorityAllPtsMapper;
import com.trechina.planocycle.mapper.ShelfPtsDataMapper;
import com.trechina.planocycle.mapper.ShelfPtsDataVersionMapper;
import com.trechina.planocycle.service.CommonMstService;
import com.trechina.planocycle.service.PriorityAllPtsService;
import com.trechina.planocycle.utils.ResultMaps;
import de.siegmar.fastcsv.writer.CsvWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class PriorityAllPtsServiceImpl implements PriorityAllPtsService {
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
    @Autowired
    private HttpSession session;
    private final Logger logger = LoggerFactory.getLogger(PriorityAllPtsServiceImpl.class);

    @Override
    public void saveWorkPtsData(String companyCd, String authorCd, Integer priorityAllCd, Integer patternCd) {
        //採用された商品をすべて検索し、棚順に並べ替え、棚上の商品の位置をマークする
        List<WorkPriorityOrderResultDataDto> workPriorityOrderResultData = priorityAllMstMapper.selectByAuthorCd(companyCd, authorCd, priorityAllCd, patternCd);
        List<WorkPriorityOrderResultDataDto> positionResultData = commonMstService.calculateTanaPosition(workPriorityOrderResultData);

        //既存の新しいptsを検出し,削除してから保存する
        //新しいptsにデータがあるptsCd
        ShelfPtsData shelfPtsData = priorityAllPtsMapper.selectWorkPtsCdByAuthorCd(companyCd, authorCd, priorityAllCd, patternCd);
        //テンポラリ・テーブルのptscd
        Integer ptsCd = shelfPtsDataMapper.getPtsCd(patternCd);

        PriorityAllPtsDataDto priorityOrderPtsDataDto = new PriorityAllPtsDataDto();
        priorityOrderPtsDataDto.setPriorityAllCd(priorityAllCd);
        priorityOrderPtsDataDto.setOldPtsCd(ptsCd);
        priorityOrderPtsDataDto.setCompanyCd(companyCd);
        priorityOrderPtsDataDto.setAuthorCd(authorCd);

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
        //modeNameはptsをダウンロードするファイル名として
        priorityOrderPtsDataDto.setFileName(modeName+"_"+patternCd+"_new.csv");
        //既存のptsからデータをクエリーする
        priorityAllPtsMapper.insertPtsData(priorityOrderPtsDataDto);
        Integer id = priorityOrderPtsDataDto.getId();
        priorityAllPtsMapper.insertPtsTaimst(ptsCd, id, authorCd, priorityAllCd, patternCd);
        priorityAllPtsMapper.insertPtsTanamst(ptsCd, id, authorCd, priorityAllCd, patternCd);
        priorityAllPtsMapper.insertPtsVersion(ptsCd, id, authorCd, priorityAllCd, patternCd);

        if (!positionResultData.isEmpty()) {
            priorityAllPtsMapper.insertPtsDataJandata(positionResultData, id, companyCd, authorCd, priorityAllCd, patternCd);
        }
    }

    @Override
    public Map<String, Object> getPtsDetailData(Integer patternCd, String companyCd, Integer priorityAllCd) {
        String authorCd = session.getAttribute("aud").toString();
        PtsDetailDataVo ptsDetailData = priorityAllPtsMapper.getPtsDetailData(companyCd, authorCd, priorityAllCd, patternCd);

        if(ptsDetailData != null){
            Integer id = ptsDetailData.getId();

            List<PtsTaiVo> taiData = priorityAllPtsMapper.getTaiData(id);
            List<PtsTanaVo> tanaData = priorityAllPtsMapper.getTanaData(id);
            List<PtsJanDataVo> janData = priorityAllPtsMapper.getJanData(id);

            ptsDetailData.setPtsTaiList(taiData);
            ptsDetailData.setPtsTanaVoList(tanaData);
            ptsDetailData.setPtsJanDataList(janData);
        }

        return ResultMaps.result(ResultEnum.SUCCESS,ptsDetailData);
    }

    @Override
    public void batchDownloadPtsData(PriorityAllVO priorityAllVO, HttpServletResponse response) throws IOException {
        String authorCd = session.getAttribute("aud").toString();
        Integer priorityAllCd = priorityAllVO.getPriorityAllCd();
        String companyCd = priorityAllVO.getCompanyCd();
        String zipFileName = null;

        List<ShelfPtsData> shelfPtsDataList = priorityAllPtsMapper.selectByPriorityAllCd(companyCd, authorCd, priorityAllCd);
        long currentTimeMillis = System.currentTimeMillis();

        String path = this.getClass().getClassLoader().getResource("").getPath();
        logger.info("parent path: {}", path);

        String fileParentPath = Joiner.on(File.separator).join(Lists.newArrayList(path, currentTimeMillis));
        File file = new File(fileParentPath);
        if (!file.exists()) {
            boolean isMkdir = file.mkdirs();
            logger.info("mkdir:{}",isMkdir);
        }

        zipFileName = MessageFormat.format("全パターン{0}.zip", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        String format = MessageFormat.format("attachment;filename={0};",  UriUtils.encode(zipFileName, "utf-8"));
        response.setHeader("Content-Disposition", format);
        response.setHeader(HttpHeaders.CONTENT_TYPE, "application/zip");

        ServletOutputStream outputStream = response.getOutputStream();

        try(ZipOutputStream zos = new ZipOutputStream(outputStream)) {
            for (ShelfPtsData ptsData : shelfPtsDataList) {
                Integer ptsCd = ptsData.getId();
                String fileName = ptsData.getFileName();

                ShelfPtsDataVersion shelfPtsDataVersion = priorityAllPtsMapper.selectAllVersionByPtsCd(companyCd, ptsCd);
                List<ShelfPtsDataTaimst> shelfPtsDataTaimst = priorityAllPtsMapper.selectAllTaimstByPtsCd(companyCd, ptsCd);
                List<ShelfPtsDataTanamst> shelfPtsDataTanamst = priorityAllPtsMapper.selectAllTanamstByPtsCd(companyCd, ptsCd);
                List<ShelfPtsDataJandata> shelfPtsDataJandata = priorityAllPtsMapper.selectAllJandataByPtsCd(companyCd, ptsCd);

                String filePath = this.generateCsv2File(shelfPtsDataVersion, shelfPtsDataTaimst, shelfPtsDataTanamst, shelfPtsDataJandata, fileParentPath, fileName);
                this.writeZip(filePath, zos, fileName);
            }

            zos.flush();
        } catch (IOException e) {
            logger.error("", e);
        } finally {
            outputStream.close();
            this.deleteDir(new File(fileParentPath));
        }
    }

    private void writeZip(String filePath, ZipOutputStream zos, String fileName){
        try(FileInputStream fis = new FileInputStream(filePath)) {
            zos.putNextEntry(new ZipEntry(fileName));
            byte[] bom = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
            zos.write(bom);

            byte[] bytes = new byte[1024];
            int len;
            while ((len = fis.read(bytes))!=-1){
                zos.write(bytes, 0, len);
            }
            zos.closeEntry();
        } catch (IOException e) {
            logger.error("写zip文件失敗", e);
        }
    }

    private void deleteDir(File dir) {
        try {
            if (dir.isDirectory()) {
                String[] children = dir.list();
                //ディレクトリ内のサブディレクトリを再帰的に削除
                for (int i=0; i<children.length; i++) {
                    Files.deleteIfExists(new File(dir, children[i]).getAbsoluteFile().toPath());
                }
            }
            // ディレクトリが空です，削除可能
            Files.deleteIfExists(dir.getAbsoluteFile().toPath());
        } catch (IOException e) {
            logger.error("", e);
        }
    }

    /**
     * ptsをファイルに生成
     * @param shelfPtsDataVersion
     * @param shelfPtsDataTaimst
     * @param shelfPtsDataTanamst
     * @param shelfPtsDataJandata
     * @return csvファイルのパスを返す
     */
    public String generateCsv2File(ShelfPtsDataVersion shelfPtsDataVersion, List<ShelfPtsDataTaimst> shelfPtsDataTaimst,
                            List<ShelfPtsDataTanamst> shelfPtsDataTanamst, List<ShelfPtsDataJandata> shelfPtsDataJandata,
                                   String fileParentPath, String fileName) {
        String filePath = Joiner.on(File.separator).join(Lists.newArrayList(fileParentPath, fileName));
        logger.info("file path: {}", fileParentPath);

        try(FileWriter fileWriter = new FileWriter(filePath);
            CsvWriter csvWriter = CsvWriter.builder().build(fileWriter)){
            csvWriter.writeRow(Lists.newArrayList(shelfPtsDataVersion.getCommoninfo(),
                    shelfPtsDataVersion.getVersioninfo(), shelfPtsDataVersion.getOutflg()));
            csvWriter.writeRow(shelfPtsDataVersion.getModename());
            csvWriter.writeRow(shelfPtsDataVersion.getTaiHeader().split(","));

            for (ShelfPtsDataTaimst ptsDataTaimst : shelfPtsDataTaimst) {
                csvWriter.writeRow(Lists.newArrayList(ptsDataTaimst.getTaiCd()+"",
                        ptsDataTaimst.getTaiHeight()+"", ptsDataTaimst.getTaiWidth()+"", ptsDataTaimst.getTaiDepth()+"",
                        Optional.ofNullable(ptsDataTaimst.getTaiName()).orElse("")));
            }

            csvWriter.writeRow(shelfPtsDataVersion.getTanaHeader().split(","));
            for (ShelfPtsDataTanamst ptsDataTanamst : shelfPtsDataTanamst) {
                csvWriter.writeRow(Lists.newArrayList(ptsDataTanamst.getTaiCd()+"",
                        ptsDataTanamst.getTanaCd()+"", ptsDataTanamst.getTanaHeight()+"", ptsDataTanamst.getTanaWidth()+"",
                        ptsDataTanamst.getTanaDepth()+"", ptsDataTanamst.getTanaThickness()+"", ptsDataTanamst.getTanaType()+""));
            }

            String[] janHeaders = shelfPtsDataVersion.getJanHeader().split(",");
            csvWriter.writeRow(janHeaders);
            for (ShelfPtsDataJandata ptsDataJandata : shelfPtsDataJandata) {
                List<String> janData = Lists.newArrayList(ptsDataJandata.getTaiCd() + "",
                        ptsDataJandata.getTanaCd() + "", ptsDataJandata.getTanapositionCd() + "", ptsDataJandata.getJan() + "",
                        ptsDataJandata.getFaceCount() + "", ptsDataJandata.getFaceMen() + "", ptsDataJandata.getFaceKaiten() + "",
                        ptsDataJandata.getTumiagesu() + "",
                        Optional.ofNullable(ptsDataJandata.getZaikosu()).orElse(0) + "", Optional.ofNullable(ptsDataJandata.getFaceDisplayflg()).orElse(0) + "",
                        Optional.ofNullable(ptsDataJandata.getFacePosition()).orElse(0) + "", Optional.ofNullable(ptsDataJandata.getDepthDisplayNum()).orElse(0) + "");
                csvWriter.writeRow(janData.subList(0, janHeaders.length));
            }

        }catch (IOException e) {
            logger.error("csv writer 閉じる异常", e);
        }

        return filePath;
    }
}
