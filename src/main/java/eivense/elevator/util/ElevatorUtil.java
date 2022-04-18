package eivense.elevator.util;

import eivense.elevator.entity.elevator.Elevator;
import eivense.elevator.entity.elevator.ElevatorInfo;
import eivense.elevator.entity.elevator.ElevatorState;
import eivense.elevator.entity.Direction;
import eivense.elevator.entity.elevator.Task;
import eivense.elevator.entity.passenger.Passenger;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.text.DecimalFormat;
import java.util.*;

/**
 * 工具类
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ElevatorUtil {


    /**
     * 转换电梯信息
     *
     * @param elevator 电梯
     * @return ElevatorInfo
     */
    public static ElevatorInfo getInfo(Elevator elevator) {
        return new ElevatorInfo(elevator.getId(), elevator.getCurFloor(), elevator.getUsers());
    }


    /**
     * 拼接名称
     *
     * @param passengers 乘客
     * @return 名称
     */
    public static String getPassengerName(List<Passenger> passengers) {
        StringBuilder builder = new StringBuilder();
        for (Passenger passenger : passengers) {
            builder.append(passenger.getUser()).append(" ");
        }
        return builder.toString();
    }

    /**
     * 判断乘客方向
     *
     * @param passenger 乘客
     * @return 方向
     */
    public static Direction getPassengerDirection(Passenger passenger) {
        int startFloor = passenger.getStartFloor();
        int endFloor = passenger.getEndFloor();
        if (startFloor < endFloor) {
            return Direction.UP;
        } else if (startFloor > endFloor) {
            return Direction.DOWN;
        }
        throw new IllegalArgumentException("StartFloor and EndFloor can not be equal");
    }


    /**
     * 对任务进行排序
     * <p>
     * 类似磁盘寻道的电梯调度算法
     *
     * 三次遍历 O(n)
     *
     * @param taskQueue 任务队列
     * @param state     状态
     * @param curFloor  当前层
     * @return 新的任务队列
     */
    public static Queue<Task> sortTask(Queue<Task> taskQueue, ElevatorState state, int curFloor) {
        // 暂存任务
        List<Task> tasks = new ArrayList<>(taskQueue);
        // 新任务队列
        Deque<Task> newQueue = new ArrayDeque<>();
        if (state == ElevatorState.UP) {
            // 向上运行则按照升序排序
            tasks.sort(Comparator.comparingInt(Task::getFloor));
            // 正向遍历 选择当前楼层之上
            for (Task task : tasks) {
                if (task.getFloor() >= curFloor && task.getDirection() == Direction.UP) {
                    newQueue.add(task);
                }
            }
            // 反向遍历选择向下的任务
            for (int i = tasks.size() - 1; i >= 0; i--) {
                Task task = tasks.get(i);
                if (task.getDirection() == Direction.DOWN) {
                    newQueue.add(task);
                }
            }
            // 正向遍历 选择在当前楼层之下的
            for (Task task : tasks) {
                if (task.getFloor() < curFloor && task.getDirection() == Direction.UP) {
                    newQueue.add(task);
                }
            }
        } else if (state == ElevatorState.DOWN) {
            // 向下运行则按照降序排序
            tasks.sort(Comparator.comparingInt(Task::getFloor).reversed());
            // 正向遍历
            for (Task task : tasks) {
                if (task.getFloor() <= curFloor && task.getDirection() == Direction.DOWN) {
                    newQueue.add(task);
                }
            }
            // 反向遍历选择向上的任务
            for (int i = tasks.size() - 1; i >= 0; i--) {
                Task task = tasks.get(i);
                if (task.getDirection() == Direction.UP) {
                    newQueue.add(task);
                }
            }
            // 正向遍历 选择在当前楼层之上的
            for (Task task : tasks) {
                if (task.getFloor() > curFloor && task.getDirection() == Direction.DOWN) {
                    newQueue.add(task);
                }
            }
        }else{
            newQueue.addAll(tasks);
        }
        return newQueue;
    }

    /**
     * 格式化double
     *
     * @param cost 数值
     * @return 保留两位小数
     */
    public static String doubleFormat(double cost) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(cost);
    }

}
