package me.jiangcai.dating.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import me.jiangcai.dating.WebTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

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
    }

}