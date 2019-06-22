package com.berry.oss.common.exceptions;

import com.berry.oss.common.constant.XmlErrorInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019-06-22 23:41
 * fileName：XmlResponseException
 * Use：
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class XmlResponseException extends RuntimeException {

    private XmlErrorInfo xmlErrorInfo;

    public XmlResponseException(XmlErrorInfo xmlErrorInfo) {
        this.xmlErrorInfo = xmlErrorInfo;
    }
}
