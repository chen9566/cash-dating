package me.jiangcai.dating.web.controller;

import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.support.BookRateLevel;
import me.jiangcai.dating.repository.UserRepository;
import me.jiangcai.dating.service.AgentService;
import me.jiangcai.dating.service.DataService;
import me.jiangcai.dating.service.UserService;
import me.jiangcai.dating.web.model.TeamMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 代理商相关
 *
 * @author CJ
 */
@Controller
public class AgentController {

    @Autowired
    private AgentService agentService;
    @Autowired
    private UserService userService;
    @Autowired
    private DataService dataService;
    @Autowired
    private UserRepository userRepository;

    /**
     * 总的入口,如果还不是代理商 应该去代理商申请页
     */
    @RequestMapping(method = RequestMethod.GET, value = "/agent")
    public String index(@AuthenticationPrincipal User user) {
        user = userService.by(user.getId());
        if (user.getAgentInfo() == null) {
            return "agent.html";
        }
        throw new NoSuchMethodError("what about agent?");
    }

    /**
     * 申请成为代理商
     */
    @RequestMapping(method = RequestMethod.POST, value = "/agent")
    @ResponseStatus(HttpStatus.CREATED)
    public void request(@AuthenticationPrincipal User user
            , @RequestBody Map<String, Object> data) {
        String name = (String) data.get("name");
        String mobile = (String) data.get("mobile");
        if (StringUtils.isEmpty(name))
            throw new IllegalArgumentException("name is required");
        if (StringUtils.isEmpty(mobile))
            throw new IllegalArgumentException("mobile is required");
        if (mobile.length() < 11)
            throw new IllegalArgumentException("mobile is required");

        user = userService.by(user.getId());
        agentService.newRequest(user, name, mobile);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/memberRate/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void changeRate(@AuthenticationPrincipal User owner, @PathVariable("id") Long id
            , @RequestBody String level) {
        User user = userService.by(id);
        BookRateLevel rateLevel = BookRateLevel.valueOf(level);
        if (!user.getAgentUser().equals(owner) && !user.equals(owner))
            return;
        if (user.getMyAgentInfo() != null) {
            user.getMyAgentInfo().setBookLevel(rateLevel);
        } else {
            user.setMyAgentInfo(user.updateMyAgentInfo());
            user.getMyAgentInfo().setBookLevel(rateLevel);
        }
        userRepository.save(user);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/myTeamData")
    public String teamData(@AuthenticationPrincipal User user, int offset, Model model) {
        model.addAttribute("offset", offset);
        if (user.getAgentInfo() == null) {
            model.addAttribute("list", new ArrayList<>());
            return "myTeamData.html";
        }

        List<TeamMember> list = ((List<?>) dataService.data(user, null, "joinTime", Sort.Direction.DESC, offset, 10
                , User.class, Arrays.asList(
                        new DataService.NumberField("id", Long.class),
                        new DataService.StringField("nickname"),
                        new DataService.StringField("mobileNumber"),
                        new DataService.StringField("joinTime"),
                        new DataService.EnumField("level") {
                            @Override
                            protected Expression<?> selectExpression(Root<?> root) {
                                return root.join("myAgentInfo", JoinType.LEFT).get("bookLevel");
                            }
                        }
                )
                , (user1, criteriaBuilder, root)
                        -> criteriaBuilder.or(criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("agentUser"), user1)
                        , User.validUserPredicate(criteriaBuilder, root))
                        , criteriaBuilder.equal(root, user1))
        ).get("rows")).stream()
                .map(TeamMember::To)
                .collect(Collectors.toList());

        model.addAttribute("list", list);

        return "myTeamData.html";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/myTeam")
    public String teamPage(@AuthenticationPrincipal User user, Model model) {
        user = userService.by(user.getId());
        if (user.getAgentInfo() == null)
            return "redirect:/my";
        return "myteam.html";
    }

}
