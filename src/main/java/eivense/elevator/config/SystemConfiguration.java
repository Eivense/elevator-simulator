package eivense.elevator.config;


import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties(SystemProperties.class)
public class SystemConfiguration {


}
