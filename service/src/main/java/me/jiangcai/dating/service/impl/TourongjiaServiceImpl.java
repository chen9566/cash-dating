package me.jiangcai.dating.service.impl;

import me.jiangcai.dating.service.TourongjiaService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author CJ
 */
@Service
public class TourongjiaServiceImpl implements TourongjiaService {

    private final String urlRoot;
    private final String tenant;
    private final String key;

    @Autowired
    public TourongjiaServiceImpl(Environment environment) {
        urlRoot = environment.getProperty("me.jiangcai.dating.tourongjia.url", "https://escrow.tourongjia.com/");
        tenant = environment.getProperty("me.jiangcai.dating.tourongjia.tenant", "yuntao");
        key = environment.getProperty("me.jiangcai.dating.tourongjia.key", "1234567890");
        //
    }

    public static String sign(final Map<String, String> params, String secretKey) {

        if (StringUtils.isBlank(secretKey)) {
            throw new IllegalArgumentException("secretKey not bank.");
        }

        StringBuilder sb = new StringBuilder();

        if (params != null && params.size() > 0) {

            List<String> nameList = new ArrayList<>();

            for (Iterator<String> it = params.keySet().iterator(); it.hasNext(); ) {
                String key = it.next();
                nameList.add(key);
            }

            Collections.sort(nameList);

            for (String name : nameList) {
                if (sb.length() > 0) {
                    sb.append("&");
                }
                sb.append(name).append("=").append(params.get(name));
            }

        }

        sb.append(secretKey);

        return DigestUtils.md5Hex(sb.toString());
    }

    @Override
    public Object recommend() throws IOException {
        try (CloseableHttpClient client = requestClient()) {
            HttpGet get = newGet("ApiServer/Tenant/getToken", new BasicNameValuePair("mobile", "13600000033"));
//            HttpGet get = newGet("ApiServer/Tenant/recommendBid", new BasicNameValuePair("mobile", "13600000033"));
//            HttpGet get = new HttpGet(urlRoot + "ApiServer/Tenant/recommendBid");
            String code = client.execute(get, new BasicResponseHandler());
            System.out.println(code);
        }
        // /ApiServer/Tenant/recommendBid
        return null;
    }

    private HttpGet newGet(String uri, NameValuePair... pairs) {
        List<NameValuePair> list = new ArrayList<>();
        list.addAll(Arrays.asList(pairs));
        list.add(new BasicNameValuePair("tenant", tenant));
        list.add(new BasicNameValuePair("sign", sign(list)));
        // 串接
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(urlRoot).append(uri).append("?");
        list.forEach(nameValuePair -> {
            try {
                urlBuilder.append(nameValuePair.getName()).append("=").append(URLEncoder.encode(nameValuePair.getValue(), "UTF-8"))
                        .append("&");
            } catch (UnsupportedEncodingException e) {
                throw new InternalError(e);
            }
        });

        urlBuilder.setLength(urlBuilder.length() - 1);
        return new HttpGet(urlBuilder.toString());
    }

    private String sign(List<NameValuePair> list) {
        HashMap<String, String> data = new HashMap<>();
        list.forEach(nameValuePair -> data.put(nameValuePair.getName(), nameValuePair.getValue()));
        return sign(data, key);
    }

    private CloseableHttpClient requestClient() {
        return HttpClientBuilder.create().build();
    }
}
