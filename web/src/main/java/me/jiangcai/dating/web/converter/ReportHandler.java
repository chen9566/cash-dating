package me.jiangcai.dating.web.converter;

import me.jiangcai.dating.selection.Report;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletResponse;

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

//        DownloadableFile file = (DownloadableFile) returnValue;
//
//        if (file.getType() != null) {
//            response.setContentType(file.getType().toString());
//        }
//        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(file.getFileName(), "UTF-8"));
//        byte[] buffer = StreamUtils.copyToByteArray(file.getData());
//        response.setContentLength(buffer.length);
        response.setStatus(200);

//        StreamUtils.copy(buffer, response.getOutputStream());
        mavContainer.setRequestHandled(true);
    }
}
