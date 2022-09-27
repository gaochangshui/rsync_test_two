package com.trechina.planocycle.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;

public class sshFtpUtils {
    Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * 下載url方式的文件
     * @return
     */
    public  byte[] downLoafCgi(String path,String tokenInfo) {
        //打開URLConnection進行読み取り
        InputStream in = null;
        ByteArrayOutputStream os = null;

        try{
            URL url = new URL(path);
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("Cookie", "MSPACEDGOURDLP="+tokenInfo);
            in = connection.getInputStream();
            byte[] buff = new byte[1024];
            int len = 0;
            os = new ByteArrayOutputStream();
            while ((len = in.read(buff)) != -1) {
                os.write(buff, 0, len);
            }

            return os.toByteArray();
        }catch (Exception e){
            logger.error("",e);
        } finally {
            try {
                if(Objects.nonNull(in)){
                    in.close();
                }

                if(Objects.nonNull(os)){
                    os.close();
                }
            }catch (Exception e){
                logger.error("io閉じる異常", e);
            }
        }

        return null;
    }



}
