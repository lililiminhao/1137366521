/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lmf.integral.task;

import com.lmf.common.IPResolverFactory;
import com.lmf.integral.service.WebsiteProxyService;
import com.lmf.website.entity.WebsiteUserVisitLog;
import com.lmf.website.service.WebsiteUserVisitLogService;
import com.xmcs03.chunzhen.IPEntry;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;

/**
 *
 * @author shenzhixiong
 */
public class WebsiteReloadConfigTask {
    
    @Autowired
    private WebsiteProxyService websiteProxyService;
    
    @Scheduled(fixedDelay = 2 * 60 * 1000)
    public void reloadConfig(){
        websiteProxyService.reloadConfig();
    }
}
