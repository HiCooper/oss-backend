package com.berry.oss.api;


import com.berry.oss.common.Result;
import com.berry.oss.common.ResultFactory;
import com.berry.oss.dao.entity.WormStrategy;
import com.berry.oss.module.mo.InitWormStrategyMo;
import com.berry.oss.service.IWormStrategyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  合规保留策略：WORM（Write Once Read Many）
 * </p>
 *
 * @author HiCooper
 * @since 2020-09-21
 */
@RestController
@RequestMapping("/ajax/worm_strategy")
@Api(tags = "合规保留策略")
public class WormStrategyController {

    @Autowired
    private IWormStrategyService wormStrategyService;

    @ApiOperation("新建合规保留策略")
    @PostMapping("/init")
    public Result initWormStrategy(@Validated @RequestBody InitWormStrategyMo mo) {
        WormStrategy wormStrategy = wormStrategyService.initWormStrategy(mo.getBucket(), mo.getDays());
        return ResultFactory.wrapper(wormStrategy);
    }

    @ApiOperation("取消未锁定的合规保留策略")
    @PostMapping("/abort")
    public Result abortWormStrategy() {
        return ResultFactory.wrapper();
    }

    @ApiOperation("锁定合规保留策略")
    @PostMapping("/complete")
    public Result completeWormStrategy() {
        return ResultFactory.wrapper();
    }

    @ApiOperation("获取合规保留策略")
    @GetMapping("/detail")
    public Result detailWormStrategy() {
        return ResultFactory.wrapper();
    }

    @ApiOperation("延长Object的保留天数")
    @PostMapping("/extend_days")
    public Result extendWormStrategyDays() {
        return ResultFactory.wrapper();
    }

}
