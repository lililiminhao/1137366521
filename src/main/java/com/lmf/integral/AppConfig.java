/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral;

import com.lmf.website.entity.Website;
import com.lmf.website.service.WebsiteService;
import java.io.IOException;
import java.util.TimeZone;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author zhangxinling
 */
@Configuration
public class AppConfig {

    @Autowired
    private SystemConfig  systemConfig;
    
    @Autowired
    private WebsiteService  websiteService;
    
    @Bean(name = "httpClient")
    public  DefaultHttpClient   httpClient()
    {
        SchemeRegistry  schemeRegistry  = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
        schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));

        PoolingClientConnectionManager  cm  = new PoolingClientConnectionManager(schemeRegistry);
        cm.setMaxTotal(10);
        cm.setDefaultMaxPerRoute(20);
        DefaultHttpClient   httpClient  = new DefaultHttpClient(cm);
        httpClient.addRequestInterceptor(new HttpRequestInterceptor(){

            @Override
            public void process(HttpRequest hr, HttpContext hc) throws HttpException, IOException {
                hr.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.101 Safari/537.36");
            }
        });
        httpClient.getParams()
                .setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000)
                .setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);
        return httpClient;
    }
    
    @Bean(name = "website")
    public  Website website()
    {
        Website website = websiteService.findOne(systemConfig.getWebsiteId());
        return website;
    }
    
    @Bean
    public TimeZone tz()
    {
        return TimeZone.getTimeZone("Asia/Shanghai");
    }
    
}
