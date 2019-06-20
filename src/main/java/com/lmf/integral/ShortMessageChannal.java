/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lmf.common.util.Digests;
import com.lmf.common.util.Encodes;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author shenzhixiong
 */
public class ShortMessageChannal {
    
    private final static String NAME    = "系统电信通道";
    
    private final Logger    logger  = LoggerFactory.getLogger(ShortMessageChannal.class);
    
    private final ObjectMapper  objectMapper;
    
    private final JavaType  resultType;
    
    public  ShortMessageChannal()
    {
        objectMapper    = new ObjectMapper();
        resultType      = objectMapper.getTypeFactory().constructMapLikeType(HashMap.class, String.class, Object.class);
    }

    public String name() {
        return NAME;
    }

    public boolean sendMessage(String content, String mobile) {
        HttpPost    post    = new HttpPost("http://c.kf10000.com/sdk/SMS");
        boolean success = false;
        try {
            List<BasicNameValuePair>  params    = new ArrayList<>();
            params.add(new BasicNameValuePair("cmd", "send"));
            params.add(new BasicNameValuePair("uid", "5298"));
            params.add(new BasicNameValuePair("psw", MD5("gz5298")));
            params.add(new BasicNameValuePair("mobiles", mobile));
            params.add(new BasicNameValuePair("msgId", String.valueOf(System.currentTimeMillis())));
            params.add(new BasicNameValuePair("msg", content));
             
            post.setEntity(new UrlEncodedFormEntity(params, "GBK"));
            HttpResponse    response    = httpClient().execute(post);
            String  resp    = EntityUtils.toString(response.getEntity());
            if (logger.isDebugEnabled())
            {
                logger.debug(resp);
            }
            if ("100".equals(resp))
            {
               success = true;
            }
        } catch (Exception exp) {
            if(logger.isWarnEnabled())
            {
                logger.warn(null, exp);
            }
        } finally {
            post.abort();
        }
        return success;
    }
    
     private static String MD5(String sourceStr) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
        }
        return result;
    }
    
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
                .setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000)
                .setParameter(CoreConnectionPNames.SO_TIMEOUT, 60000);
        return httpClient;
    }
    
}
