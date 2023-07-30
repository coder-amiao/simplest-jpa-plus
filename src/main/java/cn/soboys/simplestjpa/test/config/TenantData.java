package cn.soboys.simplestjpa.test.config;

import cn.soboys.simplestjpa.plugin.TenantFactory;

/**
 * @author 公众号 程序员三时
 * @version 1.0
 * @date 2023/7/30 15:35
 * @webSite https://github.com/coder-amiao
 */
public class TenantData  implements TenantFactory {

    @Override
    public String getTenantId() {
        return "租户解析";
    }
}
