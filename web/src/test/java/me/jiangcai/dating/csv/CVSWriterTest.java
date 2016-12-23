package me.jiangcai.dating.csv;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.jiangcai.dating.selection.Report;
import me.jiangcai.dating.selection.Selection;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * @author CJ
 */
public class CVSWriterTest {

    @Test
    public void writeTo() throws Exception {
        Report<TheBean> report = new Report<>("呵呵"
                , Arrays.asList(
                new TheBean("John", LocalDateTime.now(), new BigDecimal("10.11"))
                , new TheBean("Alice", LocalDateTime.now(), new BigDecimal("9999.11"))
                , new TheBean("Bob", LocalDateTime.now(), new BigDecimal("1992939383"))
        )
                , Arrays.asList(new Selection<TheBean, String>() {
            @Override
            public String getTitle() {
                return "名字";
            }

            @Override
            public Class<? extends String> getTargetType() {
                return String.class;
            }

            @Override
            public String export(TheBean data) {
                return data.getName();
            }
        }, new Selection<TheBean, LocalDateTime>() {
            @Override
            public String getTitle() {
                return "生日";
            }

            @Override
            public Class<? extends LocalDateTime> getTargetType() {
                return LocalDateTime.class;
            }

            @Override
            public LocalDateTime export(TheBean data) {
                return data.getLocalDateTime();
            }
        }, new Selection<TheBean, BigDecimal>() {
            @Override
            public String getTitle() {
                return "钱";
            }

            @Override
            public Class<? extends BigDecimal> getTargetType() {
                return BigDecimal.class;
            }

            @Override
            public BigDecimal export(TheBean data) {
                return data.getAmount();
            }
        }));

        CVSWriter writer = new CVSWriter();
        File file = new File("target/report." + writer.extension());
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            writer.writeTo(report, outputStream);
            outputStream.flush();
        }
    }

    @Data
    @AllArgsConstructor
    private class TheBean {
        private String name;
        private LocalDateTime localDateTime;
        private BigDecimal amount;
    }

}