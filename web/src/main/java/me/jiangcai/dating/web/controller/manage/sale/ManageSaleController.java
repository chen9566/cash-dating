package me.jiangcai.dating.web.controller.manage.sale;

import me.jiangcai.dating.DataField;
import me.jiangcai.dating.core.Login;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.sale.CashGoods;
import me.jiangcai.dating.entity.sale.FakeGoods;
import me.jiangcai.dating.entity.sale.TicketGoods;
import me.jiangcai.dating.repository.sale.CashGoodsRepository;
import me.jiangcai.dating.service.DataService;
import me.jiangcai.dating.service.QRCodeService;
import me.jiangcai.dating.service.sale.MallGoodsService;
import me.jiangcai.goods.service.ManageGoodsService;
import me.jiangcai.lib.resource.service.ResourceService;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.imageio.ImageIO;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author CJ
 */
@Controller
@RequestMapping("/manage/goods")
public class ManageSaleController {

    @Autowired
    private DataService data;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private CashGoodsRepository cashGoodsRepository;
    @Autowired
    private QRCodeService qrCodeService;
    @Autowired
    private MallGoodsService mallGoodsService;
    @Autowired
    private ManageGoodsService manageGoodsService;

    // 编辑商品
    @RequestMapping(method = RequestMethod.POST, value = {"", "/"})
    @PreAuthorize("hasAnyRole('ROOT','" + Login.Role_Sale_Goods_Value + "')")
    @Transactional
    public String update(long goodsId, TicketGoods goods) {
        TicketGoods goods1 = (TicketGoods) mallGoodsService.findGoods(goodsId);
        goods1.setNotes(goods.getNotes());
        goods1.setSubPrice(goods.getSubPrice());
        goods1.setRichDetail(goods.getRichDetail());
        goods1.setPrice(goods.getPrice());
        goods1.setBrand(goods.getBrand());
        goods1.setDescription(goods.getDescription());
        goods1.setName(goods.getName());
        return "redirect:/manage/goods";
    }

    @RequestMapping(method = RequestMethod.GET, value = {"", "/"})
    @PreAuthorize("hasAnyRole('ROOT','" + Login.Role_Sale_Goods_Value + "','" + Login.Role_Sale_Goods_Read_Value + "')")
    public String index() {
        return "manage/sale/goods.html";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PreAuthorize("hasAnyRole('ROOT','" + Login.Role_Sale_Goods_Value + "','" + Login.Role_Sale_Goods_Read_Value + "')")
    @Transactional(readOnly = true)
    @ResponseBody
    public Object data(@AuthenticationPrincipal User user, String search, String sort, Sort.Direction order
            , int offset, int limit) {
        return data.data(user, search, sort, order, offset, limit, CashGoods.class, fieldList(), null);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{goodsId}/enable")
    @PreAuthorize("hasAnyRole('ROOT','" + Login.Role_Sale_Goods_Value + "')")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void enable(@RequestBody String to, @PathVariable("goodsId") long goodsId) {
        CashGoods goods = cashGoodsRepository.getOne(goodsId);
        boolean enable = BooleanUtils.toBoolean(to);
        if (enable == goods.isEnable())
            return;
        if (enable)
            manageGoodsService.enableGoods(goods, Function.identity());
        else {
            goods.setEnable(false);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/batch")
    @PreAuthorize("hasAnyRole('ROOT','" + Login.Role_Sale_Goods_Value + "','" + Login.Role_Sale_Stock_Value + "')")
    @Transactional
    public String batch(@AuthenticationPrincipal User user, RedirectAttributes redirectAttributes, long goodsId
            , @RequestParam LocalDate expiredDate, String comment, MultipartFile file) throws IOException {
        //首先解析文件
        File tmpFile = File.createTempFile("cashGoods", ".zip");
        try {
            ArrayList<String> codes = new ArrayList<>();
            try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(tmpFile))) {
                try (ZipInputStream zipInputStream = new ZipInputStream(file.getInputStream())) {
                    while (true) {
                        ZipEntry entry = zipInputStream.getNextEntry();
                        if (entry == null)
                            break;
                        //内存中操作
                        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                        StreamUtils.copy(zipInputStream, buffer);
                        zipInputStream.closeEntry();
                        BufferedImage image = ImageIO.read(new ByteArrayInputStream(buffer.toByteArray()));
                        if (image == null)
                            continue;
                        try {
                            String code = qrCodeService.scanImage(image);
                            codes.add(code);
                        } catch (IllegalArgumentException ex) {
                            zipOutputStream.putNextEntry(entry);
                            StreamUtils.copy(new ByteArrayInputStream(buffer.toByteArray()), zipOutputStream);
                            zipOutputStream.closeEntry();
                            zipOutputStream.flush();
                        }
                    }
                }
            }

            TicketGoods goods = (TicketGoods) cashGoodsRepository.getOne(goodsId);
            mallGoodsService.addTicketBatch(user, goods, expiredDate, comment, codes);

            redirectAttributes.addFlashAttribute("batchedGoods", goods);
            redirectAttributes.addFlashAttribute("count", codes.size());
            if (tmpFile.length() > 0) {
                LocalDateTime now = LocalDateTime.now();
                String path = "batchFailed/" + now.format(DateTimeFormatter.ofPattern("y-M-d-H-m-s")) + ".zip";
                try (FileInputStream fileInputStream = new FileInputStream(tmpFile)) {
                    redirectAttributes.addFlashAttribute("failedUrl", resourceService.uploadResource(path, fileInputStream).httpUrl());
                }
            }

            return "redirect:/manage/goods";
        } finally {
            //noinspection ResultOfMethodCallIgnored
            tmpFile.delete();
        }

    }

    private List<DataField> fieldList() {
        return Arrays.asList(
                new DataService.NumberField("id", Long.class)
//                , new DataService.StringField("imageUrl") {
//                    @Override
//                    protected Expression<?> selectExpression(Root<?> root) {
//                        return root.get("goodsImages");
//                    }
//
//                    @Override
//                    public Object export(Object origin, MediaType type) {
//                        if (origin instanceof GoodsImage){
//                            return resourceService.getResource(((GoodsImage) origin).getDefaultImage().getResourcePath()).httpUrl();
//                        }
//
//                        if (origin instanceof List){
//                            GoodsImage goodsImage = (GoodsImage) ((List) origin).get(0);
//                            return resourceService.getResource(goodsImage.getDefaultImage().getResourcePath()).httpUrl().toString();
//                        }
//
//                        return origin.toString();
//                    }
//                }
                , new DataService.BooleanField("enable")
                , new DataService.StringField("brand")
                , new DataService.StringField("name")
                , new DataService.StringField("description")
                , new DataService.NumberField("price", BigDecimal.class)
                , new DataService.StringField("subPrice")
                , new DataService.StringField("richDetail")
                , new DataService.StringField("notes") {
                    @SuppressWarnings("unchecked")
                    @Override
                    public Selection<?> select(CriteriaBuilder builder, CriteriaQuery<?> query, Root<?> root) {
                        return builder.treat((Root) root, TicketGoods.class).get("notes");
                    }
                }
                , new DataService.NumberField("stock", Long.class) {
                    @Override
                    public Selection<?> select(CriteriaBuilder builder, CriteriaQuery<?> query, Root<?> root) {
// 目前只支持 ticketGoods
//                        @SuppressWarnings("unchecked")
//                        Root<TicketGoods> ticketGoodsRoot = builder.treat((Root) root, TicketGoods.class);

                        return CashGoods.StockLeftExpression(builder, query, root);
                    }
                }, new DataService.StringField("type") {
                    @Override
                    protected Expression<?> selectExpression(Root<?> root) {
                        return root.type();
                    }

                    @Override
                    public Object export(Object origin, MediaType type) {
                        if (TicketGoods.class.equals(origin))
                            return "卡券类";
                        if (FakeGoods.class.equals(origin))
                            return "伪类";
                        return "unknown";
                    }
                }, new DataService.StringField("javaType") {
                    @Override
                    protected Expression<?> selectExpression(Root<?> root) {
                        return root.type();
                    }

                    @Override
                    public Object export(Object origin, MediaType type) {
                        Class clazz = (Class) origin;
                        return clazz.getSimpleName();
                    }
                }
//                , new DataService.BooleanField("isTicket") {
//                    @Override
//                    public Selection<?> select(CriteriaBuilder builder, CriteriaQuery<?> query, Root<?> root) {
//                        return builder.equal(root.type(), TicketGoods.class);
//                    }
//                }
        );
    }

}
