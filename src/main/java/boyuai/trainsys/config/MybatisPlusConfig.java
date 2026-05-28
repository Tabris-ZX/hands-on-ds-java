package boyuai.trainsys.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("boyuai.trainsys.dao.mapper")
public class MybatisPlusConfig {
}
