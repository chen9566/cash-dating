package me.jiangcai.dating.model.trj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import me.jiangcai.wx.converter.BooleanDeserializer;

/**
 * 理财产品
 *
 * @author CJ
 */
@Data
@JsonIgnoreProperties(value = {"remind", "activity_id", "activity_ext_info", "is_rate_yellow"}, ignoreUnknown = true)
public class Financing {
    private String id;
    private String prj_series;
    private String prj_type;
    @JsonProperty("prj_type_name")
    private String projectTypeName;
    private String prj_name;
    /**
     * 10.00
     */
    @JsonProperty("year_rate")
    private String yearRate;
    private String time_limit;
    private String time_limit_unit_view;
    private String schedule;
    //    "bid_status":"2","start_bid_time":"1476873238","min_bid_amount_name":"1,000.00","min_bid_amount_raw":"1000"
// ,"step_bid_amount_view":"1.00","max_bid_amount_view":"1,000.00","max_bid_amount_raw":"1000","demand_amount_view":"1.00 万"
// ,"remaining_amount":"1,000.00 元","invest_count":"9","client_type":["0"],"is_limit_amount":true,"guarantor_num":1,
// "remind":{"remind_id":"0","is_available":"0"}
// ,"activity_id":36
// ,"activity_ext_info":{"name":"","icon":"","big_icon":""}
// ,"projectSlogan":"定期理财 稳定收益 风险保障"
// ,"wanyuanProfit":"273.97元",
// "can_read":1}}
    private String bid_status;
    private String start_bid_time;
    private String min_bid_amount_name;
    private String min_bid_amount_raw;
    private String step_bid_amount_view;
    private String max_bid_amount_view;
    private String max_bid_amount_raw;
    private String demand_amount_view;
    private String remaining_amount;
    private String invest_count;
    private String[] client_type;
    @JsonProperty("is_limit_amount")
    private boolean is_limit_amount;
    private int guarantor_num;
    @JsonProperty("prj_slogan")
    private String projectSlogan;
    private String wanyuanProfit;
    @JsonDeserialize(using = BooleanDeserializer.class)
    private boolean can_read;
    private String url;


}
