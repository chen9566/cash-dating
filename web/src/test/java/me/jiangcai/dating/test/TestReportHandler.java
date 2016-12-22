package me.jiangcai.dating.test;

import me.jiangcai.dating.selection.Report;
import me.jiangcai.dating.web.converter.ReportHandler;
import org.springframework.context.annotation.Primary;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletResponse;

/**
 * @author CJ
 */
@Component
@Primary
public class TestReportHandler extends ReportHandler {

    public static Report lastReport;

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        lastReport = (Report) returnValue;

        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        response.setStatus(200);
        mavContainer.setRequestHandled(true);

    }
}
