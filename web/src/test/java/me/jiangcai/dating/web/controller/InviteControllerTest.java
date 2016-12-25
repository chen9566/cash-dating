package me.jiangcai.dating.web.controller;

import me.jiangcai.dating.LoginWebTest;
import me.jiangcai.dating.page.CodePage;
import me.jiangcai.dating.page.MyPage;
import me.jiangcai.dating.repository.UserRepository;
import me.jiangcai.dating.service.AgentService;
import me.jiangcai.dating.service.QRCodeService;
import me.jiangcai.dating.service.StatisticService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.security.SignatureException;

/**
 * 合伙人已经从现有需求中排除
 * @author CJ
 */
public class InviteControllerTest extends LoginWebTest {

    @Autowired
    private StatisticService statisticService;
    @Autowired
    private QRCodeService qrCodeService;
    @Autowired
    private AgentService agentService;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private UserRepository userRepository;

    @Test
    public void invite() throws IOException, SignatureException {
        MyPage myPage = myPage();

        CodePage codePage = myPage.toCodePage();

        codePage.printThisPage();
    }


}