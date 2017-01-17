package me.jiangcai.dating.entity.sale.pk;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.jiangcai.dating.entity.sale.TicketBatch;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import javax.persistence.Column;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import static me.jiangcai.dating.entity.sale.TicketCode.CodeLength;

/**
 * @author CJ
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketCodePK implements Serializable {
    private Long batch;
    @Column(length = CodeLength)
    private String code;

    public TicketCodePK(TicketBatch batch, String code) {
        this(batch.getId(), code);
    }

    public TicketCodePK(String format) {
        this(Long.parseLong(format.substring(format.lastIndexOf("@") + 1))
                , format.substring(0, format.lastIndexOf("@")));
    }

    public static TicketCodePK valueOf(String str) throws DecoderException, UnsupportedEncodingException {
        String format = new String(Hex.decodeHex(str.toCharArray()), "UTF-8");
        return new TicketCodePK(format);
    }

    /**
     * @return URI友好的格式
     */
    public String getURIFormat() {
//        return URLEncoder.encode(code + "@" + batch, "UTF-8");
        try {
            return Hex.encodeHexString((code + "@" + batch).getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new InternalError(e);
        }
    }
}
