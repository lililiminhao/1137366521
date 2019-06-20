/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.velocity;

import com.lmf.website.theme.v2.BlockValue;
import com.lmf.website.theme.v2.PageBlockDef;
import com.lmf.website.theme.v2.PageBlockType;
import com.lmf.website.theme.v2.PageSkeleton;
import com.lmf.website.theme.v2.PageSkeleton.PageBlock;
import com.lmf.website.theme.v2.manager.ThemeManager;
import java.io.StringWriter;
import java.util.List;
import java.util.Map.Entry;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.view.context.ChainedContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 *
 * @author 张心灵
 */
@DefaultKey("v2RenderTool")
public class VelocityThemeV2RenderTool {
    
    @Autowired
    private ChainedContext  cctx;
    
    private WebApplicationContext   appCtx;
    
    private ThemeManager    themeManager;
    
    public  void   init(Object ctx)
    {
        cctx        = (ChainedContext) ctx;
    }
    
    public String getVelocity(PageSkeleton ps, PageBlockType blockType, String key, int index, Model model)
    {
        VelocityContext ctx;
        if (model != null)
        {
            ctx = new VelocityContext(model.asMap());
            for (Entry<String, Object> te : cctx.getToolbox().entrySet())
            {
                ctx.put(te.getKey(), te.getValue());
            }
        } else {
            ctx = new VelocityContext(cctx.getToolbox());
        }
        ctx.put("request", cctx.getRequest());
        ctx.put("session", cctx.getSession());
        
        PageBlock block = null;
        if(PageBlockType.header == blockType){
            block = ps.getHeader();
        }else if(PageBlockType.block == blockType){
            List<PageBlock> blocks = ps.getBodyBlocks();
            if(blocks != null && blocks.size() >= index){
                block = blocks.get(index);
            }
        }else if(PageBlockType.footer == blockType){
            block = ps.getFooter();
        }
        
        if (block != null)
        {
            for (Entry<String, BlockValue> e : block.getBlockValues().entrySet())
            {
                ctx.put(e.getKey(), e.getValue());
            }
            PageBlockDef  def   = getThemeManager().getThemeDef(blockType, block.getBlockKey());
            if(def != null){
                StringWriter  writer    = new StringWriter();
                cctx.getVelocityEngine().evaluate(ctx, writer, def.getKey(), def.getVelocity());
                return writer.toString();
            }
        }
        return null;
    }
    
    public  String  header(PageSkeleton ps, Model model)
    {
        VelocityContext ctx;
        if (model != null)
        {
            ctx = new VelocityContext(model.asMap());
            for (Entry<String, Object> te : cctx.getToolbox().entrySet())
            {
                ctx.put(te.getKey(), te.getValue());
            }
        } else {
            ctx = new VelocityContext(cctx.getToolbox());
        }
        ctx.put("request", cctx.getRequest());
        ctx.put("session", cctx.getSession());
        PageBlock block = ps.getHeader();
        if (block != null)
        {
            for (Entry<String, BlockValue> e : block.getBlockValues().entrySet())
            {
                ctx.put(e.getKey(), e.getValue());
            }
            PageBlockDef  def   = getThemeManager().getThemeDef(PageBlockType.header, block.getBlockKey());
            if(def != null){
                StringWriter  writer    = new StringWriter();
                cctx.getVelocityEngine().evaluate(ctx, writer, def.getKey(), def.getVelocity());
                return writer.toString();
            }
        }
        return null;
    }
    
    public  String  body(PageSkeleton ps)
    {
        StringBuilder bodyHtml = new StringBuilder();
        VelocityContext ctx = new VelocityContext(cctx.getToolbox());
        ctx.put("request", cctx.getRequest());
        ctx.put("session", cctx.getSession());
        List<PageBlock> blocks = ps.getBodyBlocks();
        for(PageBlock block : blocks)
        {
            for (Entry<String, BlockValue> e : block.getBlockValues().entrySet())
            {
                ctx.put(e.getKey(), e.getValue());
            }
            PageBlockDef  def   = getThemeManager().getThemeDef(PageBlockType.block, block.getBlockKey());
            if(def != null){
                StringWriter  writer    = new StringWriter();
                cctx.getVelocityEngine().evaluate(ctx, writer, def.getKey(), def.getVelocity());
                bodyHtml.append(writer.toString());
            }
        } 
        return bodyHtml.toString();
    }
    
    public  String  footer(PageSkeleton ps)
    {
        VelocityContext ctx = new VelocityContext(cctx.getToolbox());
        ctx.put("request", cctx.getRequest());
        ctx.put("session", cctx.getSession());
        PageBlock footer = ps.getFooter();
        for (Entry<String, BlockValue> e : footer.getBlockValues().entrySet())
        {
            ctx.put(e.getKey(), e.getValue());
        }
        PageBlockDef  def   = getThemeManager().getThemeDef(PageBlockType.block, footer.getBlockKey());
        if(def != null){
            StringWriter  writer    = new StringWriter();
            cctx.getVelocityEngine().evaluate(ctx, writer, def.getKey(), def.getVelocity());
            return writer.toString();
        }
        return null;
    }
    
    private WebApplicationContext  getAppCtx()
    {
        if (appCtx == null)
        {
            appCtx  = WebApplicationContextUtils.getWebApplicationContext(cctx.getServletContext());
        }
        return  appCtx;
    }
    
    private ThemeManager    getThemeManager()
    {
        if (themeManager == null)
        {
            themeManager    = getAppCtx().getBean(ThemeManager.class);
        }
        return themeManager;
    }
}
