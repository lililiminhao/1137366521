/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.admin.product.controller;

import com.lmf.common.SerializableMultipartFile;
import com.lmf.common.SimpleJsonResponse;
import com.lmf.common.image.ImageService;
import com.lmf.common.image.ImageSize;
import com.lmf.common.image.ImageType;
import com.lmf.common.util.SimpleImageInfo;
import com.lmf.product.entity.ProductImage;
import com.lmf.product.service.ProductImageService;
import com.lmf.product.service.ProductImageStorageService;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author zhangxinling
 */
@Controller
public class ProductImageController {
    
    @Autowired
    private ProductImageService productImageService;
    
    @Autowired
    private ProductImageStorageService  productImageStorageService;
    
    @Autowired
    private ImageService    imageService;
    
    private static final Logger logger  = LoggerFactory.getLogger(ProductImageController.class);
 
    @RequiresPermissions("product:edit,create")
    @RequestMapping(value = "/admin/product/image/uploadHeaderImage.php", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public @ResponseBody SimpleJsonResponse uploadImage(@RequestParam("images[]") MultipartFile[] files, 
                                                        @RequestParam("max_file") int max_file) throws IOException
    {
        for(MultipartFile file : files){
            if(!file.getContentType().equals("image/gif") && !file.getContentType().equals("image/jpg") && !file.getContentType().equals("image/jpeg")){
                return new SimpleJsonResponse(false, "请上传正确的图片文件，包括.gif, .jpg, .jpeg");
            }
        }
        if (max_file > 5 || max_file < 0)
        {
            return new SimpleJsonResponse(false, "图片已上传满，无法上传更多的图片");
        }
        List<UploadResponse> response  = new ArrayList<>(files.length);
        int processed   = 0;
        for (MultipartFile file : files)
        {
            if (file.isEmpty())
            {
                continue;
            }
            if (!file.getContentType().startsWith("image/"))
            {
                continue;
            }
            byte[]  bytes   = file.getBytes();
            try {
                SimpleImageInfo sii = new SimpleImageInfo(bytes);
            } catch (IOException exp) {
                continue;
            }
            
            UploadResponse  rep = new UploadResponse();
            ProductImage pi = new ProductImage();
            pi  = productImageService.save(pi, new SerializableMultipartFile(bytes, file.getName(), file.getOriginalFilename(), file.getContentType(), bytes.length));
            rep.setImageID(pi.getId());
            rep.setSrc(pi.getImageUrl());
            response.add(rep);
            
            ++ processed;
            if (processed >= max_file)
            {
                break;
            }
        }
        if (response.isEmpty())
        {
            return new SimpleJsonResponse(false, "没有图片被上传，请选择图片");
        }
        return new SimpleJsonResponse(true, response);
    }
    
    @RequestMapping(value = "/image/readImage.php", method = RequestMethod.GET)
    public @ResponseBody void readImage(@RequestParam("path") String path, 
                                        @RequestParam("width") int width,
                                        @RequestParam("height") int height,
                                        HttpServletResponse response)
    {
        InputStream input       = null;
        OutputStream    output  = null;
        try {
            byte[]  bytes   = productImageStorageService.readImageAsBytes(path);
            byte[]  bytesOut    = imageService.thumbnail(bytes, ImageType.png, new ImageSize(width, height), true);
            response.setContentType("image/png");
            output  = response.getOutputStream();
            output.write(bytesOut);
        } catch (Exception exp) {
            if (logger.isErrorEnabled())
            {
                logger.error(null, exp);
            }
        } finally {
            if (input != null)
            {
                try {
                    input.close();
                } catch (IOException ignore) {}
            }
            if (output != null)
            {
                try {
                    output.close();
                } catch (IOException ignore) {}
            }
        }
    }
    
    class UploadResponse implements Serializable {
        
        long    imageID;
        
        String  src;

        public String getSrc() {
            return src;
        }

        public void setSrc(String src) {
            this.src = src;
        }

        public long getImageID() {
            return imageID;
        }

        public void setImageID(long imageID) {
            this.imageID = imageID;
        }
    }
}
