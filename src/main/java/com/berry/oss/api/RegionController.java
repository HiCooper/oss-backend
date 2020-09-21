package com.berry.oss.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.berry.oss.common.Result;
import com.berry.oss.common.ResultFactory;
import com.berry.oss.dao.entity.RegionInfo;
import com.berry.oss.dao.entity.ServerInfo;
import com.berry.oss.dao.service.IRegionInfoDaoService;
import com.berry.oss.dao.service.IServerInfoDaoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
@RequestMapping("ajax/region_server")
@Api(tags = "Region 与 服务终端 管理")
public class RegionController {

    @Autowired
    private IRegionInfoDaoService regionInfoDaoService;

    @Autowired
    private IServerInfoDaoService serverInfoDaoService;

    @ApiOperation("分页查询 区域列表")
    @GetMapping("/page_list_region")
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

    @ApiOperation("查询region关联服务器列表")
    @GetMapping("/list_server_by_region")
    public Result listServerListByRegion(@RequestParam("regionId") String regionId) {
        List<ServerInfo> res = serverInfoDaoService.listServerListByRegion(regionId);
        return ResultFactory.wrapper(res);
    }

    @ApiOperation("分页查询 服务终端列表")
    @GetMapping("/page_list_server")
    public Result pageListServer(@RequestParam("pageSize") Integer pageSize,
                                 @RequestParam("pageNum") Integer pageNum,
                                 @RequestParam("keyword") String keyword) {
        IPage<ServerInfo> page = new Page<>(pageNum, pageSize);
        QueryWrapper<ServerInfo> queryWrapper = new QueryWrapper<>();
        if (isNotBlank(keyword)) {
            queryWrapper.like("ip", keyword)
                    .or()
                    .like("remark", keyword);
        }
        serverInfoDaoService.page(page, queryWrapper);
        return ResultFactory.wrapper(page);
    }
}
