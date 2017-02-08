package me.jiangcai.dating.web.controller.manage.sale;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.jiangcai.dating.core.Login;
import me.jiangcai.dating.entity.sale.CashGoods;
import me.jiangcai.dating.service.sale.MallGoodsService;
import me.jiangcai.goods.core.entity.SimpleGoodsImage;
import me.jiangcai.goods.image.ImageUsage;
import me.jiangcai.goods.image.ScaledImage;
import me.jiangcai.lib.resource.service.ResourceService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author CJ
 */
@Controller
@PreAuthorize("hasAnyRole('ROOT','" + Login.Role_Sale_Goods_Value + "','" + Login.Role_Sale_Goods_Read_Value + "')")
@RequestMapping("/manage/goods/images")
public class GoodsImagesController {
    private static final Log log = LogFactory.getLog(GoodsImagesController.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MallGoodsService mallGoodsService;
    @Autowired
    private ResourceService resourceService;

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}/{uuid}")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable("id") long id
            , @PathVariable("uuid") long uuid) throws IOException {
        final CashGoods goods = mallGoodsService.findGoods(id);
        findImage(uuid, goods)
                .ifPresent(simpleGoodsImage -> {
                    goods.getGoodsImages().remove(simpleGoodsImage);
                    simpleGoodsImage.getImageSet().forEach(image -> {
                        try {
                            resourceService.deleteResource(image.getResourcePath());
                        } catch (IOException e) {
                            throw new InternalError(e);
                        }
                    });
                });
    }

    private Optional<SimpleGoodsImage> findImage(long uuid, CashGoods goods) {
        return goods.getGoodsImages()
                .stream()
                .filter(simpleGoodsImage -> simpleGoodsImage.getId() == uuid)
                .findAny();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}/{uuid}")
    @Transactional(readOnly = true)
    public ResponseEntity getItem(@PathVariable("id") long id
            , @PathVariable("uuid") long uuid) throws IOException {
        final CashGoods goods = mallGoodsService.findGoods(id);
        SimpleGoodsImage goodsImage = findImage(uuid, goods).orElse(null);
        if (goodsImage == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity
                .ok()
//                .contentType(item.toContentType())
                .body(StreamUtils.copyToByteArray(resourceService.getResource(goodsImage.getDefaultImage().getResourcePath()).getInputStream()));
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseBody
    public Object postItems(@PathVariable("id") long id
            , MultipartFile qqfile, @RequestParam String qqfilename) throws IOException {


//        if (StringUtils.isEmpty(qqfilename))
//            throw new IllegalArgumentException("qqfilename is required.");
        if (qqfile.isEmpty())
            throw new IllegalArgumentException("qqfile is required.");

        final CashGoods goods = mallGoodsService.findGoods(id);

        SimpleGoodsImage image = new SimpleGoodsImage();
        image.setDescription(qqfilename);
        ScaledImage scaledImage = new ScaledImage();
        int dotIndex = qqfile.getOriginalFilename().lastIndexOf('.');
        scaledImage.setFormat(qqfile.getOriginalFilename().substring(dotIndex).toUpperCase(Locale.ENGLISH));
        String newPath = "goods_images/" + UUID.randomUUID().toString() + scaledImage.getFormat();
        try (InputStream inputStream = qqfile.getInputStream()) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            StreamUtils.copy(inputStream, buffer);
            BufferedImage image1 = ImageIO.read(new ByteArrayInputStream(buffer.toByteArray()));
            scaledImage.setHeight(image1.getHeight());
            scaledImage.setWidth(image1.getWidth());
            scaledImage.setUsage(ImageUsage.preview);
            resourceService.uploadResource(newPath, new ByteArrayInputStream(buffer.toByteArray()));
            scaledImage.setResourcePath(newPath);
        }

        image.addScaledImage(scaledImage);

        goods.getGoodsImages().add(image);

        HashMap<String, Object> body = new HashMap<>();
        body.put("success", true);
//        body.put("newUuid", String.valueOf(item.getId()));
        return body;
    }


    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    public ResponseEntity<String> getItems(@PathVariable("id") long id) throws JsonProcessingException {

        final CashGoods goods = mallGoodsService.findGoods(id);


        List<Map<String, Object>> list = goods.getGoodsImages().stream()
                .map((Function<SimpleGoodsImage, Map<String, Object>>) galleryItem -> {
                    HashMap<String, Object> data = new HashMap<>();
                    data.put("serial", String.valueOf(galleryItem.getId()));
                    data.put("name", galleryItem.getDescription());
                    data.put("uuid", String.valueOf(galleryItem.getId()));
                    try {
                        data.put("size", resourceService.getResource(galleryItem.getDefaultImage().getResourcePath()).contentLength());
                    } catch (IOException e) {
                        // 这个错误并不重要
                        log.warn("", e);
                    }
//                    data.put("thumbnailUrl",
//                            "/"
//                                    + servletContext.getContextPath()
//                                    + "manage/gallery/" + galleryItem.getGallery().getId()
//                                    + "/items/"
//                                    + galleryItem.getId());
                    try {
                        data.put("thumbnailUrl",
                                resourceService.getResource(galleryItem.getDefaultImage().getResourcePath()).httpUrl().toString());
                    } catch (IOException ignored) {
                        // never
                        log.fatal("", ignored);
                    }

                    // thumbnailUrl 考虑到跨域策略,这里给出本域的一个地址
                    return data;
                }).collect(Collectors.toList());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(objectMapper.writeValueAsString(list));
    }


}
