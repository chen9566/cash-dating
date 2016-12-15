package me.jiangcai.dating.service.impl;

import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.model.trj.ApplyLoan;
import me.jiangcai.dating.model.trj.Financing;
import me.jiangcai.dating.model.trj.FinancingId;
import me.jiangcai.dating.model.trj.Loan;
import me.jiangcai.dating.model.trj.LoanContract;
import me.jiangcai.dating.model.trj.LoanStatus;
import me.jiangcai.dating.model.trj.LoanStatusResult;
import me.jiangcai.dating.model.trj.MobileToken;
import me.jiangcai.dating.model.trj.VerifyCodeSentException;
import me.jiangcai.dating.service.CashStrings;
import me.jiangcai.dating.service.TourongjiaService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URI;
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

    private static final Log log = LogFactory.getLog(TourongjiaServiceImpl.class);

    private static final RequestConfig defaultRequestConfig = RequestConfig.custom()
            .setConnectTimeout(30000)
            .setConnectionRequestTimeout(30000)
            .setSocketTimeout(30000).build();

    // 理财
    private final String urlRoot1;
    // 借款
    private final String urlRoot2;
    // www
    private final String urlRoot3;
    private final String tenant;
    private final String key;
    private final Environment environment;


    @Autowired
    public TourongjiaServiceImpl(Environment environment) {
        this.environment = environment;
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
    public MobileToken token(String mobile) throws IOException {
        try (CloseableHttpClient client = requestClient()) {
            HttpGet get = new1Get("ApiServer/Tenant/getToken", new BasicNameValuePair("mobile", mobile));
            return client.execute(get, new TRJJsonHandler<>(MobileToken.class));
        }
    }

    @Override
    public void sendCode(MobileToken token) throws IOException {
        try (CloseableHttpClient client = requestClient()) {
            HttpGet get = new1Get("ApiServer/Tenant/verifyCode"
                    , new BasicNameValuePair("mobile", token.getMobile())
//                    , new BasicNameValuePair("token", token.getToken())
            );
            client.execute(get, new TRJJsonHandler<>());
        }
    }

    @Override
    public void bind(String mobile, String code) throws IOException {
        try (CloseableHttpClient client = requestClient()) {
            HttpGet get = new1Get("ApiServer/Tenant/bind"
                    , new BasicNameValuePair("mobile", mobile)
                    , new BasicNameValuePair("verifyCode", code)
            );
            client.execute(get, new TRJJsonHandler<>());
        }
    }

//    /ApiServer/Tenant/investSeriesBid
//参数名	备注
//    tenant	yuntao
//    minTerm	最小期限
//    maxTerm	最大期限
//    termUnit  期限单位 day,month,year
//    minRate	最小利率 80 means 8% 不带小数,*1000
//    maxRate	最大利率
//    sign	对参数签名

    @Override
    public Financing randomFinancing() throws IOException {
        try (CloseableHttpClient client = requestClient()) {
            HttpGet get = new1Get("ApiServer/Tenant/investSeriesBid"
                    , new BasicNameValuePair("termUnit", "month")
                    , new BasicNameValuePair("minTerm", "1")
                    , new BasicNameValuePair("maxTerm", "24")
                    , new BasicNameValuePair("minRate", "10")
                    , new BasicNameValuePair("maxRate", "300")
            );
            Financing financing = new Financing();
            financing.setId(client.execute(get, new TRJJsonHandler<>(FinancingId.class)).getPrjId());
            return financing;
        }
    }

    @Override
    public Financing recommend() throws IOException {
        try (CloseableHttpClient client = requestClient()) {
            HttpGet get = new1Get("ApiServer/Tenant/recommendBid");
            return client.execute(get, new TRJJsonHandler<>(Financing.class));
        }
    }
    //////////////////////////

    @Override
    public Loan[] loanList() throws IOException {
        try (CloseableHttpClient client = requestClient()) {
            HttpGet get = new2Get("tenant/yt_listProduct.jhtml");
            return client.execute(get, new TRJJsonHandler<>(Loan[].class));
        }
    }


    @Override
    public String projectLoan(User user, String name, String number, BigDecimal amount, int termDays, int limitYears
            , String province, String city, String address, int familyIncome, int personalIncome, int age
            , boolean hasHouse, String[] attaches) throws IOException {
        //
        NameValuePair[] attachPairs = new NameValuePair[attaches.length];
        for (int i = 0; i < attaches.length; i++) {
            attachPairs[i] = new BasicNameValuePair("attachList[" + i + "]", attaches[i]);
        }
        NameValuePair[] others = new NameValuePair[]{
                new BasicNameValuePair("applyLoan.name", name)
                , new BasicNameValuePair("applyLoan.mobile", user.getMobileNumber())
                , new BasicNameValuePair("applyLoan.cardNo", number)
                , new BasicNameValuePair("applyLoan.term", String.valueOf(termDays))
                , new BasicNameValuePair("applyLoan.termUnit", "day")
                , new BasicNameValuePair("applyLoan.prjTerm", String.valueOf(limitYears))
                , new BasicNameValuePair("applyLoan.prjTermUnit", "year")
                , new BasicNameValuePair("applyLoan.amount", amount.toString())
                , new BasicNameValuePair("applyLoan.province", province)
                , new BasicNameValuePair("applyLoan.city", city)
                , new BasicNameValuePair("applyLoan.address", address)
                , new BasicNameValuePair("applyLoan.personalIncome", String.valueOf(personalIncome))
                , new BasicNameValuePair("applyLoan.familyIncome", String.valueOf(familyIncome))
                , new BasicNameValuePair("applyLoan.age", String.valueOf(age))
                , new BasicNameValuePair("applyLoan.hasHouse", hasHouse ? "1" : "0")
        };

        // mix them
        NameValuePair[] parameters = new NameValuePair[attachPairs.length + others.length];
        System.arraycopy(others, 0, parameters, 0, others.length);
        System.arraycopy(attachPairs, 0, parameters, others.length, attachPairs.length);

        try (CloseableHttpClient client = requestClient()) {
            HttpGet get = new2Get("tenant/yt_prjApplyLoan.jhtml", parameters);
            return client.execute(get, new TRJJsonHandler<>(ApplyLoan.class)).getApplyId();
        }
    }

    @Override
    public String signContract(String requestId, String contract) throws IOException {
        try (CloseableHttpClient client = requestClient()) {
            HttpGet get = new2Get("tenant/yt_signContract.jhtml"
                    , new BasicNameValuePair("applyId", requestId)
                    , new BasicNameValuePair("templateNo", contract)
            );
            return client.execute(get, new TRJJsonHandler<>(LoanContract.class)).getContractId();
        }
    }

    @Override
    public void testMakeLoanStatus(String requestId, boolean success) throws IOException {
        try (CloseableHttpClient client = requestClient()) {
            HttpGet get = new2Get("tenant/yt_auditTest.jhtml"
                    , new BasicNameValuePair("applyId", requestId)
                    , new BasicNameValuePair("status", success ? "3" : "2")
            );
            client.execute(get).close();
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
                    , new BasicNameValuePair("applyLoan.term", String.valueOf(new CashStrings().termInteger(term)))
                    , new BasicNameValuePair("applyLoan.termUnit", "month")

            );
            return client.execute(get, new TRJJsonHandler<>(ApplyLoan.class)).getApplyId();
        }
    }

    @Override
    public LoanStatus checkLoanStatus(String id) throws IOException {
        try (CloseableHttpClient client = requestClient()) {
            HttpGet get = new2Get("tenant/yt_applyStatus.jhtml"
                    , new BasicNameValuePair("applyId", id)
            );
            String code = client.execute(get, new TRJJsonHandler<>(LoanStatusResult.class)).getStatus();
            switch (code) {
                case "2":
                    return LoanStatus.failed;
                case "3":
                    return LoanStatus.success;
                default:
                    return LoanStatus.auditing;
            }
        }
    }

    ////////////

    @Override
    public URI loginURL(String mobile) {
        // #/login///?tenant=&mobile=&sign=
        return new3Get("#/login///", new BasicNameValuePair("mobile", mobile)).getURI();
    }

    @Override
    public URI financingURL(String financingId, String mobile) throws IOException, VerifyCodeSentException {
        MobileToken token = token(mobile);
        if (token.getBinding() == -1)
            return loginURL(mobile);
        if (token.getBinding() == 0) {
            sendCode(token);
            throw new VerifyCodeSentException(token);
        }

        ///#/invest/1583/30/0?tenant=&prjId=123&mobile=13600000033&sign=&token=
        return new3Get("#/invest/" + financingId + "/1/30"
                , new BasicNameValuePair("mobile", mobile)
                , new BasicNameValuePair("prjId", financingId)
                , new BasicNameValuePair("token", token.getToken())
        ).getURI();
    }

    private HttpGet new1Get(String uri, NameValuePair... pairs) {
        return newGet(urlRoot1, uri, pairs);
    }

    private HttpGet new2Get(String uri, NameValuePair... pairs) {
        return newGet(urlRoot2, uri, pairs);
    }

    private HttpGet new3Get(String uri, NameValuePair... pairs) {
        return newGet(urlRoot3, uri, pairs);
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
        if (log.isDebugEnabled())
            log.debug("[TRJ]" + urlBuilder.toString());
        return new HttpGet(urlBuilder.toString());
    }

    private String sign(List<NameValuePair> list) {
        HashMap<String, String> data = new HashMap<>();
        list.forEach(nameValuePair -> data.put(nameValuePair.getName(), nameValuePair.getValue()));
        return sign(data, key);
    }

    private CloseableHttpClient requestClient() {
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder = builder.setDefaultRequestConfig(defaultRequestConfig);
        if (environment.acceptsProfiles("test")) {
            builder.setSSLHostnameVerifier(new NoopHostnameVerifier());
        }

        return builder.build();
    }
}
