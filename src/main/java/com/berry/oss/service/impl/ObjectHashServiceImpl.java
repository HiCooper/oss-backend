package com.berry.oss.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.berry.oss.common.ResultCode;
import com.berry.oss.common.exceptions.BaseException;
import com.berry.oss.core.entity.ObjectHash;
import com.berry.oss.core.service.IObjectHashDaoService;
import com.berry.oss.service.IObjectHashService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019-06-07 21:04
 * fileName：ObjectHashServiceImpl
 * Use：
 */
@Service
public class ObjectHashServiceImpl implements IObjectHashService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    private final IObjectHashDaoService objectHashDaoService;

    ObjectHashServiceImpl(IObjectHashDaoService objectHashDaoService) {
        this.objectHashDaoService = objectHashDaoService;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String checkExist(String hash, Long dataLength) {
        QueryWrapper<ObjectHash> queryWrapper = new QueryWrapper<ObjectHash>().eq("hash", hash);
        int count = objectHashDaoService.count(queryWrapper);
        if (count == 0) {
            return null;
        }
        ObjectHash one = objectHashDaoService.getOne(queryWrapper);
        if (count > 1) {
            // 数据库建立了 hash 唯一索引，理论上不可能出现
            logger.error("hash：{}，出现 {} 次", hash, count);
            // 触发清理,将id较大的删除
            queryWrapper.gt("id", one.getId());
            objectHashDaoService.remove(queryWrapper);
        }
        return one.getFileId();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean increaseRefCountByHash(String hash, String fileId, Long size) {
        QueryWrapper<ObjectHash> queryWrapper = new QueryWrapper<ObjectHash>()
                .eq("hash", hash)
                .eq("file_id", fileId);
        ObjectHash one = objectHashDaoService.getOne(queryWrapper);
        if (one == null) {
            one = new ObjectHash()
                    .setHash(hash)
                    .setFileId(fileId)
                    .setSize(size)
                    .setReferenceCount(0);
        }
        one.setReferenceCount(one.getReferenceCount() + 1);
        return objectHashDaoService.saveOrUpdate(one);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean decreaseRefCountByHash(String hash) {
        QueryWrapper<ObjectHash> queryWrapper = new QueryWrapper<ObjectHash>().eq("hash", hash);
        ObjectHash one = objectHashDaoService.getOne(queryWrapper);
        if (one == null) {
            throw new BaseException(ResultCode.DATA_NOT_EXIST);
        }
        int newCount = Math.max(one.getReferenceCount() - 1, 0);
        one.setReferenceCount(newCount);
        // 引用为 0  的索引，由定时任务程序去扫描整理删除 对应的数据和引用
        return objectHashDaoService.updateById(one);
    }
}
