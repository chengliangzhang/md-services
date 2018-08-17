//package com.maoding.FileServer.Aliyun;
//
//import com.alibaba.druid.support.json.JSONUtils;
//import com.aliyun.oss.ClientException;
//import com.aliyun.oss.OSSClient;
//import com.aliyun.oss.common.utils.BinaryUtil;
//import com.aliyun.oss.model.*;
//import com.maoding.Bean.*;
//import com.maoding.Const.ApiResponseConst;
//import com.maoding.FileServer.BasicFileServerInterface;
//import com.maoding.Utils.ExceptionUtils;
//import com.maoding.Utils.OSSClientUtils;
//import com.maoding.Utils.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//
//import java.io.ByteArrayInputStream;
//import java.net.URL;
//import java.util.*;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//
///**
// * 深圳市卯丁技术有限公司
// * 作    者 : 张成亮
// * 日    期 : 2017/10/25 10:05
// * 描    述 :
// */
//@Service("aliyunServer")
//public class AliyunServer implements BasicFileServerInterface {
//    protected static final Logger log = (Logger) LoggerFactory.getLogger(AliyunServer.class);
//
//    /**
//     * 获取通过http方式上传文件数据库时的需要设置的部分参数
//     *
//     * @param src
//     * @param callbackSetting
//     */
//    @Override
//    public BasicFileRequestDTO getUploadRequest(BasicFileDTO src, Integer mode, BasicCallbackDTO callbackSetting) {
//        BasicFileRequestDTO dto = new BasicFileRequestDTO();
//        String dir = "";
//        if (!StringUtils.isEmpty(src.getKey())) {
//            dir = src.getKey();
//        } else {
//            dir = System.currentTimeMillis() + "";
//        }
//        dto.setKey(src.getKey());
//        OSSClient client = OSSClientUtils.getInstance();
//        String bucketName = "publicmaoding";
//        if (dto.getScope() == null) dto.setScope(bucketName);
//        Map<String, String> paramsMap = new HashMap<String, String>();
//        try {
//            //设置超时时间，可以设置为常量，根据需求设置时长
//            long expireTime = 30;
//            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
//            Date expiration = new Date(expireEndTime);
//            PolicyConditions policyConds = new PolicyConditions();
//            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
//            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);
//            String postPolicy = client.generatePostPolicy(expiration, policyConds);
//            byte[] binaryData = postPolicy.getBytes("utf-8");
//            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
//            String postSignature = client.calculatePostSignature(postPolicy);
//            //上传所要的权限验证信息
//            paramsMap.put("accessid", client.getCredentialsProvider().getCredentials().getAccessKeyId());
//            paramsMap.put("policy", encodedPolicy);
//            paramsMap.put("signature", postSignature);
//            paramsMap.put("dir", dir);
//            paramsMap.put("expire", String.valueOf(expireEndTime / 1000));
//            dto.setParams(paramsMap);
//            //设置callback
//            Map<String, Object> callback = new HashMap<>();
//            if (null != callbackSetting) {
//                callback.put("callbackUrl", callbackSetting.getUrl());
//                callback.put("callbackHost", callbackSetting.getName());
//                callback.put("callbackBodyType", "application/json");
//                callback.put("callbackBody", getCallbackBody(callbackSetting.getParams()));
//                byte[] callbackBytes = JSONUtils.toJSONString(callback).getBytes();
//                paramsMap.put("callback", BinaryUtil.toBase64String(callbackBytes));
//                dto.setParams(paramsMap);
//            }
//            //上传地址
//            dto.setUrl("http://" + bucketName + "." + client.getEndpoint().getHost());
//            return dto;
//        } catch (Exception e) {
//            e.printStackTrace();
//            ExceptionUtils.logError(log, e);
//        }
//
//        return dto;
//    }
//
//
//    /**
//     * @Author：MaoSF Description：获取callbackBody字符串
//     * @Date:2017/11/9
//     */
//    private String getCallbackBody(Map<String, String> callbackVar) {
//        //循环输出key
//        StringBuilder callBodyBuf = new StringBuilder();
//        for (Map.Entry<String, String> entry : callbackVar.entrySet()) {
//            callBodyBuf.append(",");
//            callBodyBuf.append("\"");
//            callBodyBuf.append(entry.getKey());
//            callBodyBuf.append("\"");
//            callBodyBuf.append(":");
//            callBodyBuf.append("\"");
//            callBodyBuf.append(entry.getValue());
//            callBodyBuf.append("\"");
//        }
//        String callBodyStr = callBodyBuf.toString();
//        //oss自带参数
//        String bodyStr = "{\"bucket\":${bucket},\"object\":${object},"
//                + "\"mimeType\":${mimeType},\"size\":${size}";
//        bodyStr = bodyStr + callBodyStr;
//        bodyStr = bodyStr + "}";
//        return bodyStr;
//    }
//
//    /**
//     * 获取通过http方式下载文件数据库时的需要设置的部分参数
//     *
//     * @param src
//     * @param callbackSetting
//     */
//    @Override
//    public BasicFileRequestDTO getDownloadRequest(BasicFileDTO src, Integer mode, BasicCallbackDTO callbackSetting) {
//        BasicFileRequestDTO dto = new BasicFileRequestDTO();
//        OSSClient ossClient = OSSClientUtils.getInstance();
//        Date expiration = new Date(System.currentTimeMillis() + 3600);
//        String scope = "publicmaoding";
//        dto.setScope(src.getScope());
//        dto.setKey(src.getKey());
//        URL url = ossClient.generatePresignedUrl(scope, src.getKey(), expiration);
//        if (url != null) {
//            dto.setUrl(url.toString());
//            return dto;
//        }
//        return dto;
//    }
//
//
//    /**
//     * 上传文件分片内容
//     * 暂不支持根据条件创建文件夹
//     *
//     * @param request
//     */
//    @Override
//    public BasicUploadResultDTO multipartUpload(BasicUploadRequestDTO request) {
//        BasicUploadResultDTO result = new BasicUploadResultDTO();
//        Integer chunkSize = null != request.getChunkSize() ? request.getChunkSize() : request.getMultipart().getSize();
//        result.setChunkId(null != request.getChunkId() ? request.getChunkId() : request.getMultipart().getPos().intValue());
//        result.setChunkSize(chunkSize);
//        OSSClient client = OSSClientUtils.getInstance();
//        List<PartETag> partETags = Collections.synchronizedList(new ArrayList<PartETag>());
//        String bucketName = request.getMultipart().getScope();// request.getMultipart().getScope();  publicmaoding
//        ExecutorService executorService = Executors.newFixedThreadPool(5);
//        try {
//            String uploadId = claimUploadId(client, bucketName, request.getMultipart().getKey());
//            final long partSize = request.getChunkPerSize();
//            for (int i = 0; i < request.getChunkCount(); i++) {
//                long startPos = i * partSize;
//                long curPartSize = (i + 1 == request.getChunkCount()) ? (chunkSize - startPos) : partSize;
//                executorService.execute(new PartUploader(new ByteArrayInputStream(request.getMultipart().getData()),
//                        startPos, curPartSize, i + 1, uploadId, bucketName, request.getMultipart().getKey(), partETags, client));
//            }
//            //文件上传完之后，关闭
//            executorService.shutdown();
//            while (!executorService.isTerminated()) {
//                try {
//                    executorService.awaitTermination(5, TimeUnit.SECONDS);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (partETags.size() != request.getChunkCount()) {
//                throw new IllegalStateException("Upload multiparts fail due to some parts are not finished yet");
//            }
//            //完成上传
//            completeMultipartUpload(client, bucketName, request.getMultipart().getKey(), uploadId, partETags);
//            result.setStatus(ApiResponseConst.SUCCESS);
//        } catch (ClientException ce) {
//            result.setStatus(ApiResponseConst.FAILED);
//            ExceptionUtils.logError(log, ce);
//        } finally {
//            if (client != null) {
//                client.shutdown();
//            }
//        }
//        return result;
//    }
//
//    private CompleteMultipartUploadResult completeMultipartUpload(OSSClient client, String bucketName, String key, String uploadId, List<PartETag> partETags) {
//        // Make part numbers in ascending order
//        Collections.sort(partETags, new Comparator<PartETag>() {
//            @Override
//            public int compare(PartETag p1, PartETag p2) {
//                return p1.getPartNumber() - p2.getPartNumber();
//            }
//        });
//
//        CompleteMultipartUploadRequest completeMultipartUploadRequest =
//                new CompleteMultipartUploadRequest(bucketName, key, uploadId, partETags);
//        return client.completeMultipartUpload(completeMultipartUploadRequest);
//    }
//
//    private String claimUploadId(OSSClient client, String bucketName, String key) {
//        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName.toLowerCase(), key);
//        InitiateMultipartUploadResult result = client.initiateMultipartUpload(request);
//        return result.getUploadId();
//    }
//
//    @Override
//    public BasicUploadResultDTO upload(BasicUploadRequestDTO request) {
//        BasicUploadResultDTO basicUploadResultDTO = this.multipartUpload(request);
//        return basicUploadResultDTO;
//    }
//
//    /**
//     * 下载文件分片内容
//     *
//     * @param request
//     */
//    @Override
//    public BasicDownloadResultDTO download(BasicDownloadRequestDTO request) {
//        BasicDownloadResultDTO result = new BasicDownloadResultDTO();
//        OSSClient client = OSSClientUtils.getInstance();
//        // 下载请求，10个任务并发下载，启动断点续传
//        // 测试使用：publicmaoding，正式使用：request.getScope();
//        DownloadFileRequest downloadFileRequest = new DownloadFileRequest(request.getScope(), request.getKey());
//        downloadFileRequest.setDownloadFile(request.getKey());
//        downloadFileRequest.setTaskNum(10);//开起10个任务下载
//        downloadFileRequest.setEnableCheckpoint(true);
//        DownloadFileResult downloadRes = null;
//        try {
//            // 下载文件
//            downloadRes = client.downloadFile(downloadFileRequest);
//            assert (downloadRes != null);
//            // 下载成功时，会返回文件的元信息
//            downloadRes.getObjectMetadata();
//            if (null == downloadRes) {
//                result.setStatus(ApiResponseConst.ERROR);
//            } else {
//                result.setStatus(ApiResponseConst.SUCCESS);
//            }
//        } catch (Throwable throwable) {
//            throwable.printStackTrace();
//        } finally {
//            if (client != null) {
//                client.shutdown();
//            }
//        }
//        // 关闭client
//        client.shutdown();
//        return result;
//    }
//
//    /**
//     * 在文件服务器上复制文件，复制到同一空间，返回复制后的文件标识
//     *
//     * @param src
//     */
//    @Override
//    public String duplicateFile(BasicFileDTO src) {
//        OSSClient ossClient = OSSClientUtils.getInstance();
//        Date date = new Date();
//        CopyObjectResult result = ossClient.copyObject(src.getScope(), src.getKey(), src.getScope(), src.getKey() + date.getTime());
//        return result.getResponse().getUri();
//    }
//
//    /**
//     * 判断在文件服务器上是否存在指定文件
//     *
//     * @param src
//     */
//    @Override
//    public Boolean isExist(BasicFileDTO src) {
//        OSSClient ossClient = OSSClientUtils.getInstance();
//        if (ossClient.getObject(src.getScope(), src.getKey()) != null) {
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * 获取文件服务器上某一空间上的所有文件
//     *
//     * @param scope
//     */
//    @Override
//    public List<String> listFile(String scope) {
//        OSSClient ossClient = OSSClientUtils.getInstance();
//        ObjectListing list = ossClient.listObjects(scope);
//        List<String> keyList = new ArrayList<>();
//        for (OSSObjectSummary obj : list.getObjectSummaries()) {
//            keyList.add(obj.getKey());
//        }
//        return keyList;
//    }
//
//    /**
//     * 获取文件服务器上的所有空间
//     */
//    @Override
//    public List<String> listScope() {
//        OSSClient ossClient = OSSClientUtils.getInstance();
//        List<Bucket> list = ossClient.listBuckets();
//        List<String> listName = new ArrayList<>();
//        for (Bucket bucket : list) {
//            listName.add(bucket.getName());
//        }
//        return listName;
//    }
//
//    /**
//     * 在文件服务器上删除指定文件
//     *
//     * @param src
//     */
//    @Override
//    public void deleteFile(BasicFileDTO src) {
//        OSSClient ossClient = OSSClientUtils.getInstance();
//        ossClient.deleteObject(src.getScope(), src.getKey());
//    }
//}
