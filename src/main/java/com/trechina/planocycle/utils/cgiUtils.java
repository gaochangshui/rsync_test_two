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
import java.util.Objects;
import java.util.ResourceBundle;
@Component
public class cgiUtils {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${smartUrlPath}")
    public String smartPath;

    /**
     * get調用cgi
     * @param path
     * @return
     * @throws IOException
     */
    ResourceBundle resourceBundle = ResourceBundle.getBundle("pathConfig");
    public  String getCgi(String path,String tokenInfo) {
        InputStream in = null;
        InputStream buffer = null;
        BufferedReader reader = null;

        try {
            URL url =new URL(smartPath+path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Cookie", "MSPACEDGOURDLP="+tokenInfo);
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
            try {
                if(Objects.nonNull(in)){
                    in.close();
                }
                if(Objects.nonNull(buffer)){
                    buffer.close();
                }
                if(Objects.nonNull(reader)){
                    reader.close();
                }
            }catch (Exception e){
                logger.error("io閉じる異常", e);
            }
        }

        return "";
    }

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

        try{
            URL url =new URL(smartPath+path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Cookie", "MSPACEDGOURDLP="+tokenInfo);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type","application/json;charset=UTF-8");

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
            try {
                if(Objects.nonNull(in)){
                    in.close();
                }

                if(Objects.nonNull(buffer)){
                    buffer.close();
                }

                if(Objects.nonNull(reader)){
                    reader.close();
                }

                if(Objects.nonNull(os)){
                    os.close();
                }
            } catch (IOException e) {
                logger.error("io閉じる異常", e);
            }
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
        } catch (IOException e) {
            logger.info("cgi調用error：", e);
            return ResultMaps.result(ResultEnum.FAILURE,null);
        } finally {
            try {
                if(Objects.nonNull(in)){
                    in.close();
                }

                if(Objects.nonNull(buffer)){
                    buffer.close();
                }

                if(Objects.nonNull(inputStreamReader)){
                    inputStreamReader.close();
                }

                if(Objects.nonNull(reader)){
                    reader.close();
                }
            } catch (IOException e) {
                logger.info("io閉じる異常：",e);
            }
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
                count+=1;
                logger.info("cgiQueryTask statusCode,reqult：{},{}",statusCode,builder);
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
        } finally {
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
            }catch (Exception e){
                logger.error("io閉じる異常", e);
            }

        }
    }

}
