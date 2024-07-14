package com.FCA.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.FCA.dao.AlbumMapper;
import com.FCA.dao.ImageMapper;
import com.FCA.dao.RecycleMapper;

import com.FCA.dao.UserMapper;
import com.FCA.domain.Image;
import com.FCA.domain.Recycle;
import com.FCA.service.RecycleService;
import com.FCA.utils.FileServeUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class RecycleServiceImpl implements RecycleService {
    @Resource
    private RecycleMapper recycleMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private ImageMapper imageMapper;
    @Resource
    private AlbumMapper albumMapper;
    @Resource
    private FileServeUtil fileServeUtil;


    /**
     * 查询回收站所有图片
     * @param userId
     * @return
     */
    @Override
    public JSONObject selectAll(Integer userId) {
        List<Recycle> images = recycleMapper.selectAll(userId);
        List<String> previewImageUrL = new ArrayList<>();
        for(Recycle i:images){
            previewImageUrL.add(i.getCompressUrL());
        }
        JSONObject res = new JSONObject();
        res.put("images",images);
        res.put("previewImageUrL",previewImageUrL);
        return res;
    }

    /**
     * 恢复图片
     * @param userId
     * @param imageIds
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recoverImage(Integer userId, List<Integer> imageIds) {
        recycleMapper.deleteRecycle(userId,imageIds);
        userMapper.addUserImage(userId,imageIds);
    }

    /**
     * 彻底删除图片
     * @param userId
     * @param imageIds
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteImage(Integer userId, List<Integer> imageIds) {
        List<Image> images = imageMapper.selectImageByIds(imageIds);
        recycleMapper.deleteRecycle(userId,imageIds);
        imageMapper.deleteImage(imageIds);
        imageMapper.deleteImageType(imageIds);
        albumMapper.deleteAlbumImageByImgId(imageIds);
        for (Image i:images){
            fileServeUtil.deleteServe(i.getImageUrL());
            fileServeUtil.deleteServe(i.getCompressUrL());
        }
    }
}
