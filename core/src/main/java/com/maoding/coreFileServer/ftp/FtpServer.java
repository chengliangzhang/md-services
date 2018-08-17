package com.maoding.coreFileServer.ftp;

import com.maoding.coreFileServer.*;
import com.maoding.coreUtils.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2017/10/31 10:33
 * 描    述 :
 */
@Service("ftpServer")
@SuppressWarnings("deprecation")
public class FtpServer implements CoreFileServer {
    protected static final Logger log = (Logger) LoggerFactory.getLogger(FtpServer.class);
    public FTPClient ftpClient = new FTPClient();
    private static final String FILE_SERVER_PATH = "c:\\";
    public static final String BASE_DIR_NAME = "scope";
    public static final String PATH_NAME = "key";
    public static final Integer DEFAULT_CHUNK_PER_SIZE = 8192;
    private static final String HOST_NAME = "192.168.17.168";
    private static final int PORT = 21;
    private static final String USER_NAME = "anonymous";
    private static final String PASSWORD = "abc@abc.com";

    final static Integer FILE_SERVER_TYPE_FASTFDS = 1;
    final static Integer FILE_SERVER_TYPE_ALIYUN = 2;
    final static Integer FILE_SERVER_TYPE_CIFS = 3;
    final static Integer FILE_SERVER_TYPE_FTP = 4;
    final static Integer FILE_SERVER_TYPE_LOCAL = 5;

    final static Integer FILE_SERVER_MODE_DEFAULT = 0;
    final static Integer FILE_SERVER_MODE_DEFAULT_COM = 1;
    final static Integer FILE_SERVER_MODE_HTTP_GET = 2;
    final static Integer FILE_SERVER_MODE_HTTP_POST = 3;
    final static Integer FILE_SERVER_MODE_OSS = 4;
    final static Integer FILE_SERVER_MODE_LOCAL = 5;
    final static Integer FILE_SERVER_MODE_RPC = 6;

    final static Integer DEFAULT_CHUNK_SIZE = 8192;

    final static Short OPEN_MODE_READ_ONLY = 0;
    final static Short OPEN_MODE_READ_WRITE = 1;
    
//    private static final String HOST_NAME = "192.168.33.148";
//    private static final int PORT = 2121;
//    private static final String USER_NAME = "administrator";
//    private static final String PASSWORD = "30116992";

    /**
     * 获取通过http方式上传文件数据库时的需要设置的部分参数
     *
     * @param src
     * @param callbackSetting
     */
    @SuppressWarnings("deprecation")
    public BasicFileRequestDTO getUploadRequest(CoreFileDTO src, Integer mode, BasicCallbackDTO callbackSetting) {
        //补全参数
        if (StringUtils.isEmpty(src.getScope())) src.setScope("");
        if (StringUtils.isEmpty(src.getKey())) src.setKey(UUID.randomUUID().toString() + ".txt");

        //建立申请上传参数对象
        BasicFileRequestDTO requestDTO = new BasicFileRequestDTO();
        requestDTO.setUrl("http://localhost:8087");
        requestDTO.putParam(BASE_DIR_NAME, src.getScope());
        requestDTO.putParam(PATH_NAME, src.getKey());
        if (FILE_SERVER_MODE_DEFAULT.equals(mode)) {
            requestDTO.setMode(FILE_SERVER_MODE_LOCAL);
        } else {
            requestDTO.setMode(FILE_SERVER_MODE_HTTP_POST);
        }
        requestDTO.putParam("uploadId", UUID.randomUUID().toString());
        return requestDTO;
    }

    /**
     * 获取通过http方式下载文件数据库时的需要设置的部分参数
     *
     * @param src
     * @param callbackSetting
     */
    @SuppressWarnings("deprecation")
    public BasicFileRequestDTO getDownloadRequest(CoreFileDTO src, Integer mode, BasicCallbackDTO callbackSetting) {
        //检查参数
        assert src != null;
        assert src.getKey() != null;

        //补全参数
        if (StringUtils.isEmpty(src.getScope())) src.setScope("");

        //建立申请下载参数对象
        BasicFileRequestDTO requestDTO = new BasicFileRequestDTO();
        requestDTO.setUrl("http://localhost:8087");
        requestDTO.putParam(BASE_DIR_NAME, src.getScope());
        requestDTO.putParam(PATH_NAME, src.getKey());
        if (FILE_SERVER_MODE_DEFAULT.equals(mode)) {
            requestDTO.setMode(FILE_SERVER_MODE_LOCAL);
        } else {
            requestDTO.setMode(FILE_SERVER_MODE_HTTP_POST);
        }
        File f = new File(FILE_SERVER_PATH + "/" + src.getKey());
        requestDTO.putParam("size", ((Long) f.length()).toString());
        return requestDTO;
    }

    /**
     * 递归创建远程服务器目录
     *
     * @param remote    远程服务器文件绝对路径
     * @param ftpClient FTPClient对象
     * @return 目录创建是否成功
     * @throws IOException
     */
    public boolean CreateDirecroty(String remote, FTPClient ftpClient) throws IOException {
        boolean result = true;
        String directory = remote.substring(0, remote.lastIndexOf("/") + 1);
        if (!directory.equalsIgnoreCase("/") && !ftpClient.changeWorkingDirectory(new String(directory.getBytes("GBK"), "iso-8859-1"))) {
            //如果远程目录不存在，则递归创建远程服务器目录
            int start = 0;
            int end = 0;
            if (directory.startsWith("/")) {
                start = 1;
            } else {
                start = 0;
            }
            end = directory.indexOf("/", start);
            while (true) {
                String subDirectory = new String(remote.substring(start, end).getBytes("GBK"), "iso-8859-1");
                if (!ftpClient.changeWorkingDirectory(subDirectory)) {
                    if (ftpClient.makeDirectory(subDirectory)) {
                        ftpClient.changeWorkingDirectory(subDirectory);
                    } else {
                        System.out.println("创建目录失败");
                        return true;
                    }
                }

                start = end + 1;
                end = directory.indexOf("/", start);

                //检查所有目录是否创建完毕
                if (end <= start) {
                    break;
                }
            }
        }
        return result;
    }

    /**
     * 上传文件到服务器,新上传和断点续传
     *
     * @param remoteFile 远程文件名，在上传之前已经将服务器工作目录做了改变
     * @param localFile  本地文件File句柄，绝对路径
     * @param ftpClient  FTPClient引用
     * @return
     * @throws IOException
     */
    public boolean uploadFile(String remoteFile, File localFile, FTPClient ftpClient, long remoteSize, BasicUploadRequestDTO request) throws IOException {
        boolean status = false;
        //显示进度的上传
        long step = localFile.length() / request.getChunkCount();
        long process = 0;
        long localreadbytes = 0L;
        RandomAccessFile raf = new RandomAccessFile(localFile, "r");
        OutputStream out = ftpClient.appendFileStream(new String(remoteFile.getBytes("GBK"), "iso-8859-1"));
        //断点续传
        if (remoteSize > 0) {
            ftpClient.setRestartOffset(remoteSize);
            process = remoteSize / step;
            raf.seek(remoteSize);
            localreadbytes = remoteSize;
        }
        Integer chunkSize = null != request.getChunkSize() ? request.getChunkSize() : request.getMultipart().getSize();
        byte[] bytes = new byte[chunkSize];
        int c;
        while ((c = raf.read(bytes)) != -1) {
            out.write(bytes, 0, c);
            localreadbytes += c;
            if (localreadbytes / step != process) {
                process = localreadbytes / step;
                System.out.println("上传进度:" + process);
                //TODO 汇报上传状态
            }
        }
        out.flush();
        raf.close();
        out.close();
        boolean result = ftpClient.completePendingCommand();
        if (remoteSize > 0 && result) {
            status = true;
        }
        return status;
    }


    /**
     * 判断在文件服务器上是否存在指定文件
     *
     * @param src
     */
    public Boolean coreIsExist(CoreFileDTO src) {
        FTPFile[] files;
        boolean status = false;
        try {
            //切换目录
            ftpClient.changeWorkingDirectory(new String(src.getScope().getBytes("GBK"), "iso-8859-1"));
            files = ftpClient.listFiles(src.getKey());
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getMessage(),e);
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException ioe) {
                    log.error(ioe.getMessage());
                }
            }
        }
        return false;
    }

    /**
     * 获取文件服务器上某一空间上的所有文件
     *
     * @param scope
     */
    public List listFile(String scope) {
        List fileList = new ArrayList();
        FTPFile[] files;
        try {
            files = ftpClient.listFiles(scope);
            fileList = Arrays.asList(files);
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getMessage(),e);
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException ioe) {
                    log.error(ioe.getMessage());
                }
            }
        }
        return fileList;
    }


    /**
     * 在文件服务器上删除指定文件
     *
     * @param src
     */
    public void coreDeleteFile(CoreFileDTO src) {
        try {
            //切换目录
            ftpClient.changeWorkingDirectory(new String(src.getScope().getBytes("GBK"), "iso-8859-1"));
            //设置PassiveMode传输
            ftpClient.enterLocalPassiveMode();
            //设置以二进制流的方式传输
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.setControlEncoding("GBK");
            //对远程目录的处理
            String remoteFileName = src.getKey();
            if (src.getKey().contains("/")) {
                remoteFileName = src.getKey().substring(src.getKey().lastIndexOf("/") + 1);
            }
            //检查远程文件是否存在
            FTPFile[] files = ftpClient.listFiles(new String(remoteFileName.getBytes("GBK"), "iso-8859-1"));
            if (1 == files.length || remoteFileName.equals(files[0].getName())) {
                ftpClient.deleteFile(remoteFileName);
            }
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException ioe) {
                    log.error(ioe.getMessage());
                }
            }
        }
    }

    /**
     * 连接到FTP服务器
     *
     * @return 是否连接成功
     * @throws IOException
     */
    public boolean connect() throws IOException {
        ftpClient.connect(HOST_NAME, PORT);
        ftpClient.setControlEncoding("GBK");
        if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
            if (ftpClient.login(USER_NAME, PASSWORD)) {
                return true;
            }
        }
        return ftpClient.isConnected();
    }
}
