package me.jiangcai.dating.web.controller.manage.sale;

import me.jiangcai.dating.AsManage;
import me.jiangcai.dating.ManageWebTest;
import me.jiangcai.dating.entity.sale.CashGoods;
import me.jiangcai.dating.entity.support.ManageStatus;
import me.jiangcai.dating.page.sale.ManageGoodsPage;
import me.jiangcai.dating.repository.sale.CashGoodsRepository;
import me.jiangcai.dating.web.converter.LocalDateFormatter;
import me.jiangcai.goods.Seller;
import me.jiangcai.goods.TradeEntity;
import net.minidev.json.JSONArray;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.util.StreamUtils;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
@AsManage(ManageStatus.general)
public class ManageSaleControllerTest extends ManageWebTest {
    @Autowired
    private CashGoodsRepository cashGoodsRepository;
    @Autowired
    private LocalDateFormatter localDateFormatter;

    @Test
    public void index() throws Exception {
        driver.get("http://localhost/manage/goods");
        ManageGoodsPage page = initPage(ManageGoodsPage.class);
    }

//    @Autowired
//    private EntityManager entityManager;

    @Test
    public void data() throws Exception {

        final int count = random.nextInt(200) + 1;
        CashGoods cashGoods = addSimpleTicketGoodsWithBatch(count);

        // 先看下函数可否运行
//        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
//        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
//        Root<?> root = query.from(CashGoods.class);
//        query = query.select(TicketGoods.StockLeftExpression(criteriaBuilder, query, root));
//        List list = entityManager.createQuery(query).getResultList();
//        System.out.println(list);


        MockHttpSession session = mvcLogin();
        assertDataResult(count, cashGoods, session);

        LocalDate expireDate = LocalDate.now().plusMonths(2);

        String location = mockMvc.perform(fileUpload("/manage/goods/batch")
                .file("file", StreamUtils.copyToByteArray(applicationContext.getResource("classpath:/TicketBatchSAndF.zip").getInputStream()))
                .param("goodsId", String.valueOf(cashGoods.getId()))
                .param("expiredDate", localDateFormatter.print(expireDate, null))
                .session(session)
        )
                .andExpect(status().isFound())
                .andReturn().getResponse().getHeader("Location");

        mockMvc.perform(get(location).session(session))
                .andDo(MockMvcResultHandlers.print());// 包括成功的结果 以及失败的文件下载地址!

        assertDataResult(count + 1, cashGoods, session);

        mockMvc.perform(put("/manage/goods/" + cashGoods.getId() + "/enable")
                .session(session)
                .contentType(MediaType.TEXT_PLAIN)
                .content("false")
        )
                .andExpect(status().isNoContent());

        assertThat(cashGoodsRepository.getOne(cashGoods.getId()).isEnable())
                .isFalse();

        mockMvc.perform(put("/manage/goods/" + cashGoods.getId() + "/enable")
                .session(session)
                .contentType(MediaType.TEXT_PLAIN)
                .content("true")
        )
                .andExpect(status().isNoContent());

        assertThat(cashGoodsRepository.getOne(cashGoods.getId()).isEnable())
                .isTrue();

        // 修改商品
        CashGoods goodsData = randomGoodsData();
        mockMvc.perform(post("/manage/goods")
                .session(session)
                .param("goodsId", String.valueOf(cashGoods.getId()))
                .param("name", goodsData.getName())
                .param("brand", goodsData.getBrand())
                .param("description", goodsData.getDescription())
                .param("subPrice", goodsData.getSubPrice())
                .param("richDetail", goodsData.getRichDetail())
                .param("price", goodsData.getPrice().toString())
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isFound());

        cashGoods = mallGoodsService.findGoods(cashGoods.getId());
        assertThat(cashGoods.getName()).isEqualToIgnoringCase(goodsData.getName());
        assertThat(cashGoods.getBrand()).isEqualToIgnoringCase(goodsData.getBrand());
        assertThat(cashGoods.getSubPrice()).isEqualToIgnoringCase(goodsData.getSubPrice());
        assertThat(cashGoods.getDescription()).isEqualToIgnoringCase(goodsData.getDescription());
        assertThat(cashGoods.getRichDetail()).isEqualToIgnoringCase(goodsData.getRichDetail());
        assertThat(cashGoods.getPrice()).isEqualByComparingTo(goodsData.getPrice());
    }

    private CashGoods randomGoodsData() {
        CashGoods goods = new CashGoods() {
            @Override
            public Seller getSeller() {
                return null;
            }

            @Override
            public void setSeller(Seller seller) {

            }

            @Override
            public TradeEntity getOwner() {
                return null;
            }

            @Override
            public void setOwner(TradeEntity owner) {

            }

            @Override
            public boolean isTicketGoods() {
                return false;
            }
        };
        goods.setName(UUID.randomUUID().toString());
        goods.setBrand(UUID.randomUUID().toString());
        goods.setDescription(UUID.randomUUID().toString());
        goods.setSubPrice(UUID.randomUUID().toString().substring(0, 25));
        goods.setPrice(randomOrderAmount());
        goods.setRichDetail(UUID.randomUUID().toString());
        return goods;
    }

    private void assertDataResult(final int count, CashGoods cashGoods, MockHttpSession session) throws Exception {
        final String stockExpression = "$.rows[?(@.id==" + cashGoods.getId() + ")].stock";
        System.out.println(stockExpression);
        mockMvc.perform(getWeixin("/manage/goods/data").session(session)
//                .param("search", newUser.getNickname())
                .param("offset", "0")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(simliarDataJsonAs("/mock/goods.json"))
                .andExpect(jsonPath("$.rows").isArray())
                .andExpect(jsonPath(stockExpression).value(new Matcher<Object>() {
                    @Override
                    public boolean matches(Object item) {
                        JSONArray array = (JSONArray) item;
                        return array.contains(count);
                    }

                    @Override
                    public void describeMismatch(Object item, Description mismatchDescription) {

                    }

                    @Override
                    public void _dont_implement_Matcher___instead_extend_BaseMatcher_() {

                    }

                    @Override
                    public void describeTo(Description description) {

                    }
                }))
                .andExpect(jsonPath("$.total").isNumber())
                .andExpect(jsonPath("$.total").value((int) cashGoodsRepository.count()));
    }

}