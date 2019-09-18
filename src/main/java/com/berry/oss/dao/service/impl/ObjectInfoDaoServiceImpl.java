package com.berry.oss.dao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.berry.oss.common.utils.ObjectId;
import com.berry.oss.dao.entity.ObjectInfo;
import com.berry.oss.dao.mapper.ObjectInfoMapper;
import com.berry.oss.dao.service.IObjectInfoDaoService;
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
public class ObjectInfoDaoServiceImpl extends ServiceImpl<ObjectInfoMapper, ObjectInfo> implements IObjectInfoDaoService {

    @Resource
    private ObjectInfoMapper mapper;

    @Override
    public boolean save(ObjectInfo entity) {
        entity.setId(ObjectId.get());
        return super.save(entity);
    }

    @Override
    public void insertIgnoreBatch(List<ObjectInfo> list) {
        mapper.insertIgnoreBatch(list);
    }
}
