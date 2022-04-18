package eivense.elevator.service;

import eivense.elevator.entity.elevator.ElevatorInfo;
import eivense.elevator.entity.passenger.Passenger;

import java.util.List;

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
