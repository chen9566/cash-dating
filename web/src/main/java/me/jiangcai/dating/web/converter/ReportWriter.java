package me.jiangcai.dating.web.converter;

import me.jiangcai.dating.selection.Report;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 可以输出报表
 *
 * @author CJ
 */
public interface ReportWriter {

    /**
     * @return 最终扩展名
     */
    String extension();

    /**
     * @return mimeType
     */
    String mimeType();

    <T> void writeTo(Report<T> report, OutputStream outputStream) throws IOException;
}
