package cn.soboys.simplestjpa.test;

import javax.persistence.EntityManager;

/**
 * @author 公众号 程序员三时
 * @version 1.0
 * @date 2023/7/27 17:15
 * @webSite https://github.com/coder-amiao
 */
public class JpaDaoHelper {
    private static EntityManager entityManager;

    public static void setEntityManager(EntityManager entityManager) {
        JpaDaoHelper.entityManager = entityManager;
    }

    public static EntityManager getEntityManager() {
        return entityManager;
    }
}

