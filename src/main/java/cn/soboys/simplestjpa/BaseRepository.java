package cn.soboys.simplestjpa;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;


/**
 * @author 公众号 程序员三时
 * @version 1.0
 * @date 2023/7/24 18:30
 * @webSite https://github.com/coder-amiao
 */
@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends JpaRepository<T, ID>, QuerydslPredicateExecutor<T> {

}
