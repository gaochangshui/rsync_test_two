package com.trechina.planocycle.service.Impl;

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
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
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
        //查询出所有采纳了的商品，按照台棚进行排序，标记商品在棚上的位置
        List<WorkPriorityOrderResultDataDto> workPriorityOrderResultData = priorityAllMstMapper.selectByAuthorCd(companyCd, authorCd, priorityAllCd, patternCd);
        List<WorkPriorityOrderResultDataDto> positionResultData = commonMstService.calculateTanaPosition(workPriorityOrderResultData);

        //查出已有的新pts，先删掉再保存
        //新pts中已有数据的ptsCd
        ShelfPtsData shelfPtsData = priorityAllPtsMapper.selectWorkPtsCdByAuthorCd(companyCd, authorCd, priorityAllCd, patternCd);
        //临时表中的ptscd
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
        //modeName作为下载pts的文件名
        priorityOrderPtsDataDto.setFileName(modeName+"_"+patternCd+"_new.csv");
        //从已有的pts中查询出数据
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

        ServletOutputStream outputStream = response.getOutputStream();
        try(ZipOutputStream zos = new ZipOutputStream(outputStream);
            WritableByteChannel writableByteChannel = Channels.newChannel(zos)) {
            zipFileName = MessageFormat.format("全パターン{0}.zip", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));

            String format = MessageFormat.format("attachment;filename={0};",  UriUtils.encode(zipFileName, "utf-8"));
            response.setHeader("Content-Disposition", format);
            response.setHeader(HttpHeaders.CONTENT_TYPE, "application/zip");

            for (ShelfPtsData ptsData : shelfPtsDataList) {
                Integer ptsCd = ptsData.getId();
                String fileName = ptsData.getFileName();

                ShelfPtsDataVersion shelfPtsDataVersion = priorityAllPtsMapper.selectAllVersionByPtsCd(companyCd, ptsCd);
                List<ShelfPtsDataTaimst> shelfPtsDataTaimst = priorityAllPtsMapper.selectAllTaimstByPtsCd(companyCd, ptsCd);
                List<ShelfPtsDataTanamst> shelfPtsDataTanamst = priorityAllPtsMapper.selectAllTanamstByPtsCd(companyCd, ptsCd);
                List<ShelfPtsDataJandata> shelfPtsDataJandata = priorityAllPtsMapper.selectAllJandataByPtsCd(companyCd, ptsCd);

                String filePath = this.generateCsv2File(shelfPtsDataVersion, shelfPtsDataTaimst, shelfPtsDataTanamst, shelfPtsDataJandata, fileParentPath, fileName);
                zos.putNextEntry(new ZipEntry(fileName));

                try(FileInputStream fis = new FileInputStream(filePath);
                    ReadableByteChannel readableByteChannel = Channels.newChannel(fis)){
                    byte[] bom = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
                    writableByteChannel.write(ByteBuffer.wrap(bom));

                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    while (readableByteChannel.read(byteBuffer)!=-1){
                        byteBuffer.clear();
                        writableByteChannel.write(byteBuffer);
                        byteBuffer.flip();
                    }
                }

                zos.closeEntry();
            }

            zos.flush();
        } catch (IOException e) {
            logger.error("", e);
        } finally {
            outputStream.close();

            this.deleteDir(new File(fileParentPath));
        }
    }

    private void deleteDir(File dir) {
        try {
            if (dir.isDirectory()) {
                String[] children = dir.list();
                //递归删除目录中的子目录下
                for (int i=0; i<children.length; i++) {
                    Files.deleteIfExists(new File(dir, children[i]).getAbsoluteFile().toPath());
                }
            }
            // 目录此时为空，可以删除
            Files.deleteIfExists(dir.getAbsoluteFile().toPath());
        } catch (IOException e) {
            logger.error("", e);
        }
    }

    /**
     * 生成pts到文件
     * @param shelfPtsDataVersion
     * @param shelfPtsDataTaimst
     * @param shelfPtsDataTanamst
     * @param shelfPtsDataJandata
     * @return 返回csv文件的路径
     */
    public String generateCsv2File(ShelfPtsDataVersion shelfPtsDataVersion, List<ShelfPtsDataTaimst> shelfPtsDataTaimst,
                            List<ShelfPtsDataTanamst> shelfPtsDataTanamst, List<ShelfPtsDataJandata> shelfPtsDataJandata,
                                   String fileParentPath, String fileName) throws IOException {
        String filePath = Joiner.on(File.separator).join(Lists.newArrayList(fileParentPath, fileName));
        logger.info("file path: {}", fileParentPath);

        CsvWriter csvWriter = CsvWriter.builder()
                .build(new FileWriter(filePath));
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
        for (ShelfPtsDataJandata ptsDataJandatum : shelfPtsDataJandata) {
            List<String> janData = Lists.newArrayList(ptsDataJandatum.getTaiCd() + "",
                    ptsDataJandatum.getTanaCd() + "", ptsDataJandatum.getTanapositionCd() + "", ptsDataJandatum.getJan() + "",
                    ptsDataJandatum.getFaceCount() + "", ptsDataJandatum.getFaceMen() + "", ptsDataJandatum.getFaceKaiten() + "",
                    ptsDataJandatum.getTumiagesu() + "",
                    Optional.ofNullable(ptsDataJandatum.getZaikosu()).orElse(0) + "", Optional.ofNullable(ptsDataJandatum.getFaceDisplayflg()).orElse(0) + "",
                    Optional.ofNullable(ptsDataJandatum.getFacePosition()).orElse(0) + "", Optional.ofNullable(ptsDataJandatum.getDepthDisplayNum()).orElse(0) + "");
            csvWriter.writeRow(janData.subList(0, janHeaders.length));
        }

        try {
            csvWriter.close();
        } catch (IOException e) {
            logger.error("csv writer 关闭异常",e);
        }

        return filePath;
    }
}
