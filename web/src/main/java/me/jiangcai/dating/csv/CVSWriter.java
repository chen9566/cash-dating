package me.jiangcai.dating.csv;

import me.jiangcai.dating.selection.Report;
import me.jiangcai.dating.selection.Selection;
import me.jiangcai.dating.web.converter.ReportWriter;
import org.supercsv.cellprocessor.FmtNumber;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.time.FmtLocalDateTime;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author CJ
 */
public class CVSWriter implements ReportWriter {
    @Override
    public String extension() {
        return "csv";
    }

    @Override
    public String mimeType() {
        return "text/csv";
    }

    private CellProcessor processorFor(Class<?> type) {
        if (type == String.class)
            return null;
        if (type == BigDecimal.class)
            return new FmtNumber(new DecimalFormat(",,###.##"));
        if (type == LocalDateTime.class)
            return new FmtLocalDateTime(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        throw new IllegalArgumentException("CSV not support " + type);
    }

    @Override
    public <T> void writeTo(Report<T> report, OutputStream outputStream) throws IOException {
        CsvListWriter writer = new CsvListWriter(new OutputStreamWriter(outputStream, "UTF-8")
                , CsvPreference.EXCEL_PREFERENCE);
        writer.write(report.getSelections().stream().map(Selection::getTitle).collect(Collectors.toList()));

        // selection应该会告诉我使用什么什么什么
        List<CellProcessor> processors = report.getSelections().stream()
                .map(Selection::getTargetType)
                .map(this::processorFor)
                .collect(Collectors.toList());
        CellProcessor[] cellProcessors = processors.toArray(new CellProcessor[processors.size()]);

        // 从Data中取值
        for (T data : report.getData()) {
            writer.write(report.getSelections().stream()
                            .map(selection -> selection.export(data))
                            .collect(Collectors.toList())
                    , cellProcessors
            );
        }

        writer.flush();
    }
}
