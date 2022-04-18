package eivense.elevator.entity.elevator;

import eivense.elevator.entity.passenger.Passenger;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

/**
 * 电梯状态
 * 接口响应的结构
 */
@Data
@AllArgsConstructor
public class ElevatorInfo {

    /**
     * 电梯的id
     */
    private int id;

    /**
     * 电梯当前所在的楼层
     */
    private int floor;

    /**
     * 当前电梯中的乘客
     */
    private Set<Passenger> users;

}
