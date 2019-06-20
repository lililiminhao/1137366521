/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lmf.integral.misc.controller;

import com.lmf.common.SerializableMultipartFile;
import com.lmf.common.SimpleJsonResponse;
import com.lmf.common.util.SimpleImageInfo;
import com.lmf.integral.ViewSettings;
import com.lmf.product.entity.ProductAttachment;
import com.lmf.product.service.ProductAttachmentService;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Administrator
 */
@Controller
public class CKEditorController {
    
    @Autowired
    private ProductAttachmentService  productAttachmentService;
    
    @Autowired
    private ViewSettings    viewSettings;
    
    @RequestMapping(value = "/admin/ckeditor/upload.php", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public @ResponseBody String upload(@RequestParam(value = "upload") MultipartFile file,
                                       @RequestParam(value = "CKEditor") String editor,
                                       @RequestParam(value = "CKEditorFuncNum") String callback,
                                       @RequestParam(value = "type", required = true) String type,
                                       @RequestParam(value = "langCode", required = false) String langCode) throws IOException
    {
        if (type.equalsIgnoreCase("Images"))
        {
            ProductAttachment  attachment   = productAttachmentService.create(new SerializableMultipartFile(file));
            StringBuilder sbd = new StringBuilder("<script type='text/javascript'>");
            sbd.append("window.parent.CKEDITOR.tools.callFunction(")
               .append(callback)
               .append(",'")
               .append(viewSettings.getImageHost())
               .append(attachment.getAttchmentUrl())
               .append("','');</script>");
            return sbd.toString();
        }
        return null;
    }
    
    @RequestMapping(value = "/admin/ckeditor/batchUpload.php", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public @ResponseBody SimpleJsonResponse batchUpload(@RequestParam(value = "batch_images[]") MultipartFile[] images) throws IOException
    {
        List<MultipartFile> files   = new ArrayList<>(images.length);
        for (MultipartFile image : images)
        {
            SimpleImageInfo sii = null;
            try {
                sii = new SimpleImageInfo(image.getBytes());
            } catch (Exception ignore) { }
            if (sii != null)
            {
                files.add(image);
            }
        }
        if (files.isEmpty())
        {
            //没有图片被上传
            return new SimpleJsonResponse<>(false, "没有图片被上传");
        }
        
        List<BatchUploadResponse>   response    = new ArrayList<>(files.size());
        for (MultipartFile mf : files)
        {
            ProductAttachment  attachment   = productAttachmentService.create(new SerializableMultipartFile(mf));
            String  path    = new StringBuilder(viewSettings.getImageHost()).append(attachment.getAttchmentUrl()).toString();
            response.add(new BatchUploadResponse(attachment.getId(), mf.getOriginalFilename(), path));
        }
        return new SimpleJsonResponse(true, response);
    }
    
    @RequestMapping(value = "/ckeditor/deleteUpload.php", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    public @ResponseBody SimpleJsonResponse delete(@RequestParam("id") long id)
    {
        ProductAttachment   attach  = productAttachmentService.findOne(id);
        if (attach != null)
        {
            productAttachmentService.delete(id);
        }
        return new SimpleJsonResponse(true, null);
    }
    
    public static class BatchUploadResponse implements Serializable {
        
        private long    imageId;
        
        private String  fileName;
        
        private String  path;

        public BatchUploadResponse(long imageId, String fileName, String path) {
            this.imageId = imageId;
            this.fileName = fileName;
            this.path = path;
        }

        public long getImageId() {
            return imageId;
        }

        public void setImageId(long imageId) {
            this.imageId = imageId;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }
}
