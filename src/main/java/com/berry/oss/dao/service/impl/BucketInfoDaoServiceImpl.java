package com.berry.oss.dao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.berry.oss.common.utils.ObjectId;
import com.berry.oss.dao.entity.BucketInfo;
import com.berry.oss.dao.mapper.BucketInfoMapper;
import com.berry.oss.dao.service.IBucketInfoDaoService;
import com.berry.oss.module.vo.BucketInfoVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author HiCooper
 * @since 2019-06-04
 */
@Service
public class BucketInfoDaoServiceImpl extends ServiceImpl<BucketInfoMapper, BucketInfo> implements IBucketInfoDaoService {

    @Resource
    private BucketInfoMapper bucketInfoMapper;

    @Override
    public boolean save(BucketInfo entity) {
        entity.setId(ObjectId.get());
        return super.save(entity);
    }


    @Override
    public List<BucketInfoVo> listBucket(Integer userId, String name) {
        return bucketInfoMapper.listBucket(userId, name);
    }
}
