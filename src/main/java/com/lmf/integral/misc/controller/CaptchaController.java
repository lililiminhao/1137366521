/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.misc.controller;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author shenzhixiong
 */
@Controller
public class CaptchaController {
    
    private static final int  CAPTCHA_WIDTH = 75;
    
    private static final int  CAPTCHA_HIGHT = 24;
    
    private static final int  CAPTCHA_LEN   = 4;
    
    private final static Random  random     = new Random(System.currentTimeMillis());
    
    private final static char[]  CHAR_LIST  = {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q',
        'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '2', '3', '4', '5', '6',
        '7', '8', '9'
    };
    
    private final static String[] FONT_FAMILY   = {
        "Times New Roman",  "宋体", "黑体", "Arial Unicode MS", "Lucida Sans"
    };
    
    @RequestMapping(value = "/captcha.php", method = RequestMethod.GET)
    public @ResponseBody void captcha(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        BufferedImage   img     = new BufferedImage(CAPTCHA_WIDTH, CAPTCHA_HIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D  graphics    = (Graphics2D) img.getGraphics();
        graphics.setRenderingHints(new HashMap(){
            {
                put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            }
        });
        graphics.setColor(getBackGroundColor());
        graphics.setFont(new Font("Times New Roman", Font.ROMAN_BASELINE, 20));
        graphics.fillRect(0, 0, CAPTCHA_WIDTH, CAPTCHA_HIGHT);
        
        //绘制干扰线
        for (int i = 0; i < 6; ++ i)
        {
            float   stroke  = Math.abs(random.nextFloat() % 30);
            graphics.setStroke(new BasicStroke(stroke));
            graphics.setColor(getColor());
            graphics.drawLine(random.nextInt(CAPTCHA_WIDTH), random.nextInt(CAPTCHA_HIGHT), random.nextInt(CAPTCHA_WIDTH), random.nextInt(CAPTCHA_HIGHT));
        }
        
        String  captcha = getCaptchaString(CAPTCHA_LEN);
        int fontSize        = 0;
        int fontStyle       = 0;
        double  oldRotate   = 0;
        for (int i = 0; i < CAPTCHA_LEN; ++ i)
        {
            fontSize    = randomInt(18, 24);
            fontStyle   = random.nextInt(6);
            
            graphics.setColor(getColor());
            graphics.setFont(new Font(FONT_FAMILY[random.nextInt(FONT_FAMILY.length)], fontStyle, fontSize));
            double  rotate  = -0.25 + Math.abs(Math.toRadians(random.nextInt(25)));
            
            graphics.rotate(-oldRotate, 12, 15);
            oldRotate   = rotate;
            graphics.rotate(rotate, 15 * i + 12, 15);

            graphics.drawString(captcha.substring(i, i + 1), 17 * i + 5, 22);
        }
        
        graphics.dispose();
        
        HttpSession session = request.getSession(true);
        session.setAttribute("CAPTCHA", captcha);
        
        response.setHeader("Content-Type", "image/png");
        OutputStream  out   = null;
        try {
            out = response.getOutputStream();
            ImageIO.write(img, "png", out);
        } finally {
            if (out != null)
            {
                try {
                    out.close();
                } catch(IOException ignore){}
            }
        }        
    }
    
    private static String getCaptchaString(int len)
    {
        StringBuilder  sb   = new StringBuilder(len);
        for (int i = 0; i < len; ++ i)
        {
            sb.append(CHAR_LIST[random.nextInt(CHAR_LIST.length)]);
        }
        return sb.toString();
    }
    
    /**
     * 获取前景色
     * 
     * @return 
     */
    private static Color getColor()
    {
        int r   = randomInt(0, 120);
        int g   = randomInt(0, 120);
        int b   = randomInt(0, 120);
        return new Color(r, g, b);
    }
    
    private static Color getBackGroundColor()
    {
        int r   = randomInt(180, 255);
        int g   = randomInt(180, 255);
        int b   = randomInt(180, 255);
        return new Color(r, g, b);
    }
    
    private static int randomInt(int min, int max)
    {
        int intval  = max - min;
        int rd      = random.nextInt(intval + 1);
        return rd + min;
    }
}

