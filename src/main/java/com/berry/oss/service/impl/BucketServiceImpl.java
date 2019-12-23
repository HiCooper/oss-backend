package com.berry.oss.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.berry.oss.common.ResultCode;
import com.berry.oss.common.exceptions.BaseException;
import com.berry.oss.common.utils.StringUtils;
import com.berry.oss.dao.entity.BucketInfo;
import com.berry.oss.dao.entity.RefererInfo;
import com.berry.oss.dao.entity.RegionInfo;
import com.berry.oss.dao.service.IBucketInfoDaoService;
import com.berry.oss.dao.service.IRefererInfoDaoService;
import com.berry.oss.dao.service.IRegionInfoDaoService;
import com.berry.oss.module.dto.BucketStatisticsInfoDto;
import com.berry.oss.module.vo.BucketInfoVo;
import com.berry.oss.module.vo.BucketStatisticsInfoVo;
import com.berry.oss.module.vo.RefererDetailVo;
import com.berry.oss.security.SecurityUtils;
import com.berry.oss.security.dto.UserInfoDTO;
import com.berry.oss.service.IBucketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019-06-04 22:33
 * fileName：BucketServiceImpl
 * Use：
 */
@Service
public class BucketServiceImpl implements IBucketService {

    private final IBucketInfoDaoService bucketInfoDaoService;
    private final IRegionInfoDaoService regionInfoDaoService;
    private final IRefererInfoDaoService refererInfoDaoService;

    @Autowired
    public BucketServiceImpl(IBucketInfoDaoService bucketInfoDaoService, IRegionInfoDaoService regionInfoDaoService, IRefererInfoDaoService refererInfoDaoService) {
        this.bucketInfoDaoService = bucketInfoDaoService;
        this.regionInfoDaoService = regionInfoDaoService;
        this.refererInfoDaoService = refererInfoDaoService;
    }

    @Override
    public List<BucketInfoVo> listBucket(Integer userId, String name) {
        return bucketInfoDaoService.listBucket(userId, name);
    }

    @Override
    public void create(String name, String region, String acl) {
        UserInfoDTO currentUser = SecurityUtils.getCurrentUser();
        // 检查 region
        RegionInfo regionInfo = regionInfoDaoService.getOne(new QueryWrapper<RegionInfo>().eq("code", region));
        if (regionInfo == null) {
            throw new BaseException("404", "region 不存在");
        }

        // 检查该 bucket 名称是否被占用, 全局 bucket 命名唯一
        boolean result = checkBucketNotExist(name);
        if (!result) {
            throw new BaseException("403", "该Bucket名字已被占用");
        }
        BucketInfo bucketInfo = new BucketInfo()
                .setName(name)
                .setAcl(acl)
                .setRegionId(regionInfo.getId())
                .setUserId(currentUser.getId());
        bucketInfoDaoService.save(bucketInfo);
    }

    @Override
    public BucketInfo checkUserHaveBucket(String bucketName) {
        UserInfoDTO currentUser = SecurityUtils.getCurrentUser();
        BucketInfo bucketInfo = bucketInfoDaoService.getOne(new QueryWrapper<BucketInfo>()
                .eq("name", bucketName)
                .eq("user_id", currentUser.getId())
        );
        if (null == bucketInfo) {
            throw new BaseException(ResultCode.BUCKET_NOT_EXIST);
        }
        return bucketInfo;
    }

    @Override
    public boolean checkBucketNotExist(String bucketName) {
        int count = bucketInfoDaoService.count(new QueryWrapper<BucketInfo>().eq("name", bucketName));
        return 0 == count;
    }

    @Override
    public boolean checkUserHaveBucket(Integer userId, String bucket) {
        int count = bucketInfoDaoService.count(new QueryWrapper<BucketInfo>().eq("name", bucket)
                .eq("user_id", userId));
        return 1 == count;
    }

    @Override
    public RefererDetailVo getReferer(String bucketId) {
        RefererInfo refererInfo = refererInfoDaoService.getOne(new QueryWrapper<RefererInfo>().eq("bucket_id", bucketId));
        if (refererInfo == null) {
            return null;
        }
        RefererDetailVo vo = new RefererDetailVo();
        vo.setId(refererInfo.getId());
        vo.setAllowEmpty(refererInfo.getAllowEmpty());
        vo.setWhiteList(refererInfo.getWhiteList());
        return vo;
    }

    @Override
    public void updateReferer(String bucketId, Integer id, Boolean allowEmpty, String whiteList) {
        RefererInfo refererInfo = new RefererInfo();
        refererInfo.setId(id);
        refererInfo.setAllowEmpty(allowEmpty);
        refererInfo.setWhiteList(whiteList);
        refererInfo.setBucketId(bucketId);
        refererInfoDaoService.saveOrUpdate(refererInfo);
    }

    @Override
    public Map<String, Object> getBucketUseInfo() {
        UserInfoDTO currentUser = SecurityUtils.getCurrentUser();
        List<BucketStatisticsInfoDto> dtos = bucketInfoDaoService.getBucketUseInfo(currentUser.getId());
        Map<String, Long> totalCountMap = new HashMap<>(16);
        dtos.forEach(i -> {
            setMapValue("maxSize", "max", totalCountMap, i.getObjectMaxSize());
            setMapValue("minSize", "min", totalCountMap, i.getObjectMinSize());
            setMapValue("totalUsed", "sum", totalCountMap, i.getUsedCapacity());
            setMapValue("totalObjectCount", "sum", totalCountMap, i.getObjectCount());
            setMapValue("allAverage", "average", totalCountMap, i.getObjectAverageSize());
        });
        Map<String, Object> formatTotalCountMap = new HashMap<>(16);
        for (Map.Entry<String, Long> entry : totalCountMap.entrySet()) {
            String key = entry.getKey();
            Long value = entry.getValue();
            if (!"totalObjectCount".equals(key)) {
                formatTotalCountMap.put(key, StringUtils.getFormattedSize(value));
            } else {
                formatTotalCountMap.put(key, value);
            }
        }
        formatTotalCountMap.put("bucketCount", dtos.size());
        // 暂时设置 最大容量为 60G，这里暂没有任何意义，不做判断，只为了 显示和后续
        formatTotalCountMap.put("capacity", "60G");
        List<BucketStatisticsInfoVo> collect = dtos.stream().map(item -> {
            BucketStatisticsInfoVo vo = new BucketStatisticsInfoVo();
            vo.setBucketName(item.getBucketName());
            vo.setObjectCount(item.getObjectCount());
            vo.setObjectAverageSize(StringUtils.getFormattedSize(item.getObjectAverageSize()));
            vo.setObjectMaxSize(StringUtils.getFormattedSize(item.getObjectMaxSize()));
            vo.setObjectMinSize(StringUtils.getFormattedSize(item.getObjectMinSize()));
            vo.setUsedCapacity(StringUtils.getFormattedSize(item.getUsedCapacity()));
            return vo;
        }).collect(Collectors.toList());
        Map<String, Object> result = new HashMap<>(16);
        result.put("detail", collect);
        result.put("total", formatTotalCountMap);
        return result;
    }

    private void setMapValue(String key, String calculateType, Map<String, Long> totalCountMap, Long val) {
        Long value = totalCountMap.get(key) == null ? 0L : totalCountMap.get(key);
        switch (calculateType) {
            case "max":
                value = Math.max(val, value);
                break;
            case "min":
                if (value == 0) {
                    value = val;
                } else {
                    value = Math.min(val, value);
                }
                break;
            case "sum":
                value += val;
                break;
            case "average":
                if (value != 0 && val != 0) {
                    value = (val + value) / 2;
                } else {
                    value = val;
                }
                break;
            default:
                value = val;
        }
        totalCountMap.put(key, value);
    }
}
