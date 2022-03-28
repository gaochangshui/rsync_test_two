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
        try {
            File file = new File(localFile);
            logger.info("ssh服务器参数：{},{}",localFile,remotePath);
            Connection connection = getConnection();
            boolean auth = connection.authenticateWithPassword(user,pw);
            logger.info("ssh服务器身份验证返回值：{}",auth);
            if (auth) {
                logger.info("验证成功");
                SCPClient client =new SCPClient(connection);
                logger.info("开始put");
                os = client.put(file.getName(),file.length(),remotePath,null);
                logger.info("开始write");
                try(FileInputStream fileInputStream = new FileInputStream(file);){
                    int i;
                    byte[] b =new byte[4096];
                    while((i=fileInputStream.read(b))!=-1){
                        os.write(b,0,i);
                    }
                    os.flush();
                    connection.close();
                    return "传送成功";
                }
            }
            else {
                return "传送失败";
            }
        } catch (IOException e) {
            logger.info("报错:",e);
            return "链接失败";
        }
    }

    private Connection getConnection() throws IOException {
        logger.info("开始ssh服务器");
        Connection connection = new Connection(ip);
        connection.connect();
        logger.info("链接成功");
        return connection;
    }

    /**
     * 下载url方式的文件
     * @return
     */
    public  byte[] downLoafCgi(String path,String tokenInfo) throws IOException {
        //打开URLConnection进行读取
        URL url = new URL(path);
        URLConnection connection = url.openConnection();
        connection.setRequestProperty("Cookie", "MSPACEDGOURDLP="+tokenInfo);
        InputStream in = connection.getInputStream();
        ByteArrayOutputStream os = null;
        byte[] buff = new byte[1024];
        int len = 0;
        os = new ByteArrayOutputStream();
        while ((len = in.read(buff)) != -1) {
            os.write(buff, 0, len);
        }

        in.close();
        return os.toByteArray();
    }

    public String getFile(String remoteFile, String localTargetDirectory, HttpServletResponse response) {
        try{
                Connection connection = getConnection();
                boolean auth = connection.authenticateWithPassword(user,pw);
                logger.info("ssh服务器身份验证返回值：{}",auth);
                if (auth) {
                    logger.info("验证成功");
                    SCPClient client =new SCPClient(connection);
                    is = client.get(remoteFile);
                    File file = new File(localTargetDirectory);
                    if(!file.createNewFile()){
                        return "传送失败";
                    }
                    try(FileOutputStream downFile = new FileOutputStream(file);){
                        OutputStream outputStream = response.getOutputStream();
                        // 构造一个长度为1024的字节数组
                        byte[] buffer = new byte[1024];
                        int len = 0;
                        while ((len = is.read(buffer)) != -1){
                            downFile.write(buffer,0,len);
                            outputStream.write(buffer,0,len);
                            downFile.flush();
                            outputStream.flush();
                        }
                        outputStream.close();
                        is.close();
                        connection.close();
                    }
                    return "传送成功";
                } else {
                    return "传送失败";
                }

            } catch (IOException e) {
            logger.info("报错:",e);
            return "链接失败";
        }
    }

}
