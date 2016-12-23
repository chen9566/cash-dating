package me.jiangcai.dating.web.converter;

import me.jiangcai.dating.csv.CVSWriter;
import me.jiangcai.dating.selection.Report;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;

/**
 * @author CJ
 */
@Component
public class ReportHandler implements HandlerMethodReturnValueHandler {
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.getParameterType() == Report.class;
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        if (returnValue == null)
            return;
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);

        Report report = (Report) returnValue;
        ReportWriter writer = new CVSWriter();

        response.setContentType(writer.mimeType());
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(report.getName() + "." + writer.extension(), "UTF-8"));

        response.setStatus(200);
        // length?
        writer.writeTo(report, response.getOutputStream());

        mavContainer.setRequestHandled(true);
    }
}
