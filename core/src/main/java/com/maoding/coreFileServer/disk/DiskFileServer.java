package com.maoding.coreFileServer.disk;

import com.maoding.coreFileServer.CoreCreateFileRequest;
import com.maoding.coreFileServer.CoreFileDataDTO;
import com.maoding.coreFileServer.CoreFileServer;
import com.maoding.coreUtils.FileUtils;
import com.maoding.coreUtils.ObjectUtils;
import com.maoding.coreUtils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2017/11/1 14:41
 * 描    述 :
 */
@Service("diskFileServer")
public class DiskFileServer implements CoreFileServer {
    /** 日志对象 */
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final Integer MAX_TRY_TIMES = 6;
    private static final Integer TRY_DELAY = 100;

    public static final String FILE_NAME_SPLIT = "_";
    public static final String BASE_DIR_NAME = "scope";
    public static final String PATH_NAME = "key";
    public static final Integer DEFAULT_CHUNK_PER_SIZE = 8192;

    private static final String DEFAULT_BASE_DIR = "C:\\work\\file_server";
    private static final String DEFAULT_UNKNOWN_SCOPE_DIR = "unknown_scope";
    private static final String FILE_UPLOAD_URL = "http://localhost:8087/FileServer/upload";
    private static final String FILE_DOWNLOAD_URL = "http://localhost:8087/FileServer/download";
    private static final String KEY_SIZE = "size";
    private static final String KEY_UPLOAD_ID = "uploadId";
    private String baseDir = null;

    /**
     * 设定文件服务器地址
     *
     * @param serverAddress
     */
    @Override
    public void coreSetServerAddress(String serverAddress) {
        coreSetBaseDir(serverAddress);
    }

    /**
     * 获取文件服务器地址
     */
    @Override
    public String coreGetServerAddress() {
        return coreGetBaseDir();
    }

    /**
     * 设定文件服务器存储空间
     *
     * @param baseDir
     */
    @Override
    public void coreSetBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    /**
     * 获取文件服务器存储空间
     */
    @Override
    public String coreGetBaseDir() {
        return (StringUtils.isNotEmpty(baseDir)) ? baseDir : DEFAULT_BASE_DIR;
    }

    /**
     * 设置本地镜像根目录
     *
     * @param mirrorBaseDir
     */
    @Override
    public void coreSetMirrorBaseDir(String mirrorBaseDir) {
        coreSetBaseDir(mirrorBaseDir);
    }

    /**
     * 获取本地镜像根目录
     */
    @Override
    public String coreGetMirrorBaseDir() {
        return coreGetBaseDir();
    }

    /**
     * 判断在文件服务器上是否存在指定文件
     *
     */
    @Override
    public boolean coreIsExist(String key) {
        if (StringUtils.isEmpty(key)) return false;
        File f = new File(getPath(key));
        return FileUtils.isValidFile(f);
    }

    /**
     * 创建文件
     *
     * @param createRequest
     */
    @Override
    public String coreCreateFile(CoreCreateFileRequest createRequest) {
        long t = System.currentTimeMillis();

        String key = ((createRequest != null) && (StringUtils.isNotEmpty(createRequest.getPath()))) ?
                createRequest.getPath() : getUniqueKey();
        if (coreIsExist(key)) {
            key = getUniqueKey(key);
        }

        String path = getPath(key);
        ensureDirExist(StringUtils.getDirName(path));

        try {
            File dstFile = new File(path);
            boolean isOk = dstFile.createNewFile();
            assert (isOk);
            if (createRequest != null){
                if (createRequest.getSrcFile() != null){
                    FileUtils.copyFile(createRequest.getSrcFile(),dstFile);
                } else if (createRequest.getFileLength() > 0) {
                    FileUtils.setFileLength(dstFile, createRequest.getFileLength());
                }
            }
        } catch (IOException e) {
            log.warn("无法建立文件" + path + ":" + e);
        }

        log.info("创建了" + path + "，用时" + (System.currentTimeMillis() - t) + "ms");
        return key;
    }

    /**
     * 计算文件MD5
     *
     * @param key 文件标志
     */
    @Override
    public String coreCalcMd5(String key) {
        assert (coreIsExist(key));
        File file = new File(getPath(key));
        return FileUtils.calcMd5(file);
    }

    /**
     * 获取本地文件或镜像文件
     *
     * @param key
     * @param mirrorBaseDir
     */
    @Override
    public File coreGetFile(String key, String mirrorBaseDir) {
        return new File(getPath(key));
    }

    /**
     * 获取文件长度
     *
     * @param key
     */
    @Override
    public long coreGetFileLength(String key) {
        long len = 0;
        if (coreIsExist(key)){
            File file = new File(getPath(key));
            len = FileUtils.getFileLength(file);
        }
        return len;
    }

    /**
     * 设置文件长度
     *
     * @param key 文件标志
     * @param fileLength 文件目标长度
     */
    @Override
    public void coreSetFileLength(String key, long fileLength) {
        String path = getPath(key);
        File file = new File(path);
        FileUtils.setFileLength(file,fileLength);
    }

    /** 改名或移动文件 */
    @Override
    public String coreMoveFile(@NotNull String src, @NotNull String dst) {
        if (!coreIsExist(src)) return null;
        if (coreIsExist(dst)) {
            dst = getUniqueKey(dst);
        }

        ensureDirExist(StringUtils.getDirName(getPath(dst)));

        File srcFile = new File(getPath(src));
        File dstFile = new File(getPath(dst));
        boolean isSuccess = srcFile.renameTo(dstFile);
        assert (isSuccess);
        return dst;
    }

    /**
     * 读取文件内容
     *
     * @param key 读取文件标志
     * @param pos 读取数据起始位置
     * @param size 读取数据尺寸
     */
    @Override
    public CoreFileDataDTO coreReadFile(@NotNull String key, long pos, int size) {
        long t = System.currentTimeMillis();

        //检查参数
        assert (coreIsExist(key));
        assert (pos >= 0);

        //补全参数
        if (size <= 0) size = DEFAULT_CHUNK_PER_SIZE;

        CoreFileDataDTO result = null;
        RandomAccessFile rf = null;
        String path = getPath(key);

        //打开文件
        for (Integer i=0; i<MAX_TRY_TIMES; i++) {
            try {
                rf = new RandomAccessFile(path, "r");
                break;
            } catch (IOException e) {
                log.warn( "打开文件" + path + "出错");
                try {
                    Thread.sleep(TRY_DELAY);
                } catch (InterruptedException e1) {
                    log.warn(e1.getMessage());
                }
                rf = null;
            }
        }

        try {
            if ((rf != null) && (pos < rf.length())) {
                //定位
                rf.seek(pos);
                //读取文件内容
                assert (size > 0);
                byte[] bytes = new byte[size];
                int n = rf.read(bytes);
                assert (n > 0) && (n <= size);
                bytes = Arrays.copyOfRange(bytes, 0, n);
                size = n;

                //设置返回参数
                result = new CoreFileDataDTO();
                result.setPos(pos);
                result.setSize(size);
                result.setData(bytes);
            }
        } catch (IOException e) {
            log.error("读取时出错",e);
            result = null;
        } finally {
            FileUtils.close(rf);
        }

        if (result != null) {
            log.info("从" + path + "读取了" + StringUtils.calBytes(result.getSize()) + "数据，用时" + (System.currentTimeMillis() - t) + "ms,速度"
                    + StringUtils.calSpeed(result.getSize(),t));
        } else {
            log.info("未能读取有效数据，用时" + (System.currentTimeMillis() - t) + "ms");
        }
        return result;
    }

    /**
     * 写入文件内容
     *
     * @param data 要写入的数据
     * @param key 文件标识
     */
    @Override
    public int coreWriteFile(@NotNull CoreFileDataDTO data, String key) {

        long t = System.currentTimeMillis();

        //获取参数
        int off = 0;
        long pos = data.getPos();
        byte[] dataArray = data.getData();
        assert (dataArray != null);
        int len = data.getSize();
        if (len <= 0){
            len = dataArray.length;
        }
        String path = getPath(key);

        //创建路径
        assert (StringUtils.isNotEmpty(path));
        ensureDirExist(StringUtils.getDirName(path));

        //打开文件
        RandomAccessFile rf = null;
        for (Integer i=0; i<MAX_TRY_TIMES; i++) {
            try {
                rf = new RandomAccessFile(path, "rw");
                break;
            } catch (IOException e) {
                log.warn("打开文件" + path + "出错");
                try {
                    Thread.sleep(TRY_DELAY);
                } catch (InterruptedException e1) {
                    log.warn(e1.getMessage());
                }
                rf = null;
            }
        }

        //写入文件
        try {
            if (rf != null) {
                assert ((pos >= 0) && (len > 0) && (dataArray != null) && (len <= dataArray.length));
                if (rf.length() < pos) rf.setLength(pos + len);
                rf.seek(pos);
                rf.write(dataArray, off, len);
            }
        } catch (IOException e) {
            String msg = "写入文件" + path + "时出错";
            log.warn(msg);
            len = 0;
        } finally {
            FileUtils.close(rf);
        }

        log.info("\t----> coreWriteFile写入" + StringUtils.calBytes(len) + "到" + path + "："
                + "pos=" + data.getPos() + ",size=" + data.getSize() + ","
                + "用时" + (System.currentTimeMillis() - t) + "ms,"
                + "速度" + StringUtils.calSpeed(len,t));
        return len;
    }

    private void ensureDirExist(String dirPath){
        assert (StringUtils.isNotEmpty(dirPath));
        File fd = new File(StringUtils.formatPath(dirPath));
        if (!fd.exists()) {
            boolean isSuccess = fd.mkdirs();
            assert (isSuccess);
        }
    }

    private String getPath(String key){
        return StringUtils.formatPath(coreGetBaseDir() + StringUtils.SPLIT_PATH + key);
    }


    private String getUniqueKey(String key){
        final String TIME_STAMP_FORMAT =  StringUtils.TIME_STAMP_FORMAT;

        if (StringUtils.isEmpty(key)) {
            key = UUID.randomUUID().toString();
        } else {
            String dir = StringUtils.getDirName(key);
            String fn = StringUtils.getFileNameWithoutExt(key);
            String ext = StringUtils.getFileExt(key);
            assert (fn != null);
            fn = StringUtils.addTimeStamp(fn);
            StringBuilder keyBuilder = new StringBuilder(dir);
            if (keyBuilder.length() > 0) {
                keyBuilder.append(StringUtils.SPLIT_PATH);
            }
            keyBuilder.append(fn);
            final int MAX_UNIQUE_NUM = 1000;
            int i = 0;
            String tmpKey;
            do {
                tmpKey = keyBuilder.toString();
                if (i > 0) {
                    tmpKey += StringUtils.SPLIT_NAME_PART + i;
                }
                if (StringUtils.isNotEmpty(ext)) {
                    tmpKey += ext;
                }
            } while (i++<MAX_UNIQUE_NUM && coreIsExist(tmpKey));
            key = tmpKey;
        }

        return key;
    }
    private String getUniqueKey(){
        return getUniqueKey(null);
    }


    /**
     * 删除指定文件
     *
     * @param key
     */
    @Override
    public void coreDeleteFile(String key) {
        if (coreIsExist(key)){
            String path = getPath(key);
            File f = new File(path);
            boolean isSuccess = f.delete();
            assert (isSuccess);
            File p = new File(StringUtils.getDirName(path));
            if (ObjectUtils.isEmpty(p.listFiles())){
                p.delete();
            }
        }
    }

    @Override
    public List<String> coreListKey(long timeBefore) {
        long t = System.currentTimeMillis();
        List<String> list = new ArrayList<>();
        File root = new File(coreGetBaseDir());
        appendFiles(list,root,t - timeBefore);
        return list;
    }

    private void appendFiles(List<String> list,File f,long beforeTime){
        if ((f != null) && (f.exists())) {
            if (f.isDirectory()){
                File[] childArray = f.listFiles();
                if (ObjectUtils.isNotEmpty(childArray)){
                    for (File child : childArray) {
                        if (child.isDirectory()) {
                            appendFiles(list, child,beforeTime);
                        } else if (child.lastModified() < beforeTime){
                            String key = getKeyByPath(child.getPath());
                            list.add(key);
                        }
                    }
                }
            } else {
                String key = getKeyByPath(f.getPath());
                list.add(key);
            }
        }
    }

    private String getKeyByPath(String path){
        String key = StringUtils.formatPath(path);
        if (StringUtils.isNotEmpty(key)){
            key = key.substring(StringUtils.length(StringUtils.formatPath(coreGetBaseDir())));
        }
        return StringUtils.formatPath(key);
    }
}
