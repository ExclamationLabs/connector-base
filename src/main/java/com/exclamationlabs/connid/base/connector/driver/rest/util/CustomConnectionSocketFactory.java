package com.exclamationlabs.connid.base.connector.driver.rest.util;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import javax.net.ssl.SSLContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.protocol.HttpContext;

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
