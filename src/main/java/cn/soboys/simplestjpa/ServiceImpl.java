package cn.soboys.simplestjpa;


import com.querydsl.core.types.EntityPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import org.springframework.beans.factory.annotation.Autowired;


import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * @author 公众号 程序员三时
 * @version 1.0
 * @date 2023/7/24 18:29
 * @webSite https://github.com/coder-amiao
 */
public class ServiceImpl<R extends BaseRepository<T, ID>,
        T, ID extends Serializable> implements IService<T, ID> {



    @Autowired(required = false)
    protected R repository;

    @Autowired(required = false)
    private EntityManager entityManager;


    @Autowired(required = false)
    private JPAQueryFactory jpaQueryFactory;

    @Override
    public BaseRepository<T, ID> getRepository() {
        return repository;
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public JPAQueryFactory queryChain() {
        return jpaQueryFactory;
    }

    @Override
    public JPAUpdateClause updateChain(EntityPath entityPath) {
        return jpaQueryFactory.update(entityPath);
    }



}
