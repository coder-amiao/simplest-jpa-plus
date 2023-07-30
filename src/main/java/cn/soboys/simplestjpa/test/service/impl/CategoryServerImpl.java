package cn.soboys.simplestjpa.test.service.impl;

import cn.soboys.simplestjpa.ServiceImpl;
import cn.soboys.simplestjpa.test.entity.Category;
import cn.soboys.simplestjpa.test.repository.CategoryRepository;
import cn.soboys.simplestjpa.test.service.ICategoryService;
import org.springframework.stereotype.Service;

/**
 * @author 公众号 程序员三时
 * @version 1.0
 * @date 2023/7/20 14:46
 * @webSite https://github.com/coder-amiao
 */
@Service
public class CategoryServerImpl extends ServiceImpl<CategoryRepository, Category, Long> implements ICategoryService {


}
