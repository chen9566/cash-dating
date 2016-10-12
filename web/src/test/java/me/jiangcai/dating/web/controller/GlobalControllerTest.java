package me.jiangcai.dating.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import me.jiangcai.dating.WebTest;
import me.jiangcai.dating.entity.SubBranchBank;
import me.jiangcai.dating.repository.SubBranchBankRepository;
import me.jiangcai.dating.service.BankService;
import me.jiangcai.dating.service.PayResourceService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;

import java.io.InputStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
public class GlobalControllerTest extends WebTest {

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private PayResourceService payResourceService;
    @Autowired
    private BankService bankService;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private SubBranchBankRepository subBranchBankRepository;

    @Test
    public void provinceList() throws Exception {
        String str = mockMvc.perform(get("/provinceList"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(org.springframework.http.MediaType.APPLICATION_JSON))
//                .andDo(print())
                .andReturn().getResponse().getContentAsString();

        JsonNode array = objectMapper.readTree(str);

        try (InputStream inputStream = applicationContext.getResource("/mock/provinces.json").getInputStream()) {
            assertSimilarJsonArray(array, inputStream);
        }

        SubBranchBank subBranchBank = subBranchBankRepository.findAll().stream()
                .max(new RandomComparator())
                .orElse(null);

        String strSubs = mockMvc.perform(
                get("/subBranchList")
                        .param("bankId", subBranchBank.getBank().getCode())
                        .param("cityId", subBranchBank.getCityCode())
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        JsonNode subArray = objectMapper.readTree(strSubs);
        try (InputStream inputStream = applicationContext.getResource("/mock/branches.json").getInputStream()) {
            assertSimilarJsonArray(subArray, inputStream);
        }
        // 然后是根据 市 和 银行 可以获取一个支行列表
    }

}