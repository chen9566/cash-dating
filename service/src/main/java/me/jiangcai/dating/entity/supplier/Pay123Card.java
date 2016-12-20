package me.jiangcai.dating.entity.supplier;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * http://123.206.99.141:8083/selfweb 的台卡
 * 我叫它Pay 123
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class Pay123Card {
    @Id
    @Column(length = 40)
    private String id;
    @Column(length = 200)
    private String qrUrl;
    @Column(columnDefinition = "datetime")
    private LocalDateTime createdTime;

}
