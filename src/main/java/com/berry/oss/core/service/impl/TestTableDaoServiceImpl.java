package com.berry.oss.core.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.berry.oss.core.entity.TestTable;
import com.berry.oss.core.mapper.TestTableMapper;
import com.berry.oss.core.service.ITestTableDaoService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author HiCooper
 * @since 2018-11-17
 */
@Service
public class TestTableDaoServiceImpl extends ServiceImpl<TestTableMapper, TestTable> implements ITestTableDaoService {

}
