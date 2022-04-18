package eivense.coding.quiz.service;

import eivense.coding.quiz.entity.elevator.ElevatorInfo;
import eivense.coding.quiz.entity.passenger.Passenger;

import java.util.List;
import java.util.Set;

/**
 * 系统控制接口
 */
public interface SystemService {

    /**
     * 重置电梯
     *
     * @return 电梯状态
     */
    List<ElevatorInfo> reset();

    /**
     * 运行电梯
     *
     * @param passengers 乘客
     * @return 电梯状态
     */
    List<ElevatorInfo> workload(List<Passenger> passengers);
}
