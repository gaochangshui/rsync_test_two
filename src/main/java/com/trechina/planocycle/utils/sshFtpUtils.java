package com.trechina.planocycle.utils;


import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.SCPInputStream;
import ch.ethz.ssh2.SCPOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import java.util.ResourceBundle;

public class sshFtpUtils {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    private ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
    private String ip = resourceBundle.getString("ServiceIP");
    private String user = resourceBundle.getString("ServiceUser");
    private String pw = resourceBundle.getString("ServicePW");
    private SCPOutputStream os = null;
    private SCPInputStream is = null;

    public String pushFile(String localFile,String remotePath){
        File file = new File(localFile);
        try(FileInputStream fileInputStream = new FileInputStream(file);) {
            logger.info("ssh server参数：{},{}",localFile,remotePath);
            Connection connection = getConnection();
            boolean auth = connection.authenticateWithPassword(user,pw);
            logger.info("ssh server認証返回値：{}",auth);
            if (auth) {
                logger.info("検証成功");
                SCPClient client =new SCPClient(connection);
                logger.info("開始put");
                os = client.put(file.getName(),file.length(),remotePath,null);
                logger.info("開始write");
                int i;
                byte[] b =new byte[4096];
                while((i=fileInputStream.read(b))!=-1){
                    os.write(b,0,i);
                }
                os.flush();
                connection.close();
                return "転送成功";
            }
            else {
                return "転送失敗";
            }
        } catch (IOException e) {
            logger.info("error:",e);
            return "connect失敗";
        }
    }

    private Connection getConnection() throws IOException {
        logger.info("開始ssh server");
        Connection connection = new Connection(ip);
        connection.connect();
        logger.info("connect成功");
        return connection;
    }

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

    public String getFile(String remoteFile, String localTargetDirectory, HttpServletResponse response) {
        File file = new File(localTargetDirectory);
        OutputStream outputStream = null;

        try(FileOutputStream downFile = new FileOutputStream(file)){
                outputStream = response.getOutputStream();
                Connection connection = getConnection();
                boolean auth = connection.authenticateWithPassword(user,pw);
                logger.info("ssh server身分検証返回値：{}",auth);
                if (auth) {
                    logger.info("検証成功");
                    SCPClient client =new SCPClient(connection);
                    is = client.get(remoteFile);
                    if(!file.createNewFile()){
                        return "転送失敗";
                    }
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = is.read(buffer)) != -1){
                        downFile.write(buffer,0,len);
                        outputStream.write(buffer,0,len);
                        downFile.flush();
                        outputStream.flush();
                    }
                    connection.close();
                    return "転送成功";
                } else {
                    return "転送失敗";
                }

            } catch (IOException e) {
            logger.info("error:",e);
            return "connect失敗";
        } finally {
            try {
                if(Objects.nonNull(outputStream)){
                    outputStream.close();
                }
                if(Objects.nonNull(is)){
                    is.close();
                }
            } catch (IOException e) {
                logger.error("error",e);
            }
        }
    }

}
