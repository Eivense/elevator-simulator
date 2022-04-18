package eivense.elevator.service;

import eivense.elevator.config.ElevatorProperties;
import eivense.elevator.entity.elevator.Elevator;
import eivense.elevator.entity.elevator.ElevatorInfo;
import eivense.elevator.entity.passenger.Passenger;

import java.util.List;

/**
 * 电梯控制接口
 */
public interface ElevatorService {


    /**
     * 注册电梯
     *
     * @param elevator 电梯
     * @param minFloor 该电梯能到的最低楼层
     * @param maxFloor 该电梯能到的最高楼层
     * @param properties 相关配置
     */
    void registerElevator(Elevator elevator, int minFloor, int maxFloor, ElevatorProperties properties);

    /**
     * 处理乘客的请求
     * 为每位乘客的选择一部电梯响应其请求
     *
     * @param passengers 乘客
     * @param worldClock 时间
     */
    void processPassengers(List<Passenger> passengers, int worldClock);

    /**
     * 处理所有电梯电梯
     *
     * @return 已经离开的乘客
     */
    List<Passenger>  processAllElevator();

    /**
     * 获取全部电梯的信息
     *
     * @return 电梯信息
     */
    List<ElevatorInfo> getElevatorInfo();
}
