package com.takeaway.intercepter;

import org.hibernate.resource.jdbc.spi.StatementInspector;

/**
 * @author sunqichen
 * @version 0.1
 * @ClassName:
 * @Description:
 * @date
 * @since 0.1
 */
public class JpaSqlIntercepter implements StatementInspector {

    @Override
    public String inspect(String sql) {
        // 拦截并打印 SQL
        System.out.println("Intercepted SQL: " + sql);

        // 可以在这里修改 SQL 或记录日志
        return sql; // 返回原始 SQL 或修改后的 SQL
    }
}
