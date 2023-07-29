package cn.soboys.simplestjpa;

import com.querydsl.core.types.EntityPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import org.dromara.hutool.extra.spring.SpringUtil;

/**
 * @author 公众号 程序员三时
 * @version 1.0
 * @date 2023/7/28 14:34
 * @webSite https://github.com/coder-amiao
 */
public class QueryWrapper {
    public static JPAQueryFactory of() {
        return SpringUtil.getBean(IService.class).queryChain();
    }
}
