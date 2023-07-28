package cn.soboys.simplestjpa;

import com.querydsl.core.types.EntityPath;
import com.querydsl.jpa.impl.JPAUpdateClause;
import org.dromara.hutool.extra.spring.SpringUtil;

/**
 * @author 公众号 程序员三时
 * @version 1.0
 * @date 2023/7/28 14:34
 * @webSite https://github.com/coder-amiao
 */
public class UpdateWrapper {

    public static JPAUpdateClause of(EntityPath entityPath){
        return SpringUtil.getBean(IService.class).updateChain(entityPath);
    }
}
