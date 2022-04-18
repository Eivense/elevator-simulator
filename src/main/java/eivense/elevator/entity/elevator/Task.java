package eivense.elevator.entity.elevator;

import eivense.elevator.entity.Direction;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 电梯任务
 */
@Data
@AllArgsConstructor
public class Task {

    /**
     * 目标楼层
     */
    private int floor;

    /**
     * 运行方向
     */
    private Direction direction;


}
