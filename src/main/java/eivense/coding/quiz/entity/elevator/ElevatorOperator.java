package eivense.coding.quiz.entity.elevator;

import eivense.coding.quiz.entity.Prediction;
import eivense.coding.quiz.entity.floor.Floor;
import eivense.coding.quiz.entity.passenger.Passenger;

import java.util.List;

/**
 * 电梯操作接口
 */
public interface ElevatorOperator {

    /**
     * 获取控制的电梯
     *
     * @return 电梯
     */
    Elevator getElevator();

    /**
     * 处理进入电梯
     *
     * @param floor 当前楼层
     * @return 进入电梯的乘客
     */
    List<Passenger> handleGetIn(Floor floor);

    /**
     * 处理离开电梯
     *
     * @param floor 当前楼层
     * @return 返回下电梯的乘客
     */
    List<Passenger> handleGetOut(Floor floor) ;


    /**
     * 移动电梯
     */
    void moveElevator();

    /**
     * 计算代价
     *
     * @param startTask 到该层的任务
     *
     * @return 预估数值
     */
    Prediction calculateCost(Task startTask);
}
