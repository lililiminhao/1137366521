/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral;

import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.lmf.common.util.Digests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 
 * @author zhouw
 */
public class WechatUtil {
    
	private final static TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
    
    private final static Random random = new Random(System.currentTimeMillis());
    
    public static Map<String, Object> resolveXML(String content) throws ParserConfigurationException, SAXException, UnsupportedEncodingException, IOException
    {
        if(content != null && !content.trim().isEmpty()){
            Map<String, Object> dataMap = new HashMap<>();
            DocumentBuilderFactory  dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder parser      = dbf.newDocumentBuilder();
            InputStream is  =  new ByteArrayInputStream(content.getBytes("UTF-8"));
            Document document = parser.parse(is);
            Element rootElement = document.getDocumentElement();
            NodeList nList = rootElement.getChildNodes();
            for(int i = 0; i < nList.getLength(); i++){
                Node node = nList.item(i);
                dataMap.put(node.getNodeName(), node.getTextContent());
            }
            return dataMap;
        }
        return Collections.EMPTY_MAP;
    }
    
    public static Map<String, Object> resolveXML(InputStream input) throws ParserConfigurationException, SAXException, IOException{
        if(input != null){
            Map<String, Object> dataMap = new HashMap<>();
            DocumentBuilderFactory  dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder parser      = dbf.newDocumentBuilder();
            Document        document    = parser.parse(input);
            Element rootElement = document.getDocumentElement();
            NodeList nList = rootElement.getChildNodes();
            for(int i = 0; i < nList.getLength(); i++){
                Node node = nList.item(i);
                dataMap.put(node.getNodeName(), node.getTextContent());
            }
            return dataMap;
        }
        return Collections.EMPTY_MAP;
    }
    
    
    //对参数按照key=value的格式，并按照参数名ASCII字典序排序  -->空值不参与签名
    public static String getParamsOrderByASCII(Map<String, String> paramMap){
        if(paramMap == null || paramMap.isEmpty()){
            return null;
        }
        
        List<String> keyList = new ArrayList();
        for(Map.Entry<String, String> item : paramMap.entrySet()){
            keyList.add(item.getKey());
        }
        Collections.sort(keyList);
        
        StringBuilder params = new StringBuilder();
        boolean flag = true;
        for(String key : keyList){
            String param = paramMap.get(key);
            if(param != null && !param.trim().isEmpty()){
                if(flag){
                    params.append(key).append("=").append(param);
                    flag = false;
                }else{
                    params.append("&").append(key).append("=").append(param);
                }
            }
        }
        return params.toString();
    }
    
    public static String getXMLParams(Map<String, String> paramMap){
        if(paramMap == null || paramMap.isEmpty()){
            return null;
        }
        
        StringBuilder sb = new StringBuilder("<xml>");
        for(Map.Entry<String, String> item : paramMap.entrySet()){
            sb.append("<").append(item.getKey()).append(">").append(item.getValue()).append("</").append(item.getKey()).append(">");
        }
        sb.append("</xml>");
        return sb.toString();
    }
    
    
    //获取指定长度的随机字符串
    public static String getNonceStr(int length){
        String tempStr = "";
        if(length <= 0){
            return tempStr;
        }
        for(int i = 0; i < length; i++){
            int index = random.nextInt(CHARS.length);
            tempStr += CHARS[index];
        }
        return tempStr;
    }
    
    private static final char CHARS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G',
      'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b',
      'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
      'x', 'y', 'z'};
    
    public static String getTimeStart(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(Calendar.getInstance(tz).getTime());
    }
    
    public static long getTimeStamp(){
        return Calendar.getInstance(tz).getTimeInMillis();
    }
    
    public static long getTimeStampSecond() {
        return Calendar.getInstance(tz).getTimeInMillis()/1000;
    }
    
    public static String SH1(String params) throws UnsupportedEncodingException{
        byte[] bytes = Digests.sha1(params.getBytes("UTF-8"));
        StringBuilder signParams = new StringBuilder();
        for (byte b : bytes) {
            signParams.append(String.format("%02x",b));
        }
        return signParams.toString();
    }
    
    public static String MD5(String params) throws UnsupportedEncodingException{
        byte[] bytes = Digests.md5(params.getBytes("UTF-8"));
        StringBuilder signParams = new StringBuilder();
        for (byte b : bytes) {
            signParams.append(String.format("%02x",b));
        }
        return signParams.toString();
    }
    
    public static String getResponseData(HttpURLConnection conn) throws IOException
    {
        InputStream ins = conn.getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf	= new byte[4096];
        int	readed	= -1;
        while (-1 != (readed = ins.read(buf)))
        {
            baos.write(buf, 0, readed);
        }
        return  new String(baos.toByteArray(), "UTF-8");
    }
    
    public static HttpURLConnection doPost(String url, String params) throws IOException
    {
        URL  u = new URL(url);
        HttpURLConnection	conn	= (HttpURLConnection) u.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        byte bytes[] = null;
        if(params != null && !params.trim().isEmpty()){
            bytes = params.getBytes("UTF-8");
        }
        if (bytes != null)
        {
            conn.setRequestProperty("Content-Length", Integer.toString(bytes.length));
            conn.setDoOutput(true);
        }
        conn.setDoInput(true);
        conn.setUseCaches(false);
        conn.setInstanceFollowRedirects(false);
        if (bytes != null)
        {
            OutputStream dos = null;
            try {
                dos	= conn.getOutputStream();
                dos.write(bytes);
                dos.flush();
            } finally {
                if (dos != null)
                {
                    dos.close();
                }
            }
        }
        return conn;
    }
    
}
