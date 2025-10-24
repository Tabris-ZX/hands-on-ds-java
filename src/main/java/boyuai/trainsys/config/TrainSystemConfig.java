package boyuai.trainsys.config;

import boyuai.trainsys.core.TrainSystem;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 火车票务系统配置类
 */
@Configuration
public class TrainSystemConfig {

    @Bean
    public TrainSystem trainSystem() {
        return new TrainSystem();
    }
}
