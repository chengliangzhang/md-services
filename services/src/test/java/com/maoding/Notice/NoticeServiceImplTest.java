//package com.maoding.Notice;
//
//import com.maoding.Notice.zeroc.MessageDTO;
//import com.maoding.Notice.zeroc.NoticeClientPrx;
//import com.maoding.Notice.zeroc.NoticeRequestDTO;
//import com.maoding.Notice.zeroc.NoticeService;
//import com.maoding.User.zeroc.AccountDTO;
//import org.junit.After;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.SpringBootConfiguration;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import java.util.List;
//
///**
//* NoticeServiceImpl Tester.
//*
//* @author Zhangchengliang
//* @since 01/03/2018
//* @version 1.0
//*/
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest
//@SpringBootConfiguration //only enable when target module hasn't @SpringBootApplication
//@ComponentScan(basePackages = {"com.maoding"}) //only enable when target module hasn't @SpringBootApplication
//@EnableAutoConfiguration
//public class NoticeServiceImplTest {
//    @Autowired
//    NoticeService noticeService;
////    NoticeServicePrx noticeServicePrx = NoticeServiceImpl.getInstance("NoticeServer;192.168.13.140");
//
//    @Test
//    public void testSendWebNotice() throws Exception{
//        NoticeRequestDTO request = new NoticeRequestDTO();
//        request.setUserId(getLocalAccount().getId());
//        request.setTypeIdString("5");
//        noticeService.sendNoticeForAccount(getLocalAccount(),request,null);
//    }
//
//    @Test
//    public void testSubscribe() throws Exception{
//        NoticeClientPrx client1 = NoticeClientImpl.createNewClient("127.0.0.1",getLocalAccount().getId());
//        noticeService.subscribeTopicForAccount(getLocalAccount(),client1,null);
//        List<String> topicList = noticeService.listSubscribedTopic(getLocalAccount().getId(),null);
//        Assert.assertNotNull(topicList);
//    }
//
//    private AccountDTO getLocalAccount(){
//        AccountDTO account = new AccountDTO();
//        account.setId("41d244733ec54f09a255836637f2b21d");
//        return account;
//    }
//
//    @Test
//    public void tesNotice() throws Exception {
//        MessageDTO msg = new MessageDTO();
//        msg.setUserId("user3");
//        msg.setTitle("title");
//        msg.setContent_Notice("message");
//
//        NoticeClientPrx client1 = NoticeClientImpl.createNewClient("192.168.13.140","1");
////        NoticeClientPrx client2 = NoticeClientImpl.createNewClient("192.168.13.140","2");
////        noticeService.subscribeTopic("User1",client1,null);
////        noticeService.subscribeTopic("User2",client2,null);
////
////        List<ReceiverDTO> list = new ArrayList<>();
////        noticeService.noticeToUser(msg,"1",null);
////        noticeService.unSubscribeTopic("User1",client1,null);
////        noticeService.unSubscribeTopic("User2",client2,null);
//
////        noticeServicePrx.noticeToUser(msg,"1");
////        noticeServicePrx.unSubscribeTopicForUser("1",client1);
////        noticeServicePrx.noticeToUser(msg,"13680809727");
//    }
//    /** for method: createTopic(String topic, Current current) */
//    @Test
//    public void testCreateTopic() throws Exception {
//        noticeService.createTopic("abcde",null);
//    }
//    /** for method: subscribeTopic(String topic, Current current) */
//    @Test
//    public void testSubscribeTopic() throws Exception {
//        //TODO: Test goes here...
//    }
//    /** for method: run() */
//    @Test
//    public void testRun() throws Exception {
//        //TODO: Test goes here...
//    }
//
//
//    /** action before each test */
//    @Before
//    public void before() throws Exception {
//    }
//
//    /** action after every test */
//    @After
//    public void after() throws Exception {
//    }
//}
