package eivense.elevator.entity;

import eivense.elevator.entity.elevator.Elevator;
import eivense.elevator.entity.elevator.Task;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Queue;

/**
 * 预测结果
 */
@Data
@AllArgsConstructor
public class Prediction {

    /**
     * 对应电梯
     */
    private Elevator elevator;

    /**
     * 该方案的预估值
     *
     * 越小越好
     */
    private double cost;

    /**
     * 该方案对应的队列
     *
     * 已经排序好的队列
     */
    private Queue<Task> taskQueue;

}
