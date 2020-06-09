package com.berry.oss.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.berry.oss.common.Result;
import com.berry.oss.common.ResultFactory;
import com.berry.oss.dao.entity.RegionInfo;
import com.berry.oss.dao.service.IRegionInfoDaoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2020/6/9 14:28
 * fileName：RegionController
 * Use：
 */
@RestController
@RequestMapping("ajax/region")
@Api(tags = "Region 管理")
public class RegionController {

    @Autowired
    private IRegionInfoDaoService regionInfoDaoService;

    @ApiOperation("分页查询 区域列表")
    @GetMapping("/page_list")
    public Result pageListRegion(@RequestParam("pageSize") Integer pageSize,
                                 @RequestParam("pageNum") Integer pageNum,
                                 @RequestParam("keyword") String keyword) {
        IPage<RegionInfo> page = new Page<>(pageNum, pageSize);
        QueryWrapper<RegionInfo> queryWrapper = new QueryWrapper<>();
        if (isNotBlank(keyword)) {
            queryWrapper.like("name", keyword)
                    .or()
                    .like("code", keyword);
        }
        regionInfoDaoService.page(page, queryWrapper);
        return ResultFactory.wrapper(page);
    }
}
