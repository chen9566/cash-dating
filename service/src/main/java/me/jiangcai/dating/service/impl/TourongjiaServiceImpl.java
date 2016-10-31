package me.jiangcai.dating.service.impl;

import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.model.trj.ApplyLoan;
import me.jiangcai.dating.model.trj.Loan;
import me.jiangcai.dating.model.trj.LoanStatus;
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
import java.math.BigDecimal;
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

    // 理财
    private final String urlRoot1;
    // 借款
    private final String urlRoot2;
    // www
    private final String urlRoot3;
    private final String tenant;
    private final String key;


    @Autowired
    public TourongjiaServiceImpl(Environment environment) {
        urlRoot1 = environment.getProperty("me.jiangcai.dating.tourongjia.url1", "https://escrow.tourongjia.com/");
        urlRoot2 = environment.getProperty("me.jiangcai.dating.tourongjia.url2", "http://escrowcrm1.tourongjia.com/");
        urlRoot3 = environment.getProperty("me.jiangcai.dating.tourongjia.url3", "https://wapescrow.tourongjia.com/");
        tenant = environment.getProperty("me.jiangcai.dating.tourongjia.tenant", "yuntao");
        key = environment.getProperty("me.jiangcai.dating.tourongjia.key", "1234567890");
        //
    }

    private static String sign(final Map<String, String> params, String secretKey) {

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
            HttpGet get = new1Get("ApiServer/Tenant/getToken", new BasicNameValuePair("mobile", "13600000033"));
//            HttpGet get = new1Get("ApiServer/Tenant/recommendBid", new BasicNameValuePair("mobile", "13600000033"));
//            HttpGet get = new HttpGet(urlRoot1 + "ApiServer/Tenant/recommendBid");
            String code = client.execute(get, new BasicResponseHandler());
            System.out.println(code);
        }
        // /ApiServer/Tenant/recommendBid
        return null;
    }

    @Override
    public Loan[] loanList() throws IOException {
        try (CloseableHttpClient client = requestClient()) {
            HttpGet get = new2Get("tenant/yt_listProduct.jhtml");
            return client.execute(get, new TRJJsonHandler<>(Loan[].class));
        }
    }

    @Override
    public String loan(Loan loan, String term, User user, String name, BigDecimal amount, String province, String city
            , String address) throws IOException {
        try (CloseableHttpClient client = requestClient()) {
            HttpGet get = new2Get("tenant/yt_applyLoan.jhtml"
                    , new BasicNameValuePair("applyLoan.name", name)
                    , new BasicNameValuePair("applyLoan.mobile", user.getMobileNumber())
                    , new BasicNameValuePair("applyLoan.productId", loan.getProductId())
                    , new BasicNameValuePair("applyLoan.amount", amount.toString())
                    , new BasicNameValuePair("applyLoan.province", province)
                    , new BasicNameValuePair("applyLoan.city", city)
                    , new BasicNameValuePair("applyLoan.address", address)
                    , new BasicNameValuePair("applyLoan.term", term)
            );
            return client.execute(get, new TRJJsonHandler<>(ApplyLoan[].class))[0].getApplyId();
        }
    }

    @Override
    public String checkLoanStatus(String id) throws IOException {
        try (CloseableHttpClient client = requestClient()) {
            HttpGet get = new2Get("tenant/yt_applyStatus.jhtml"
                    , new BasicNameValuePair("applyId", id)
            );
            return client.execute(get, new TRJJsonHandler<>(LoanStatus[].class))[0].getStatus();
        }
    }

    private HttpGet new1Get(String uri, NameValuePair... pairs) {
        return newGet(urlRoot1, uri, pairs);
    }

    private HttpGet new2Get(String uri, NameValuePair... pairs) {
        return newGet(urlRoot2, uri, pairs);
    }

    private HttpGet newGet(String urlRoot, String uri, NameValuePair[] pairs) {
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
