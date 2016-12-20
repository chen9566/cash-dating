package me.jiangcai.dating.web.controller.manage;

import me.jiangcai.dating.core.Login;
import me.jiangcai.dating.entity.supplier.Pay123Card;
import me.jiangcai.dating.repository.supplier.Pay123CardRepository;
import me.jiangcai.dating.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author CJ
 */
@Controller
@PreAuthorize("hasAnyRole('ROOT','" + Login.Role_Order_Value + "')")
public class ManagePay123Controller {

    private static final Pattern gapPattern = Pattern.compile("(.+)\\|(.+)");
    @Autowired
    private SystemService systemService;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private Pay123CardRepository pay123CardRepository;

    @RequestMapping(method = RequestMethod.GET, value = "/manage/pay123")
    public String index(Model model) {
        model.addAttribute("gap", pay123CardRepository.countAllUnused());
        return "manage/pay123.html";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/manage/pay123/toggle")
    @Transactional
    public String toggle() {
        systemService.updateEnablePay123(!systemService.isEnablePay123());
        return "redirect:/manage/pay123";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/manage/pay123")
    public String post(MultipartFile file) throws IOException {
        // 解压
        try (ZipInputStream zipInputStream = new ZipInputStream(file.getInputStream())) {
            while (true) {
                ZipEntry entry = zipInputStream.getNextEntry();
                if (entry == null)
                    break;
                Scanner sc = new Scanner(zipInputStream);
                while (sc.hasNextLine()) {
                    Matcher matcher = gapPattern.matcher(sc.nextLine());
                    if (matcher.matches()) {
                        addCard(matcher.group(1), matcher.group(2));
                    }
                }
//                StringTokenizer allString = new StringTokenizer(new String(entry.getExtra(),"UTF-8"),"\n");
//                while (allString.hasMoreTokens()){
//                    System.out.println(allString.nextToken());
//                }
            }
        }

        return "redirect:/manage/pay123";
    }

    @Transactional
    private void addCard(String id, String url) {
        if (pay123CardRepository.findOne(id) != null)
            return;
        Pay123Card card = new Pay123Card();
        card.setCreatedTime(LocalDateTime.now());
        card.setId(id);
        card.setQrUrl(url);
        pay123CardRepository.save(card);
    }
}
