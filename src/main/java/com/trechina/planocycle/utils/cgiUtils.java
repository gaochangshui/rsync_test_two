package com.trechina.planocycle.utils;

import com.alibaba.fastjson.JSON;
import com.trechina.planocycle.enums.ResultEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
@Component
public class cgiUtils {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${smartUrlPath}")
    public String smartPath;

    /**
     * get调用cgi
     * @param path
     * @return
     * @throws IOException
     */
    ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
    public  String getCgi(String path,String tokenInfo) throws IOException {
        URL url =new URL(smartPath+path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Cookie", "MSPACEDGOURDLP="+tokenInfo);
        InputStream in =connection.getInputStream();
        InputStream buffer =new BufferedInputStream(in);
        BufferedReader reader =new BufferedReader(new InputStreamReader(buffer));
        String read;
        StringBuilder builder = new StringBuilder();
        while ((read = reader.readLine()) !=null){
            builder.append(read);
        }
        in.close();
        return builder.toString();
    }

    /**
     * post调用cgi
     * @param path
     * @param cla
     * @param tokenInfo
     * @param <U>
     * @return
     * @throws IOException
     */
    public <U> String  postCgi(String path, U cla, String tokenInfo) throws IOException {
        URL url =new URL(smartPath+path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Cookie", "MSPACEDGOURDLP="+tokenInfo);
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty("Content-Type","application/json;charset=UTF-8");

        OutputStream os = connection.getOutputStream();
        os.write(JSON.toJSONBytes(cla));
        InputStream in =connection.getInputStream();
        InputStream buffer =new BufferedInputStream(in);
        BufferedReader reader =new BufferedReader(new InputStreamReader(buffer));
        String read;
        StringBuilder builder = new StringBuilder();
        while ((read = reader.readLine()) !=null){
            builder.append(read);
        }
        in.close();
        return builder.toString();
    }

    public String setPath(String key){
        ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
        return resourceBundle.getString(key);

    }

    /**
     * 递归调用cgi（web版）
     * @param path
     * @param taskid
     * @param tokenInfo
     * @return
     */
    public  Map<String, Object> postCgiOfWeb(String path,String taskid, String tokenInfo){
        try{
            StringBuilder builder = null;
            URL url =new URL(smartPath+path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Cookie", "MSPACEDGOURDLP="+tokenInfo);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type","application/json;charset=UTF-8");

            OutputStream os = connection.getOutputStream();
            Map<String,String> para = new HashMap<>();
            para.put("taskid",taskid);
            os.write(JSON.toJSONBytes(para));
            Integer statusCode = connection.getResponseCode();

            InputStream in =connection.getInputStream();
            InputStream buffer =new BufferedInputStream(in);

            BufferedReader reader =new BufferedReader(new InputStreamReader(buffer));
            String read;
            builder = new StringBuilder();
            while ((read = reader.readLine()) !=null){
                builder.append(read+"@");
            }
            if (builder.length()==0){
                return ResultMaps.result(ResultEnum.SIZEISZERO,"");
            }
            builder = new StringBuilder(builder.substring(0,builder.length()-1));
            logger.info("postCgiOfWeb statusCode,reqult："+statusCode+","+builder.toString());
            in.close();
            if (builder.toString().equals("2")) {
                return ResultMaps.result(ResultEnum.CGITIEMOUT,null);
            }
            else if (builder.toString().equals("3")) {
                return ResultMaps.result(ResultEnum.CGICANCEL,null);
            }
            else if (builder.toString().equals("4")) {
                return ResultMaps.result(ResultEnum.CGIERROR,null);
            }else if (builder.toString().equals("5")){
                return ResultMaps.result(ResultEnum.DATAISTOOLARGE,null);
            }
            else{
                return ResultMaps.result(ResultEnum.SUCCESS,builder.toString());
            }
        } catch (ProtocolException e) {
            logger.info("cgi调用报错："+e);
            return ResultMaps.result(ResultEnum.FAILURE,null);
        } catch (MalformedURLException e) {
            logger.info("cgi调用报错："+e);
            return ResultMaps.result(ResultEnum.FAILURE,null);
        } catch (IOException e) {
            logger.info("cgi调用报错："+e);
            return ResultMaps.result(ResultEnum.FAILURE,null);
        }
    }

    /**
     * 递归调用cgi
     *                  通过返回的状态码判断是否成功
     *                  statusCode:200 调用成功，结束循环
     *                  否则根据一下flag告知前端错误情况
     *                  2：超时
     *                  3：手动取消
     *                  4：cgi报错
     *                  9：数据抽取中，等待30秒再次抽取。
     *
     * @param path
     * @return
     * @throws IOException
     */
    public  Map<String, Object> postCgiLoop(String path,String taskid, String tokenInfo) {
        try{
            URL url =new URL(smartPath+path);
            Boolean result  = true;
            int count =0;
            StringBuilder builder = null;
            while (result) {
                Thread.sleep(10*1_000L);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Cookie", "MSPACEDGOURDLP="+tokenInfo);
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestProperty("Content-Type","application/json;charset=UTF-8");

                OutputStream os = connection.getOutputStream();
                Map<String,String> para = new HashMap<>();
                para.put("taskid",taskid);
                os.write(JSON.toJSONBytes(para));
                Integer statusCode = connection.getResponseCode();

                InputStream in =connection.getInputStream();
                InputStream buffer =new BufferedInputStream(in);

                BufferedReader reader =new BufferedReader(new InputStreamReader(buffer));
                String read;
                builder = new StringBuilder();
                while ((read = reader.readLine()) !=null){
                    builder.append(read+"@");
                }
                builder = new StringBuilder(builder.substring(0,builder.length()-1));
                in.close();
                count+=1;
                logger.info("cgiQueryTask statusCode,reqult："+statusCode+","+builder.toString());
                if(statusCode == 200) {
                    break;
                } else  {
                    if (builder.toString().equals("2")) {
                        return ResultMaps.result(ResultEnum.CGITIEMOUT,null);
                    }
                    else if (builder.toString().equals("3")) {
                        return ResultMaps.result(ResultEnum.CGICANCEL,null);
                    }
                    else if (builder.toString().equals("4")) {
                        return ResultMaps.result(ResultEnum.CGIERROR,null);
                    }
                    else if (builder.toString().equals("9")) {
                        continue;
                    }
                    else{
                        return ResultMaps.result(ResultEnum.FAILURE,null);
                    }
                }
            }
            return ResultMaps.result(ResultEnum.SUCCESS,builder.toString());
        } catch (IOException e) {
            return ResultMaps.result(ResultEnum.FAILURE,null);
        } catch (InterruptedException e){
            Thread.currentThread().interrupt();
            return ResultMaps.result(ResultEnum.FAILURE,null);
        }
    }

}
