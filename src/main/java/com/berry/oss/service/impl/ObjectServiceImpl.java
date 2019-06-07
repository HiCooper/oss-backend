package com.berry.oss.service.impl;

import com.berry.oss.common.utils.StringUtils;
import com.berry.oss.core.entity.ObjectInfo;
import com.berry.oss.core.service.IObjectInfoDaoService;
import com.berry.oss.security.SecurityUtils;
import com.berry.oss.security.vm.UserInfoDTO;
import com.berry.oss.service.IObjectHashService;
import com.berry.oss.service.IObjectService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019-06-04 22:33
 * fileName：ObjectServiceImpl
 * Use：
 */
@Service
public class ObjectServiceImpl implements IObjectService {

    private final IObjectInfoDaoService objectInfoDaoService;
    private final IObjectHashService objectHashService;


    ObjectServiceImpl(IObjectInfoDaoService objectInfoDaoService,
                      IObjectHashService objectHashService) {
        this.objectInfoDaoService = objectInfoDaoService;
        this.objectHashService = objectHashService;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean saveObjectInfo(String bucketId, String acl, String hash, Long contentLength, String fileName, String filePath, String fileId) {
        UserInfoDTO currentUser = SecurityUtils.getCurrentUser();
        // 极速上传，添加 该账号 该文件记录
        ObjectInfo objectInfo = new ObjectInfo()
                .setId(StringUtils.getRandomStr(32))
                .setBucketId(bucketId)
                .setCategory(StringUtils.getExtName(fileName))
                .setFileId(fileId)
                .setSize(contentLength)
                .setFileName(fileName)
                .setFilePath(filePath)
                .setIsDir(false)
                .setAcl(acl)
                .setUserId(currentUser.getId())
                .setFormattedSize(StringUtils.getFormattedSize(contentLength));
        objectInfoDaoService.save(objectInfo);

        // 引用+1
        return objectHashService.increaseRefCountByHash(hash);
    }
}
