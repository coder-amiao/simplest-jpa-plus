package cn.soboys.simplestjpa;


import cn.soboys.simplestjpa.config.TenantProperties;
import cn.soboys.simplestjpa.exception.UpdateException;
import cn.soboys.simplestjpa.plugin.TenantFactory;
import com.querydsl.core.types.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import org.dromara.hutool.core.array.ArrayUtil;
import org.dromara.hutool.core.bean.BeanUtil;
import org.dromara.hutool.core.bean.copier.CopyOptions;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.exception.ExceptionUtil;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.extra.spring.SpringUtil;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.transaction.annotation.Transactional;


import javax.persistence.EntityManager;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author 公众号 程序员三时
 * @version 1.0
 * @date 2023/7/24 18:29
 * @webSite https://github.com/coder-amiao
 */
public interface IService<T, ID extends Serializable> {

    int DEFAULT_BATCH_SIZE = 1000;

    // ===== 保存（增）操作 =====

    /**
     * <p>获取对应实体类（Entity）的基础映射类（BaseMapper）。
     *
     * @return 基础映射类（BaseMapper）
     */

    BaseRepository<T, ID> getRepository();


    /**
     * queryDSL 查询器工厂 JPAQueryFactory
     *
     * @return
     */
    JPAQueryFactory queryChain();


    /**
     * queryDSL 更新
     *
     * @return
     */
    JPAUpdateClause updateChain(EntityPath entityPath);


    /**
     * JPA数据持久操作API  EntityManager
     *
     * @return
     */
    EntityManager getEntityManager();


    /**
     * JPA 实体类的元数据，如实体类的Java类型，ID的类型等
     */
    //JpaEntityInformation<T, ?> getJpaEntityInformation();

    /**
     * 插入一条记录
     *
     * @param entity 实体对象
     * @return T
     */
    default T save(T entity) {
        TenantProperties tenantProperties = SpringUtil.getBean(TenantProperties.class);
        if (tenantProperties != null && tenantProperties.getEnableTenant() && tenantProperties.getTables().size() > 0) {
            String tenant = StrUtil.toCamelCase(tenantProperties.getTenantIdColumn());
            TenantFactory tenantFactory = SpringUtil.getBean("tenantFactory");
            try {
                Field field = entity.getClass().getDeclaredField(tenant);
                field.setAccessible(true);
                if (tenantFactory != null) field.set(entity, tenantFactory.getTenantId());
            } catch (Exception e) {

            }
        }
        return getRepository().save(entity);
    }

    ;

    /**
     * <p>批量保存实体类对象数据。
     *
     * @param entities 实体类对象
     * @return {@code true} 保存成功，{@code false} 保存失败。
     */
    default List<T> saveBatch(Collection<T> entities) {
        return getRepository().saveAll(entities);
    }

    /**
     * @param entity 实体类对象
     * @param entity 查询条件  根据实体ID 更新。 全量覆盖
     * @return T
     */
    @Transactional
    default T update(T entity) {

        JpaEntityInformation entityInformation = JpaEntityInformationSupport.getEntityInformation(entity.getClass(), getEntityManager());
        entityInformation.getRequiredId(entity);
        return getEntityManager().merge(entity);
    }

    /**
     * @param entity 实体类对象
     * @param entity 查询条件  根据实体ID更新。自定义忽略null值
     * @return T
     */
    @Transactional
    default T update(T entity, Boolean ignore) {

        JpaEntityInformation entityInformation = JpaEntityInformationSupport.getEntityInformation(entity.getClass(), getEntityManager());
        entityInformation.getRequiredId(entity);
        if (!ignore) {
            return getEntityManager().merge(entity);
        } else if (ignore) {
            Object id = entityInformation.getId(entity);
            T existingEntity = getRepository().findById((ID) id).orElseThrow(() -> new IllegalArgumentException("Entity not found"));
            CopyOptions copyOptions = new CopyOptions();
            copyOptions.setIgnoreCase(false);
            copyOptions.setIgnoreNullValue(true);

            //忽略null 属性
            Map<String, Object> stringObjectMap = BeanUtil.beanToMap(entity, true, true);
            String[] IgnoreProperties = (stringObjectMap.keySet()).toArray(new String[0]);
            copyOptions.setIgnoreProperties(IgnoreProperties);
            BeanUtil.copyProperties(existingEntity, entity, copyOptions);
            return getEntityManager().merge(entity);
        } else {
            return getEntityManager().merge(entity);
        }
    }


    /**
     * @param entity 实体类对象
     * @param entity 查询条件  根据实体ID 更新。 全量覆盖
     * @return T
     */
    @Transactional
    default T update(T entity, Boolean ignore, String[] ignoreProperties) {

        JpaEntityInformation entityInformation = JpaEntityInformationSupport.getEntityInformation(entity.getClass(), getEntityManager());
        entityInformation.getRequiredId(entity);
        if (!ignore) {
            return getEntityManager().merge(entity);
        } else if (ignore) {
            Object id = entityInformation.getId(entity);
            T existingEntity = getRepository().findById((ID) id).orElseThrow(() -> new IllegalArgumentException("Entity not found"));
            CopyOptions copyOptions = new CopyOptions();
            copyOptions.setIgnoreCase(false);
            copyOptions.setIgnoreNullValue(true);

            //忽略null 属性
            Map<String, Object> stringObjectMap = BeanUtil.beanToMap(entity, true, true);
            String[] entityIgnoreProperties = (stringObjectMap.keySet()).toArray(new String[0]);
            String[] CustomIgnoreProperties = ArrayUtil.addAll(entityIgnoreProperties, ignoreProperties);
            copyOptions.setIgnoreProperties(CustomIgnoreProperties);
            BeanUtil.copyProperties(existingEntity, entity, copyOptions);
            return getEntityManager().merge(entity);
        } else {
            return getEntityManager().merge(entity);
        }
    }


    /**
     * <p>保存或者更新实体类对象数据。
     *
     * @param entity 实体类对象
     * @return {@code true} 保存或更新成功，{@code false} 保存或更新失败。
     * @apiNote 如果实体类对象主键有值，则更新数据，若没有值，则保存数据。
     */
    @Transactional
    default T saveOrUpdate(T entity) {
        JpaEntityInformation entityInformation = JpaEntityInformationSupport.getEntityInformation(entity.getClass(), getEntityManager());
        if (entityInformation.isNew(entity)) {
            getEntityManager().persist(entity);
            return entity;
        } else {
            return getEntityManager().merge(entity);
        }
    }

    /**
     * 插入或者更新，若主键有值，则更新，若没有主键值，则插入
     *
     * @param entity
     * @return 更新会忽略 null 值。
     */
    @Transactional
    default T saveOrUpdateSelective(T entity) {
        JpaEntityInformation entityInformation = JpaEntityInformationSupport.getEntityInformation(entity.getClass(), getEntityManager());
        if (entityInformation.isNew(entity)) {
            getEntityManager().persist(entity);
            return entity;
        } else {
            return this.update(entity, true);
        }
    }

    /**
     * <p>根据 {@link Map} 构建查询条件更新数据。
     *
     * @param query 查询条件
     * @return {@code true} 更新成功，{@code false} 更新失败。
     */
    @Transactional
    default boolean update(JPAUpdateClause query) {
        long count = query.execute();
        if (count > 0) {
            return true;
        }
        return false;
    }

    /**
     * <p>查询所有数据数量。
     *
     * @return 所有数据数量
     */
    default long count() {
        return getRepository().count();
    }

    /**
     * <p>根据查询条件查询数据数量。
     *
     * @param example 查询条件
     * @return 数据数量
     */
    default long count(Example<T> example) {
        return getRepository().count(example);
    }


    // ===== 查询（查）操作 =====

    /**
     * <p>根据数据主键查询一条数据。
     *
     * @param id 数据主键
     * @return 查询结果数据
     */
    default T getById(ID id) {
        return getRepository().findById(id).get();
    }

    /**
     * <p>根据数据主键查询一条数据。
     *
     * @param id 数据主键
     * @return 查询结果数据
     * @apiNote 该方法会将查询结果封装为 {@link Optional} 类进行返回，方便链式操作。
     */
    default Optional<T> getByIdOpt(ID id) {
        return getRepository().findById(id);
    }

    /**
     * <p>根据查询条件查询一条数据。
     *
     * @param example 查询条件
     * @return 查询结果数据
     * @apiNote 该方法会将查询结果封装为 {@link Optional} 类进行返回，方便链式操作。
     */
    default Optional<T> getOneOpt(Example<T> example) {
        return getRepository().findOne(example);
    }

    /**
     * <p>根据查询条件查询一条数据。
     *
     * @param example 查询条件
     * @return 查询结果数据
     * Example.of(T)
     */
    default T getOne(Example<T> example) {
        return getRepository().findOne(example).get();
    }

    /**
     * <p>根据查询条件查询一条数据。
     *
     * @param query 查询条件
     * @return 查询结果数据
     */
    default T getOne(Predicate query) {
        return getRepository().findOne(query).get();
    }

    /**
     * <p>根据查询条件查询数据数量。
     *
     * @param query 查询条件
     * @return 数据数量
     */
    default long count(Predicate query) {
        return getRepository().count(query);
    }


    /**
     * <p>根据查询条件判断数据是否存在。
     *
     * @param query 查询条件
     * @return {@code true} 数据存在，{@code false} 数据不存在。
     */
    default boolean exists(Predicate query) {
        return getRepository().exists(query);
    }

    /**
     * <p>根据查询条件判断数据是否存在。
     *
     * @param condition 查询条件
     * @return {@code true} 数据存在，{@code false} 数据不存在。
     */
    default boolean exists(Example condition) {
        return getRepository().exists(condition);
    }


    /**
     * 根据ID判断是否存在
     *
     * @param id
     * @return
     */
    default boolean existsById(ID id) {
        return getRepository().existsById(id);
    }

    /**
     * <p>查询所有数据。
     *
     * @return 所有数据
     */
    default List<T> list() {
        return getRepository().findAll();
    }

    /**
     * <p>根据查询条件查询数据集合。
     *
     * @param query 查询条件
     * @return 数据集合
     */
    default List<T> list(Predicate query) {
        return StreamSupport.stream(getRepository().findAll(query).spliterator(), false)
                .collect(Collectors.toList());
    }

    /**
     * <p>根据查询条件查询数据集合。
     *
     * @param query 查询条件
     * @return 数据集合
     */
    default List<T> list(Example query) {
        return getRepository().findAll(query);
    }

    /**
     * <p>根据数据主键查询数据集合。
     *
     * @param ids 数据主键
     * @return 数据集合
     */
    default List<T> listByIds(Collection<ID> ids) {
        return getRepository().findAllById(ids);
    }

    // ===== 分页查询操作 =====

    /**
     * <p>分页查询所有数据。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    default Page<T> page(Pageable page) {
        return getRepository().findAll(page);
    }


    /**
     * <p>根据查询条件分页查询数据。
     *
     * @param page  分页对象
     * @param query 查询条件
     * @return 分页对象
     */
    default Page<T> page(Pageable page, Predicate query) {
        return getRepository().findAll(query, page);
    }


    // ===== 删除（删）操作 =====

    /**
     * <p>根据数据主键删除数据。
     *
     * @param id 数据主键
     * @return {@code true} 删除成功，{@code false} 删除失败。
     */
    default boolean removeById(ID id) {
        boolean success = true;
        if (StrUtil.isBlankIfStr(id)) {
            return false;
        }
        try {
            getRepository().deleteById(id);
        } catch (Exception e) {
            success = false;
            new UpdateException(ExceptionUtil.stacktraceToString(e));
        }
        return success;
    }

    /**
     * <p>根据数据主键批量删除数据。
     *
     * @param ids 数据主键
     * @return {@code true} 删除成功，{@code false} 删除失败。
     */
    default boolean removeByIds(Collection<? extends ID> ids) {
        boolean success = true;
        if (CollUtil.isEmpty(ids)) {
            return false;
        }
        try {
            getRepository().deleteAllById(ids);
        } catch (Exception e) {
            success = false;
            new UpdateException(ExceptionUtil.stacktraceToString(e));
        }
        return success;
    }


    /**
     * <p>根据Entity 注解 查询条件删除数据。  以ID为主键删除。
     *
     * @param entity
     * @return {@code true} 删除成功，{@code false} 删除失败。
     */
    default boolean remove(T entity) {
        boolean success = true;
        try {
            getRepository().delete(entity);
        } catch (Exception e) {
            success = false;
            new UpdateException(ExceptionUtil.stacktraceToString(e));
        }
        return success;
    }


    /**
     * <p>根据查询条件删除数据。
     *
     * @param entities 实体集合 根据集合ID主键
     * @return {@code true} 删除成功，{@code false} 删除失败。
     */
    default boolean remove(Collection<T> entities) {
        boolean success = true;
        if (CollUtil.isEmpty(entities)) {
            return false;
        }
        try {
            getRepository().deleteAllInBatch(entities);
        } catch (Exception e) {
            success = false;
            new UpdateException(ExceptionUtil.stacktraceToString(e));
        }
        return success;
    }

    /**
     * <p>根据查询条件删除数据。
     *
     * @param entities 实体集合 根据集合ID主键
     * @return {@code true} 删除成功，{@code false} 删除失败。
     */
    default boolean removeAll(Collection<? extends T> entities) {
        boolean success = true;
        if (CollUtil.isEmpty(entities)) {
            return false;
        }
        try {
            getRepository().deleteAll(entities);
        } catch (Exception e) {
            success = false;
            new UpdateException(ExceptionUtil.stacktraceToString(e));
        }
        return success;
    }


    /**
     * @return {@code true} 删除成功，{@code false} 删除失败。
     */
    default boolean remove() {
        boolean success = true;
        try {
            getRepository().deleteAll();
        } catch (Exception e) {
            success = false;
            new UpdateException(ExceptionUtil.stacktraceToString(e));
        }
        return success;
    }

}
