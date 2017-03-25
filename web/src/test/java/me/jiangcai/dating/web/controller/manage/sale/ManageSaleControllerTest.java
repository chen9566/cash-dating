package me.jiangcai.dating.web.controller.manage.sale;

import com.jayway.jsonpath.JsonPath;
import me.jiangcai.dating.AsManage;
import me.jiangcai.dating.ManageWebTest;
import me.jiangcai.dating.entity.sale.CashGoods;
import me.jiangcai.dating.entity.sale.FakeGoods;
import me.jiangcai.dating.entity.sale.TicketGoods;
import me.jiangcai.dating.entity.support.ManageStatus;
import me.jiangcai.dating.page.sale.ManageGoodsPage;
import me.jiangcai.dating.repository.mall.FakeGoodsRepository;
import me.jiangcai.dating.repository.sale.CashGoodsRepository;
import me.jiangcai.dating.web.converter.LocalDateFormatter;
import me.jiangcai.goods.Seller;
import me.jiangcai.goods.TradeEntity;
import net.minidev.json.JSONArray;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.util.StreamUtils;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author CJ
 */
@AsManage(ManageStatus.general)
public class ManageSaleControllerTest extends ManageWebTest {
    @Autowired
    private CashGoodsRepository cashGoodsRepository;
    @Autowired
    private LocalDateFormatter localDateFormatter;
    @Autowired
    private FakeGoodsRepository fakeGoodsRepository;

//    @Autowired
//    private EntityManager entityManager;

    @Test
    public void index() throws Exception {
        driver.get("http://localhost/manage/goods");
        ManageGoodsPage page = initPage(ManageGoodsPage.class);
    }

    @Test
    public void data() throws Exception {

        // 添加随机库存量的 卡券类商品
        final int count = random.nextInt(200) + 1;
        TicketGoods ticketGoods = (TicketGoods) addSimpleTicketGoodsWithBatch(count);

        // 先看下函数可否运行
//        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
//        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
//        Root<?> root = query.from(CashGoods.class);
//        query = query.select(TicketGoods.StockLeftExpression(criteriaBuilder, query, root));
//        List list = entityManager.createQuery(query).getResultList();
//        System.out.println(list);


        MockHttpSession session = mvcLogin();
        // 添加一个伪类商品
        FakeGoods fakeGoods = addRandomFakeGoods(session);
        // 设定它的库存


        // 检查库存量是否符合
        assertDataResult(count, ticketGoods, session);
        assertDataResult(0, fakeGoods, session);

        LocalDate expireDate = LocalDate.now().plusMonths(2);

        String location = mockMvc.perform(fileUpload("/manage/goods/batch")
                .file("file", StreamUtils.copyToByteArray(applicationContext.getResource("classpath:/TicketBatchSAndF.zip").getInputStream()))
                .param("goodsId", String.valueOf(ticketGoods.getId()))
                .param("expiredDate", localDateFormatter.print(expireDate, null))
                .session(session)
        )
                .andExpect(status().isFound())
                .andReturn().getResponse().getHeader("Location");

        mockMvc.perform(get(location).session(session))
                .andDo(MockMvcResultHandlers.print());// 包括成功的结果 以及失败的文件下载地址!

        assertDataResult(count + 1, ticketGoods, session);

        mockMvc.perform(put("/manage/goods/" + ticketGoods.getId() + "/enable")
                .session(session)
                .contentType(MediaType.TEXT_PLAIN)
                .content("false")
        )
                .andExpect(status().isNoContent());

        assertThat(cashGoodsRepository.getOne(ticketGoods.getId()).isEnable())
                .isFalse();

        mockMvc.perform(put("/manage/goods/" + ticketGoods.getId() + "/enable")
                .session(session)
                .contentType(MediaType.TEXT_PLAIN)
                .content("true")
        )
                .andExpect(status().isNoContent());

        assertThat(cashGoodsRepository.getOne(ticketGoods.getId()).isEnable())
                .isTrue();

        // 修改商品
        CashGoods goodsData = randomGoodsData();
        mockMvc.perform(post("/manage/goods")
                .session(session)
                .param("goodsId", String.valueOf(ticketGoods.getId()))
                .param("name", goodsData.getName())
                .param("brand", goodsData.getBrand())
                .param("description", goodsData.getDescription())
                .param("subPrice", goodsData.getSubPrice())
                .param("richDetail", goodsData.getRichDetail())
                .param("price", goodsData.getPrice().toString())
        )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isFound());

        ticketGoods = (TicketGoods) mallGoodsService.findGoods(ticketGoods.getId());
        assertThat(ticketGoods.getName()).isEqualToIgnoringCase(goodsData.getName());
        assertThat(ticketGoods.getBrand()).isEqualToIgnoringCase(goodsData.getBrand());
        assertThat(ticketGoods.getSubPrice()).isEqualToIgnoringCase(goodsData.getSubPrice());
        assertThat(ticketGoods.getDescription()).isEqualToIgnoringCase(goodsData.getDescription());
        assertThat(ticketGoods.getRichDetail()).isEqualToIgnoringCase(goodsData.getRichDetail());
        assertThat(ticketGoods.getPrice()).isEqualByComparingTo(goodsData.getPrice());

        // 处理图片
        int oldSize = JsonPath.read(mockMvc.perform(get("/manage/goods/images/" + ticketGoods.getId())
                        .session(session))
//                .andDo(MockMvcResultHandlers.print())
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)).andReturn().getResponse().getContentAsString()
                , "$.length()");

        // 上传一张新的
        mockMvc.perform(fileUpload("/manage/goods/images/" + ticketGoods.getId())
                .file(new MockMultipartFile("qqfile", "thumbnail.png", "image/png", new ClassPathResource("/thumbnail.png").getInputStream()))
                .param("qqfilename", UUID.randomUUID().toString())
                .session(session))
//                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        String newImageId = JsonPath.read(mockMvc.perform(get("/manage/goods/images/" + ticketGoods.getId())
                .session(session))
//                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(oldSize + 1))
                .andReturn().getResponse().getContentAsString(), "$[0].uuid");

        mockMvc.perform(delete("/manage/goods/images/" + ticketGoods.getId() + "/" + newImageId)
                .session(session)
        )
//                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/manage/goods/images/" + ticketGoods.getId())
                .session(session))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(oldSize));
    }

    /**
     * 通过MVC创建一个虚拟的fakeGoods
     *
     * @param session
     * @return
     */
    private FakeGoods addRandomFakeGoods(MockHttpSession session) throws Exception {
        redirectTo(mockMvc.perform(post("/manage/goods/addition")
                .session(session)
                .param("javaClass", "FakeGoods")
                .param("name", UUID.randomUUID().toString())
                .param("price", randomOrderAmount().toString())
        ), session)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML));

        return fakeGoodsRepository.findAll(new Sort(Sort.Direction.DESC, "id")).get(0);
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

            @Override
            protected void moreModel(Map<String, Object> data) {

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
        final String thisData = "$.rows[?(@.id==" + cashGoods.getId() + ")]";
        final String stockExpression = thisData + ".stock";
        // 还应当取出该数据，并且根据type 验证详情字段
        final String contentAsString = mockMvc.perform(get("/manage/goods/data").session(session)
//                .param("search", newUser.getNickname())
                .param("offset", "0")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(similarDataJsonAs("/mock/goods.json"))
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
                .andExpect(jsonPath("$.total").value((int) cashGoodsRepository.count()))
                .andReturn().getResponse().getContentAsString();


        String type = (String) ((JSONArray) JsonPath.read(
                contentAsString, thisData + ".javaType"
        )).get(0);
        String url = (String) ((JSONArray) JsonPath.read(
                contentAsString, thisData + ".morePropertiesUrl"
        )).get(0);

        if (url != null) {
            mockMvc.perform(get(url).session(session).accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(similarJsonAs("/mock/goods_" + type + ".json"));
        }


    }

}