package com.project.common;


import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;

import com.project.common.security.DigestUtils;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * @author huawei
 * @create 2019-06-09
 **/
public class OkHttpUtil {
    private static final OkHttpClient client = new OkHttpClient.Builder()
                                    .connectTimeout(10, TimeUnit.SECONDS)
                                    .writeTimeout(10, TimeUnit.SECONDS)
                                    .readTimeout(30, TimeUnit.SECONDS)
                                    .build();


    /**
     * get请求
     *
     * @param url
     *            请求地址
     * @param params
     *            请求参数
     * @return 返回结果
     * @throws IOException
     */
    public static Response get(String url, Map<String, String> params)
            throws IOException {
        return createGetCall(url, params).execute();
    }

    /**
     * get请求
     *
     * @param url
     *            请求地址
     * @param params
     *            请求参数
     * @return 返回结果
     * @throws IOException
     */
    public static JSONObject  getJsonObject(String url, Map<String, String> params)
            throws IOException {
        Response response =createGetCall(url, params).execute();

        return  JSONObject.parseObject(response.body().string());
    }

    /**
     * get异步请求
     *
     * @param url
     *            请求地址
     * @param params
     *            请求参数
     * @return 返回结果
     * @throws IOException
     */
    public static void get(String url, Map<String, String> params,
                           Callback callback) {
        createGetCall(url, params).enqueue(callback);
    }

    /**
     * post同步请求
     *
     * @param url
     *            请求地址
     * @param params
     *            请求参数
     * @return 返回结果
     * @throws IOException
     */
    public static Response post(String url, Map<String, String> params)
            throws IOException {
        return createPostCall(url, params).execute();
    }

    /**
     * post同步请求
     *
     * @param url
     *            请求地址
     * @param params
     *            请求参数
     * @return 返回结果
     * @throws IOException
     */
    public static JSONObject postJsonObject(String url, Map<String, String> params)
            throws IOException {
        Response response =createPostCall(url, params).execute();

        return  JSONObject.parseObject(response.body().string());
    }
    /**
     * post同步请求
     *
     * @param url
     *            请求地址
     * @param params
     *            请求参数
     * @return 返回结果
     * @throws IOException
     */
    public static JSONObject postRawJsonObject(String url, Map<String, String> params)
            throws Exception {
        Response response =createRawPostCall(url, params).execute();

        return  JSONObject.parseObject(response.body().string());
    }

    /**
     * post同步请求
     *
     * @param url
     *            请求地址
     *
     *            请求参数
     * @return 返回结果
     * @throws IOException
     */
    public static String postRawJsonObject(String url, byte [] datas)
            throws Exception {
        Response response =createRawPostCall(url, datas).execute();
        return  response.body().string();
    }

    /**
     * post异步请求
     *
     * @param url
     *            请求地址
     * @param params
     *            请求参数
     * @param callback
     *            返回结果
     */
    public static void post(String url, Map<String, String> params,
                            Callback callback) {
        createPostCall(url, params).enqueue(callback);
    }

    /**
     * post同步请求
     *
     * @param url
     *            请求地址
     * @param params
     *            提交参数
     * @param files
     *            提交文件
     * @return 返回结果
     * @throws IOException
     */
    public static Response post(String url, Map<String, String> params,
                                Map<String, File> files) throws IOException {

        return createPostCall(url, params, files).execute();

    }

    /**
     * post异步请求
     *
     * @param url
     *            请求地址
     * @param params
     *            提交参数
     * @param files
     *            提交文件
     * @param callback
     *            返回结果
     */
    public static void post(String url, Map<String, String> params,
                            Map<String, File> files, Callback callback) {
        createPostCall(url, params, files).enqueue(callback);
    }

    private static Call createGetCall(String url, Map<String, String> params) {
        String urlParams = buildUrlParams(params);
        Request request = new Request.Builder().get()
                .url(url + '?' + urlParams).build();
        return client.newCall(request);
    }

    private static String buildUrlParams(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (result.length() > 0) {
                result.append("&");
            }
            result.append(entry.getKey());
            result.append("=");
            result.append(entry.getValue());
        }
        return result.toString();
    }

    private static Call createPostCall(String url, Map<String, String> params,
                                       Map<String, File> files) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        // 上传的参数
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.addFormDataPart(entry.getKey(), entry.getValue());
            }
        }
        // 设置上传的文件
        if (files != null && !files.isEmpty()) {

            for (Map.Entry<String, File> entry : files.entrySet()) {
                File file = entry.getValue();
                String contentType = null;

                boolean isPng = file.getName().endsWith(".png")
                        || file.getName().endsWith(".PNG");

                if (isPng) {
                    contentType = "image/png; charset=UTF-8";
                }

                boolean isJpg = file.getName().endsWith(".jpg")
                        || file.getName().endsWith(".JPG")
                        || file.getName().endsWith(".jpeg")
                        || file.getName().endsWith(".JPEG");
                if (isJpg) {
                    contentType = "image/jpeg; charset=UTF-8";
                }
                if (file != null && file.exists()) {
                    RequestBody body = RequestBody.create(
                            MediaType.parse(contentType), file);
                    builder.addFormDataPart(entry.getKey(), file.getName(),
                            body);
                }
            }
        }
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder().url(url).post(requestBody)
                .build();
        return client.newCall(request);
    }

    private static Call createPostCall(String url, Map<String, String> params) {
        FormBody.Builder builder = new FormBody.Builder(Charset.forName("UTF-8"));
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
        }
        RequestBody requestBody = builder.build();

        Request request = new Request.Builder().url(url).post(requestBody)
                .build();
        return client.newCall(request);
    }

    private static Call createRawPostCall(String url, Map<String, String> params) throws Exception {
        String data = concatParams(params,false);
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, data.getBytes("UTF-8"));
        Request request = new Request.Builder().url(url).post(body).build();
        return client.newCall(request);
    }

    private static Call createRawPostCall(String url,byte[] datas) throws Exception {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, datas);
        Request request = new Request.Builder().url(url).post(body).build();
        return client.newCall(request);
    }

    public static String concatParams(Map<String, String> params,boolean isUrlEncode) throws UnsupportedEncodingException {
        if(params != null){
            StringBuilder builder = new StringBuilder();
            int i= 0;
            for(Map.Entry<String, String>  entry : params.entrySet()){
                if( i != 0) {
                    builder.append("&");
                }
                builder.append(entry.getKey()).append("=").append(isUrlEncode ? URLEncoder.encode(entry.getValue(),"UTF-8"):entry.getValue());
                i++;
            }
            return builder.toString();
        }
        return null;
    }

    public static void main(String[] args) throws Exception {

        Map<String,String> dataMap = Maps.newHashMap();
        String url = "http://sodsoft.vicp.net:50880/OneCSMAPI/API_PG_CX";
        String jsonData =  "{\"APPKEY\":\"6647251\", \"name\":\"张三\",\"age\":\"18\"}";

        String data ="666"+jsonData+"666";

        String sign = DigestUtils.md5(data.getBytes(),false);
        System.out.println(sign);
        dataMap.put("Param",jsonData);
        dataMap.put("Sign",sign);

//        Response response = OkHttpUtil.post(url, dataMap);
//        System.out.println(response.body().string());
        JSONObject jsonObject = OkHttpUtil.postRawJsonObject(url, dataMap);
        System.out.println(jsonObject);
    }

}
