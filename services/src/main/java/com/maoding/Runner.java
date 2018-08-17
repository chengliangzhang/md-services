package com.maoding;

import com.maoding.common.config.IceConfig;
import com.maoding.common.config.ScheduleConfig;
import com.maoding.common.config.StartupConfig;
import com.maoding.common.zeroc.CommonService;
import com.maoding.coreUtils.DigitUtils;
import com.maoding.coreUtils.FileUtils;
import com.maoding.coreUtils.StringUtils;
import com.maoding.coreUtils.ThreadUtils;
import com.maoding.fileServer.zeroc.FileServicePrx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2017/12/13 12:06
 * 描    述 :
 */
@Component
@EnableScheduling
public class Runner implements SchedulingConfigurer {
    /** 日志对象 */
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IceConfig iceConfig;

    @Autowired
    private StartupConfig startupConfig;

    @Autowired
    private ScheduleConfig scheduleConfig;

    @Autowired
    private CommonService commonService;

    private boolean started = false;

    public void run(String[] args) {
        //更新数据库
        if (StringUtils.isNotEmpty(startupConfig.getDatabase())){
            updateDatabase(startupConfig.getDatabase());
        }
        //启动管理服务器
        if (StringUtils.isNotEmpty(startupConfig.getIcegrid())){
            runIceGrid(startupConfig.getIcegrid());
        }
        //启动消息服务器
        if (StringUtils.isNotEmpty(startupConfig.getActivemq())){
            runActiveMQ(startupConfig.getActivemq());
        }
        if (StringUtils.isNotEmpty(startupConfig.getIcebox())){
            runIceBox(startupConfig.getIcebox());
        }
        //启动服务
        started = true;
        if (StringUtils.isNotEmpty(iceConfig.getConfig())) {
            startIceService(iceConfig.getConfig());
        }
    }

    public void startIceService(@NotNull String configFile){
        configFile = ensureIcePathExists(configFile);
        log.info("启动IceBox服务，配置文件：" + configFile);
        List<String> paramsList = new ArrayList<>();
        paramsList.add("--Ice.Config=" + configFile);
        String[] paramsArray = paramsList.toArray(new String[paramsList.size()]);
        com.zeroc.IceBox.Server.main(paramsArray);
    }

    private String ensureIcePathExists(@NotNull String configFile) {
        Properties properties = new Properties();
        InputStreamReader reader = null;
        try {
            if (configFile.contains("classpath:")){
                String classPath = this.getClass().getResource("").toString();
                configFile = "file:/" + classPath + "/" + configFile;
            }
            reader = new InputStreamReader(new FileInputStream(configFile));
            properties.load(reader);
            for (Object key : properties.keySet()) {
                String keyStr = key.toString();
                if (keyStr.contains("LMDB.Path")) {
                    String path = new String(properties.getProperty(keyStr).getBytes("ISO-8859-1"), "utf-8");
                    FileUtils.ensureDirExist(path);
                } else if (StringUtils.isSame(keyStr, "Ice.LogFile")) {
                    String path = StringUtils.getDirName(new String(properties.getProperty(keyStr).getBytes("ISO-8859-1"), "utf-8"));
                    FileUtils.ensureDirExist(path);
                }
            }
        } catch (FileNotFoundException e) {
            log.error("配置文件不存在:" + configFile,e);
        } catch (IOException e) {
            log.error("打开配置文件时出错:" + configFile,e);
        } finally {
            FileUtils.close(reader);
        }
        return configFile;
    }

    public void executeCmd(String[] cmdArray){
        if ((cmdArray != null) && (cmdArray.length > 0)) {
            try {
                Runtime.getRuntime().exec(cmdArray);
            } catch (IOException e) {
                log.error("执行命令时出现错误",e);
            }
        }
    }

    private String[] analysisCmd(@NotNull String[] cmdArray,String title){
        if ((cmdArray.length > 0) && (StringUtils.isSame(StringUtils.getFileExt(cmdArray[0]),".bat"))){
            List<String> cmdList = new ArrayList<>();
            cmdList.add("cmd");
            cmdList.add("/c");
            cmdList.add("start");
            if (StringUtils.isNotEmpty(title)) {
                cmdList.add("\"" + title + "\"");
            } else {
                cmdList.add("\"" + cmdArray[0] + "\"");
            }
            Collections.addAll(cmdList, cmdArray);
            cmdArray = cmdList.toArray(new String[cmdList.size()]);
        }
        return cmdArray;
    }
    private String[] analysisCmd(@NotNull String cmd,String title){
        String[] cmdArray = cmd.split(";");
        return analysisCmd(cmdArray,title);
    }
    private String[] analysisCmd(@NotNull String cmd){
        return analysisCmd(cmd,null);
    }

    public void updateDatabase(@NotNull String cmd){
        log.info("升级数据库:" + cmd);
        String[] cmdArray = analysisCmd(cmd,"升级数据库");
        executeCmd(cmdArray);
    }

    public void runActiveMQ(@NotNull String cmd){
        log.info("启动ActiveMQ:" + cmd);
        String[] cmdArray = analysisCmd(cmd,"ActiveMQ");
        executeCmd(cmdArray);
    }

    public void runIceGrid(@NotNull String cmd){
        log.info("启动IceGrid:" + cmd);
        //保证ice服务目录存在
        String[] cmdArray = cmd.split(";");
        if (cmdArray.length > 1) {
            cmdArray[1] = ensureIcePathExists(cmdArray[1]);
        }
        cmdArray = analysisCmd(cmdArray,"IceGrid");

        executeCmd(cmdArray);
        //等待icegrid启动完毕
        ThreadUtils.sleep(4000);
    }

    public void runIceBox(@NotNull String cmd){
        log.info("启动IceBox:" + cmd);
        String[] cmdArray = cmd.split(";");
        if (cmdArray.length > 1) {
            cmdArray[1] = ensureIcePathExists(cmdArray[1]);
        }
        cmdArray = analysisCmd(cmdArray,"IceBox");
        executeCmd(cmdArray);
        //等待ice公共服务启动完毕
        ThreadUtils.sleep(4000);
    }

    @Scheduled(fixedDelay  = 5 * 1000)
    public void clearFileServer() throws Exception {
        //如果尚未完全启动，不执行定义任务
        if (!started) {
            return;
        }

        //定期清理服务器文件
        if (DigitUtils.parseBoolean(scheduleConfig.getClearServer())) {
            FileServicePrx fileServicePrx = commonService.getDefaultFileService(null);
            if (fileServicePrx != null) {
                log.info("------- ------- ------- 清理文件 ------- ------- -------");
                long t = System.currentTimeMillis();
                fileServicePrx.clearAll(null);
                log.info("------- ------- 结束清理文件，用时" + (System.currentTimeMillis() - t) + "ms ------- -------");
            }
        }

        //定期升级文件服务器
        if (DigitUtils.parseBoolean(scheduleConfig.getAutoUpdate())) {
            log.info("------- ------- ------- 开始更新 ------- ------- -------");
            commonService.updateService(null);
            log.info("------- ------- 结束更新 ------- -------");
        }
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
    }

    @Bean
    public Executor taskExecutor() {
        return Executors.newScheduledThreadPool(scheduleConfig.getPoolSize());
    }

}
