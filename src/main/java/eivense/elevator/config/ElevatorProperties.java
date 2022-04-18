package eivense.elevator.config;


import lombok.Data;

/**
 * 电梯属性
 */
@Data
public class ElevatorProperties {

    /**
     * 电梯最大乘客数
     */
    private int maxPassengers = 20;

    /**
     * 电梯向上运行时的能耗
     */
    private double upCost = 0.6;

    /**
     * 电梯向下运行时的能耗
     */
    private double downCost = 0.4;

    /**
     * 时间消耗和能耗的占比
     */
    private double ratio = 0.5;
}
