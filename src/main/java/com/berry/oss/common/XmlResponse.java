package com.berry.oss.common;

import com.berry.oss.common.constant.XmlErrorInfo;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019-06-23 00:16
 * fileName：XmlResponse
 * Use：
 */
@Data
@Accessors(chain = true)
@JacksonXmlRootElement(localName = "ERROR")
public class XmlResponse {

    @JacksonXmlProperty(localName = "INFO")
    public XmlErrorInfo xmlErrorInfo;

    public XmlResponse(XmlErrorInfo xmlErrorInfo) {
        this.xmlErrorInfo = xmlErrorInfo;
    }
}
