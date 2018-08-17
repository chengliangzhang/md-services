package com.maoding.coreUtils;

import com.maoding.coreFileServer.web.CoreKeyValuePair;
import com.maoding.coreBean.CoreResponse;
import com.maoding.coreFileServer.web.CoreUploadFileItem;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2017/11/10 10:45
 * 描    述 :
 */
public class HttpUtils {
    /** 日志对象 */
    private static final Logger log = LoggerFactory.getLogger(HttpUtils.class);

    public static final int MAX_BLOCK_SIZE = (8192 * 1024);
    public static final String DEFAULT_FILE_CONTENT_TYPE = "text/plain";
//    public static final String DEFAULT_FILE_CONTENT_TYPE = "application/octet-stream";
    final static int HTTP_RESULT_OK = 200;

    public static <T> CloseableHttpResponse postData(CloseableHttpClient client, @NotNull String url, String type, T data) {
        final String requestBodyType = "application/json";
        final String defaultVarName = "var";

        //建立参数
        StringEntity entity = null;
        if ((type != null) && (data != null)) {
            if (requestBodyType.equals(type)) {
                entity = new StringEntity(JsonUtils.obj2Json(data), StandardCharsets.UTF_8);
            } else {
                List<NameValuePair> params = new ArrayList<>();
                if (data instanceof Map) {
                    for (Map.Entry<?, ?> item : ((Map<?, ?>) data).entrySet()) {
                        params.add(new BasicNameValuePair(item.getKey().toString(), JsonUtils.obj2Json(item.getValue())));
                    }
                } else {
                    params.add(new BasicNameValuePair(defaultVarName, JsonUtils.obj2Json(data)));
                }
                entity = new UrlEncodedFormEntity(params, StandardCharsets.UTF_8);
            }
            entity.setContentEncoding("UTF-8");
            entity.setContentType(type);
        }

        // 发起Post请求
        HttpPost post = new HttpPost(url);
        if (type != null) post.setHeader(HTTP.CONTENT_TYPE,type);
        if (entity != null) post.setEntity(entity);
        CloseableHttpResponse response = null;
        try {
            response = client.execute(post);
        } catch (HttpHostConnectException e) {
            log.warn("无法连接" + url);
            response = null;
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        }
        return response;
    }

    public static <T> CloseableHttpResponse postData(CloseableHttpClient client, String url){
        return postData(client,url,null,null);
    }

    public static CoreResponse<?> postFileData(@NotNull String urlString, @NotNull ArrayList<CoreKeyValuePair> propertyList,
                                             CoreUploadFileItem fileItem, long pos, int size, int blockSize) {
        final String END_LINE  = "\r\n";
        final String BOUNDARY = "------WebKitFormBoundaryevvAep7TZEo073p4";
        final String BOUNDARY_BODY = END_LINE + "--" + BOUNDARY + END_LINE;
        final String BOUNDARY_END = END_LINE + "--" + BOUNDARY + "--" + END_LINE;
        final String BODY_NAME_START = "Content-Disposition: form-data; name=\"";
        final String BODY_NAME_END = "\"" + END_LINE + END_LINE;
        final String FILE_PROPERTY_START = "Content-Disposition: form-data; name=\"";
        final String FILE_PROPERTY_END = "\";";
        final String FILE_NAME_START = "filename=\"";
        final String FILE_NAME_END = "\"" + END_LINE;
        final String FILE_CONTENT_TYPE = "Content-Type:" + DEFAULT_FILE_CONTENT_TYPE + END_LINE + END_LINE;
        final String FILE_SPLIT = "--" + BOUNDARY;
        final String FILE_END = "--" + BOUNDARY + "--" + END_LINE;
        final String DEFAULT_CHAR_SET = StandardCharsets.UTF_8.name();
        final String[][] REQUEST_PROPERTIES = {
                {"Connection","Keep-Alive"}
                ,{"Charset",DEFAULT_CHAR_SET}
                ,{"Content-Type","multipart/form-data; boundary=" + BOUNDARY}
        };
        final String DEFAULT_METHOD = HttpPost.METHOD_NAME;

        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            log.error("无法连接服务器",e);
        }

        HttpURLConnection connection = null;
        OutputStream out = null;
        RandomAccessFile rf = null;
        if (url != null) {
            try {
                log.info("正在连接" + urlString);
                // 向服务器发送post请求
                connection = (HttpURLConnection) url.openConnection();

                // 发送POST请求必须设置如下两行
                log.info("连接成功，准备发送");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setUseCaches(false);
                connection.setRequestMethod(DEFAULT_METHOD);
                for (String[] ptyPair : REQUEST_PROPERTIES) {
                    int n = 0;
                    String key = ptyPair[n++];
                    String val = null;
                    if (n < ptyPair.length) val = ptyPair[n];
                    connection.setRequestProperty(key, val);
                }
                out = connection.getOutputStream();

                // 处理普通表单域(即形如key = value对)的POST请求
                StringBuilder contentBody = new StringBuilder();
                for (CoreKeyValuePair kvp : propertyList) {
                    contentBody.append(BOUNDARY_BODY)
                            .append(BODY_NAME_START)
                            .append(kvp.getKey())
                            .append(BODY_NAME_END)
                            .append(kvp.getValue());
                }
                out.write(contentBody.toString().getBytes(DEFAULT_CHAR_SET));
                log.info(contentBody.toString());

                if (fileItem != null) {
                    //写文件起始块
                    contentBody = new StringBuilder();
                    contentBody.append(BOUNDARY_BODY)
                            .append(FILE_PROPERTY_START)
                            .append(fileItem.getPropertyName())
                            .append(FILE_PROPERTY_END)
                            .append(FILE_NAME_START)
                            .append(StringUtils.getFileName(fileItem.getFileName()))
                            .append(FILE_NAME_END)
                            .append(FILE_CONTENT_TYPE);
                    out.write(contentBody.toString().getBytes(DEFAULT_CHAR_SET));
                    log.info(contentBody.toString());

                    // 真正向服务器写文件
                    rf = new RandomAccessFile(fileItem.getFileName(), "r");

                    long realPos = pos;
                    int realSize = size;
                    if (realSize == 0) realSize = (int) rf.length();
                    if (blockSize == 0) {
                        blockSize = MAX_BLOCK_SIZE;
                    }
                    if ((0 < blockSize) && (blockSize < realSize)) realSize = blockSize;
                    while (realPos < (pos + size)) {
                        if (realPos > pos) out.write(BOUNDARY_BODY.getBytes(DEFAULT_CHAR_SET));
                        if (realSize > (rf.length() - realPos)) realSize = (int) (rf.length() - realPos);
                        byte[] buf = new byte[realSize];
                        rf.seek(realPos);
                        int bytes = rf.read(buf);
                        out.write(buf, 0, bytes);
                        realPos += bytes;
                        log.info("已发送了" + realPos);
                    }
                }

                //写结尾
                out.write(BOUNDARY_END.getBytes(DEFAULT_CHAR_SET));
                log.info(BOUNDARY_END);
                out.flush();
                log.info("发送完毕");
            } catch (IOException e) {
                log.error("发送web操作时错误",e);
            } finally {
                FileUtils.close(rf);
                FileUtils.close(out);
                log.info("关闭发送流");
            }
        }

        //从服务器获得回答的内容
        StringBuilder responseString = new StringBuilder();
        if (connection != null) {
            InputStream in = null;
            try {
                log.info("等待回应");
                in = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String responseLine="";
                while((responseLine = reader.readLine()) != null) {
                    responseString.append(responseLine).append("\n");
                }
            } catch (IOException e) {
                log.error("发送web操作后接收返回值时错误",e);
            } finally {
                FileUtils.close(in);
                log.info("关闭接收流");
            }
        }

        return getResult(responseString.toString());
    }
    public static CoreResponse<?> postFileData(@NotNull String urlString, @NotNull ArrayList<CoreKeyValuePair> propertyList,
                                               CoreUploadFileItem fileItem, long pos, int size) {
        return postFileData(urlString,propertyList,fileItem,pos,size,0);
    }
    public static CoreResponse<?> postFileData(@NotNull String urlString, @NotNull Map<String,String> propertyMap,
                                               CoreUploadFileItem fileItem, long pos, int size) {
        ArrayList<CoreKeyValuePair> propertyList = new ArrayList<>();
        for(Map.Entry<String,String> entry : propertyMap.entrySet()){
            CoreKeyValuePair property = new CoreKeyValuePair(entry.getKey(),entry.getValue());
            propertyList.add(property);
        }
        return postFileData(urlString,propertyList,fileItem,pos,size,0);
    }

    public static File getFile(String url,String localDir) {
        return null;
    }

    public static Boolean isResponseOK(CloseableHttpResponse response){
        return (response != null) && (response.getStatusLine() != null) && (response.getStatusLine().getStatusCode() == HTTP_RESULT_OK);
    }

    public static CoreResponse getResult(CloseableHttpResponse response){
        CoreResponse result = null;
        try {
            result = JsonUtils.json2Obj(EntityUtils.toString(response.getEntity()),CoreResponse.class);
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        }
        return result;
    }

    public static CoreResponse<?> getResult(String responseString){
        return JsonUtils.json2Obj(responseString,CoreResponse.class);
    }

    public static <T> T getResponseData(CoreResponse<?> response, Class<? extends T> dataClass){
        assert (dataClass != null);
        if (response == null) return null;
        if (dataClass.isInstance(response.getData())) {
            return dataClass.cast(response.getData());
        } else {
            return null;
        }
    }
}
