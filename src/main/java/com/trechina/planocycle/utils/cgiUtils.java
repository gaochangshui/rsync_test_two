package com.trechina.planocycle.utils;

import com.alibaba.fastjson.JSON;
import org.springframework.http.HttpHeaders;
import com.trechina.planocycle.enums.ResultEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
@Component
public class cgiUtils {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${smartUrlPath}")
    public String smartPath;
    private String textFormat ="application/json;charset=UTF-8";


    /**
     * post調用cgi
     * @param path
     * @param cla
     * @param tokenInfo
     * @param <U>
     * @return
     * @throws IOException
     */
    public <U> String  postCgi(String path, U cla, String tokenInfo) {
        OutputStream os = null;
        BufferedReader reader = null;
        InputStream in = null;
        InputStream buffer = null;
        HttpURLConnection connection =null;
        try{
            URL url =new URL(smartPath+path);
             connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Cookie", "MSPACEDGOURDLP="+tokenInfo);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty(HttpHeaders.CONTENT_TYPE,textFormat);

            os = connection.getOutputStream();
            os.write(JSON.toJSONBytes(cla));
            in =connection.getInputStream();

            buffer =new BufferedInputStream(in);
            reader =new BufferedReader(new InputStreamReader(buffer));
            String read;
            StringBuilder builder = new StringBuilder();
            while ((read = reader.readLine()) !=null){
                builder.append(read);
            }
            return builder.toString();
        }catch (Exception e){
            logger.error("io異常", e);
        }finally {
            this.closeResource(in,buffer,reader,connection);
        }
        return "";
    }
    public <U> String  postCgiCompany(String path, U cla, String tokenInfo) {
        OutputStream os = null;
        BufferedReader reader = null;
        InputStream in = null;
        InputStream buffer = null;
        HttpURLConnection connection =null;
        try{
            URL url =new URL(smartPath+path);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Cookie", "MSPACEDGOURDLP="+tokenInfo);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty(HttpHeaders.CONTENT_TYPE,textFormat);

            os = connection.getOutputStream();
            os.write(JSON.toJSONBytes(cla));
            in =connection.getInputStream();

            buffer =new BufferedInputStream(in);
            reader =new BufferedReader(new InputStreamReader(buffer));
            String read;
            StringBuilder builder = new StringBuilder();
            while ((read = reader.readLine()) !=null){
                builder.append(read+"@");
            }
            return builder.toString();
        }catch (Exception e){
            logger.error("io異常", e);
        }finally {
            this.closeResource(in,buffer,reader,connection);
        }
        return "";
    }
    /**
     * 非同期post呼び出しcgi
     * @param path
     * @param cla
     * @param tokenInfo
     * @param <U>
     * @return
     * @throws IOException
     */
    public <U> String  postCgi(String path, U cla, String tokenInfo,String smartPath) {
        OutputStream os = null;
        BufferedReader reader = null;
        InputStream in = null;
        InputStream buffer = null;
        HttpURLConnection connection =null;

        try{
            URL url =new URL(smartPath+path);
             connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Cookie", "MSPACEDGOURDLP="+tokenInfo);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty(HttpHeaders.CONTENT_TYPE,textFormat);

            os = connection.getOutputStream();
            os.write(JSON.toJSONBytes(cla));
            in =connection.getInputStream();

            buffer =new BufferedInputStream(in);
            reader =new BufferedReader(new InputStreamReader(buffer));
            String read;
            StringBuilder builder = new StringBuilder();
            while ((read = reader.readLine()) !=null){
                builder.append(read);
            }
            return builder.toString();
        }catch (Exception e){
            logger.error("io異常", e);
        }finally {
            this.closeResource(in,buffer,reader,connection);
        }
        return "";
    }
    public String setPath(String key){
        ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
        return resourceBundle.getString(key);

    }

    /**
     * 再帰調用cgi（web版）
     * @param path
     * @param taskid
     * @param tokenInfo
     * @return
     */
    public  Map<String, Object> postCgiOfWeb(String path,String taskid, String tokenInfo){
        InputStream in = null;
        InputStream buffer = null;
        BufferedReader reader = null;
        InputStreamReader inputStreamReader = null;
        HttpURLConnection connection = null;
        try{
            StringBuilder builder = null;
            URL url =new URL(smartPath+path);
             connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Cookie", "MSPACEDGOURDLP="+tokenInfo);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type",textFormat);

            OutputStream os = connection.getOutputStream();
            Map<String,String> para = new HashMap<>();
            para.put("taskid",taskid);
            os.write(JSON.toJSONBytes(para));
            Integer statusCode = connection.getResponseCode();

            in =connection.getInputStream();
            buffer =new BufferedInputStream(in);

            inputStreamReader = new InputStreamReader(buffer);
            reader =new BufferedReader(inputStreamReader);
            String read;
            builder = new StringBuilder();
            while ((read = reader.readLine()) !=null){
                builder.append(read+"@");
            }
            if (builder.length()==0){
                return ResultMaps.result(ResultEnum.SIZEISZERO,"");
            }
            builder = new StringBuilder(builder.substring(0,builder.length()-1));
            logger.info("postCgiOfWeb statusCode,reqult：{},{}",statusCode, builder);
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
        } catch (IOException e) {
            logger.info("cgi調用error：", e);
            return ResultMaps.result(ResultEnum.FAILURE,null);
        } finally {
            this.closeResource(in,buffer,reader,connection);
        }
    }
    /**
     *呼び戻しcgi（非同期呼び出しweb版）
     * @param path
     * @param taskid
     * @param tokenInfo
     * @return
     */
    public  Map<String, Object> postCgiOfWeb(String path,String taskid, String tokenInfo,String smartPath){
        InputStream in = null;
        InputStream buffer = null;
        BufferedReader reader = null;
        InputStreamReader inputStreamReader = null;
        HttpURLConnection connection = null;
        try{
            StringBuilder builder = null;
            URL url =new URL(smartPath+path);
             connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Cookie", "MSPACEDGOURDLP="+tokenInfo);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type",textFormat);

            OutputStream os = connection.getOutputStream();
            Map<String,String> para = new HashMap<>();
            para.put("taskid",taskid);
            os.write(JSON.toJSONBytes(para));
            Integer statusCode = connection.getResponseCode();

            in =connection.getInputStream();
            buffer =new BufferedInputStream(in);

            inputStreamReader = new InputStreamReader(buffer);
            reader =new BufferedReader(inputStreamReader);
            String read;
            builder = new StringBuilder();
            while ((read = reader.readLine()) !=null){
                builder.append(read+"@");
            }
            if (builder.length()==0){
                return ResultMaps.result(ResultEnum.SIZEISZERO,"");
            }
            builder = new StringBuilder(builder.substring(0,builder.length()-1));
            logger.info("postCgiOfWeb statusCode,reqult：{},{}",statusCode, builder);
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
                return ResultMaps.result(ResultEnum.DATAISTOOLARGE,"5");
            }
            else{
                return ResultMaps.result(ResultEnum.SUCCESS,builder.toString());
            }
        } catch (IOException e) {
            logger.info("cgi調用error：", e);
            return ResultMaps.result(ResultEnum.FAILURE,null);
        } finally {
            this.closeResource(in,buffer,reader,connection);
        }
    }
    /**
     * 再帰調用cgi
     *                  通を過ぎて返回的code判断是否成功
     *                  statusCode:200 調用成功，結束循環
     *                  否則根据一下flag告知前端錯誤情况
     *                  2：超時
     *                  3：手動取消
     *                  4：cgi error
     *                  9：数据抽取中，等待30秒再次抽取。
     *
     * @param path
     * @return
     * @throws IOException
     */
    public  Map<String, Object> postCgiLoop(String path,String taskid, String tokenInfo) {
        InputStream in = null;
        InputStream buffer = null;
        BufferedReader reader = null;
        HttpURLConnection connection =null;
        try{
            URL url =new URL(smartPath+path);

            StringBuilder builder = null;
            while (true) {
                Thread.sleep(10*1_000L);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Cookie", "MSPACEDGOURDLP="+tokenInfo);
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestProperty("Content-Type",textFormat);

                OutputStream os = connection.getOutputStream();
                Map<String,String> para = new HashMap<>();
                para.put("taskid",taskid);
                os.write(JSON.toJSONBytes(para));
                int statusCode = connection.getResponseCode();

                in =connection.getInputStream();
                buffer =new BufferedInputStream(in);
                reader =new BufferedReader(new InputStreamReader(buffer));
                String read;
                builder = new StringBuilder();
                while ((read = reader.readLine()) !=null){
                    builder.append(read+"@");
                }
                if (builder.length()==0){
                    return ResultMaps.result(ResultEnum.SIZEISZERO,"");
                }
                builder = new StringBuilder(builder.substring(0,builder.length()-1));
                logger.info("cgiQueryTask statusCode,reqult：{},{}",statusCode,builder);
                if(statusCode == 200) {
                    break;
                }

                if (builder.toString().equals("2")) {
                    return ResultMaps.result(ResultEnum.CGITIEMOUT,null);
                }
                else if (builder.toString().equals("3")) {
                    return ResultMaps.result(ResultEnum.CGICANCEL,null);
                }
                else if (builder.toString().equals("4")) {
                    return ResultMaps.result(ResultEnum.CGIERROR,null);
                }
                else{
                    return ResultMaps.result(ResultEnum.FAILURE,null);
                }
            }
            return ResultMaps.result(ResultEnum.SUCCESS,builder.toString());
        } catch (IOException e) {
            return ResultMaps.result(ResultEnum.FAILURE,null);
        } catch (InterruptedException e){
            Thread.currentThread().interrupt();
            return ResultMaps.result(ResultEnum.FAILURE,null);
        } finally {

        this.closeResource(in,buffer,reader,connection);
        }
    }

    public void closeResource(InputStream in ,InputStream buffer, BufferedReader reader ,HttpURLConnection connection){
        try{
            if(Objects.nonNull(in)){
                in.close();
            }

            if(Objects.nonNull(buffer)){
                buffer.close();
            }

            if(Objects.nonNull(reader)){
                reader.close();
            }
            if (Objects.nonNull(connection)){
                connection.disconnect();
            }
        }catch (Exception e){
            logger.error("io閉じる異常", e);
        }
    }

}
