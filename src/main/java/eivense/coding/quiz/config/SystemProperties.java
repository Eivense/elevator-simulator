package eivense.coding.quiz.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * 系统配置
 */
@Data
@ConfigurationProperties(prefix = "control.system")
public class SystemProperties {

    /**
     * 最低楼层号 默认为1
     */
    private int minFloor = 1;

    /**
     * 最高楼层 默认为11
     */
    private int maxFloor = 11;

    /**
     * 电梯数量 默认为3
     */
    private int elevatorNum = 3;

    /**
     * 电梯相关属性
     */
    @NestedConfigurationProperty
    private ElevatorProperties properties = new ElevatorProperties();


}
