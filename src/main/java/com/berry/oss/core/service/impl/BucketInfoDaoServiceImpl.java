package com.berry.oss.core.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.berry.oss.common.utils.ObjectId;
import com.berry.oss.core.entity.BucketInfo;
import com.berry.oss.core.mapper.BucketInfoMapper;
import com.berry.oss.core.service.IBucketInfoDaoService;
import org.springframework.stereotype.Service;

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

    @Override
    public boolean save(BucketInfo entity) {
        entity.setId(ObjectId.get());
        return super.save(entity);
    }

}
