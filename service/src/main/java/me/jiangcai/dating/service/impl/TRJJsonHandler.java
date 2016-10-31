package me.jiangcai.dating.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.impl.client.AbstractResponseHandler;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author CJ
 */
class TRJJsonHandler<T> extends AbstractResponseHandler<T> {

    private static final Log log = LogFactory.getLog(TRJJsonHandler.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final Class<T> type;

    TRJJsonHandler(Class<T> clazz) {
        type = clazz;
    }

    @Override
    public T handleEntity(HttpEntity entity) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        StreamUtils.copy(entity.getContent(), buffer);
        if (log.isDebugEnabled()) {
            log.debug(StreamUtils.copyToString(new ByteArrayInputStream(buffer.toByteArray()), Charset.forName("UTF-8")));
        }
        JsonNode root = objectMapper.readTree(new ByteArrayInputStream(buffer.toByteArray()));
        if (root.get("boolen").intValue() == 0)
            throw new IllegalStateException(root.get("message").asText());
        JsonNode data = root.get("data");

        if (type.isArray() && !data.isArray()) {
            return objectMapper.readValue(data.asText(), type);
        }

        return objectMapper.readValue(objectMapper.treeAsTokens(data), type);
    }
}
