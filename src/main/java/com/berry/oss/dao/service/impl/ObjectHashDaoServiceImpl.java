package com.berry.oss.dao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.berry.oss.dao.entity.ObjectHash;
import com.berry.oss.dao.mapper.ObjectHashMapper;
import com.berry.oss.dao.service.IObjectHashDaoService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author HiCooper
 * @since 2019-06-07
 */
@Service
public class ObjectHashDaoServiceImpl extends ServiceImpl<ObjectHashMapper, ObjectHash> implements IObjectHashDaoService {

}
