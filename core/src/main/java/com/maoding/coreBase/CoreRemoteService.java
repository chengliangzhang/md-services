package com.maoding.coreBase;

import com.maoding.coreUtils.StringUtils;
import com.zeroc.Ice.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;

/**
 * 深圳市卯丁技术有限公司
 * 作    者 : 张成亮
 * 日    期 : 2017/9/12 20:12
 * 描    述 :
 */
public class CoreRemoteService<P extends ObjectPrx> extends _ObjectPrxI implements ObjectPrx{
    /** 日志对象 */
    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    protected static final Logger staticLog = LoggerFactory.getLogger(CoreRemoteService.class);

    /** 查找远程服务线程 */
    private class ConnectThread extends Thread {
        /** 配置环境 */
        private String[] config;
        private String service;
        private Class<P> proxy;
        private Class<?> impl;

        ConnectThread(String service, String[] config, Class<P> proxy, Class<?> impl) {
            this.service = service;
            this.config = config;
            this.proxy = proxy;
            this.impl = impl;
        }

        @Override
        public void run() {
            //查找服务代理
            P prx = null;
            try {
                Communicator c = getCommunicator(config);
                prx = ObjectPrx._checkedCast(c.stringToProxy(service),P.ice_staticId(), proxy, impl);
                String info = ((c.getDefaultLocator() != null) ? "在" + c.getDefaultLocator().toString() : "") + "找到" + service + "服务";
                if (!StringUtils.isSame(lastInfo, info)) {
                    log.info(info);
                    lastInfo = info;
                }
            } catch (ConnectFailedException e) {
                String info = ((lastCommunicator != null) && (lastCommunicator.getDefaultLocator() != null) ?
                        "在" + lastCommunicator.getDefaultLocator().toString() : "") + "无法找到" + service + "服务";
                if (!StringUtils.isSame(lastInfo,info)) {
                    log.warn(info);
                    lastInfo = info;
                }
            }

            remotePrx = prx;
            connectThread = null;
        }
    }

    /** 最后使用的连接对象 */
    private static Communicator lastCommunicator = null;

    /** 最后远程连接地址 */
    private static String lastLocator = null;

    /** 最后提示信息 */
    private static String lastInfo = null;

    /** 远程服务代理 */
    private volatile P remotePrx = null;

    /** 查找远程服务线程 */
    private volatile ConnectThread connectThread = null;

    public static Communicator getCommunicator(String[] config) {
        if (lastCommunicator == null){
            if ((config == null) || (config.length <= 0)){
                lastCommunicator = Util.initialize();
            } else {
                lastCommunicator = Util.initialize(config);
            }
        }
        return lastCommunicator;
    }

    public P getServicePrx(String service, String config, @NotNull Class<P> proxy, Class<? extends _ObjectPrxI> impl, P defaultPrx) {
        if ((connectThread == null) && (StringUtils.isNotEmpty(service))){
            String[] configArray = null;
            if (StringUtils.isNotEmpty(config)){
                configArray = config.split(StringUtils.SPLIT_CONTENT);
            }
            connectThread = new ConnectThread(service,configArray,proxy,impl);
            connectThread.start();
            //如果未配置默认服务代理等待连接动作完成，否则立即返回默认服务代理
            try {
                if (defaultPrx == null) {
                    connectThread.join();
                } else {
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                log.warn("远程连接线程被中断:" + e);
            }
        }

        P prx = remotePrx;
        //如果远程服务代理尚未连接成功返回默认服务代理
        if (prx != null){
            String thisLocator = remotePrx.ice_getConnection().toString().replace('\n', ',');
            if (!StringUtils.isSame(thisLocator, lastLocator)) {
                log.info("切换到" + thisLocator + "的" + service + "服务");
                lastLocator = thisLocator;
            }
        } else if (defaultPrx != null){
            prx = defaultPrx;
            if (lastLocator != null) {
                log.info("切换到默认的" + service + "服务");
                lastLocator = null;
            }
            lastLocator = null;
        } else {
            log.info("无法找到" + service + "服务");
            lastLocator = null;
        }
        return prx;
    }
    public P getServicePrx(String service, String config, @NotNull Class<P> proxy, Class<? extends _ObjectPrxI> impl) {
        return getServicePrx(service,config,proxy,impl,null);
    }
    public P getServicePrx(String config, @NotNull Class<P> proxy, Class<? extends _ObjectPrxI> impl,  P defaultPrx) {
        P prx = defaultPrx;
        if (StringUtils.isNotEmpty(config)) {
            String s = StringUtils.left(config,StringUtils.SPLIT_CONTENT);
            String c = StringUtils.right(config,StringUtils.SPLIT_CONTENT);
            prx = getServicePrx(s, c, proxy, impl, prx);
        }
        return prx;
    }
}
