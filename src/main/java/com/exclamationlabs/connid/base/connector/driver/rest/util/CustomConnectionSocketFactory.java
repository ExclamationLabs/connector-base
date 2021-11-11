package com.exclamationlabs.connid.base.connector.driver.rest.util;

import com.exclamationlabs.connid.base.connector.configuration.basetypes.security.ProxyConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.protocol.HttpContext;

import javax.net.ssl.SSLContext;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;

public class CustomConnectionSocketFactory extends SSLConnectionSocketFactory {

    public CustomConnectionSocketFactory(SSLContext sslContext) {
        super(sslContext);
    }

    @Override
    public Socket createSocket(final HttpContext context) {
        InetSocketAddress socksaddr = (InetSocketAddress) context.getAttribute("socks.address");
        Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksaddr);
        return new Socket(proxy);

    }
}
