package me.jiangcai.dating.web.model;

import lombok.Data;
import me.jiangcai.dating.entity.support.BookRateLevel;

import java.util.Map;

/**
 * 我的成员model
 *
 * @author CJ
 */
@Data
public class TeamMember {
    private Long id;
    private String name;
    private String mobile;
    private BookRateLevel level;


    @SuppressWarnings("unchecked")
    public static TeamMember To(Object o) {
        Map<String, ?> data = (Map<String, ?>) o;
        TeamMember member = new TeamMember();
        member.id = (Long) data.get("id");
        member.name = (String) data.get("nickname");
        member.mobile = (String) data.get("mobileNumber");
        member.level = (BookRateLevel) data.get("level");
        if (member.level == null)
            member.level = BookRateLevel.threshold;
        return member;
    }
}
