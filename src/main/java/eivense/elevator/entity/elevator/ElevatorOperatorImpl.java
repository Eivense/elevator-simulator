package eivense.elevator.entity.elevator;

import eivense.elevator.config.ElevatorProperties;
import eivense.elevator.entity.Prediction;
import eivense.elevator.entity.floor.Floor;
import eivense.elevator.entity.Direction;
import eivense.elevator.entity.passenger.Passenger;
import eivense.elevator.util.ElevatorUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 电梯操作类
 */
@Data
@Slf4j
public class ElevatorOperatorImpl implements ElevatorOperator {

    /**
     * 电梯
     */
    public Elevator elevator;

    /**
     * 能够到的最高楼层
     */
    public int minFloor;

    /**
     * 能够到的最高楼层
     */
    public int maxFloor;

    /**
     * 一些属性
     */
    public ElevatorProperties properties;


    public ElevatorOperatorImpl(Elevator elevator, int minFloor, int maxFloor, ElevatorProperties properties) {
        this.elevator = elevator;
        this.minFloor = minFloor;
        this.maxFloor = maxFloor;
        this.properties = properties;
    }


    @Override
    public List<Passenger> handleGetOut(Floor floor) {
        List<Passenger> getOutPassengers = new ArrayList<>();
        Set<Passenger> passengers = elevator.getUsers();
        int curFloor = elevator.getCurFloor();
        for (Passenger passenger : passengers) {
            // 目标楼层等于当前楼层
            if (passenger.getEndFloor() == curFloor) {
                // 加入到待删除队列
                getOutPassengers.add(passenger);
            }
        }
        // 离开电梯
        for (Passenger passenger : getOutPassengers) {
            elevator.getOut(passenger);
        }
        return getOutPassengers;
    }


    /**
     * 移动电梯
     */
    @Override
    public void moveElevator() {
        ElevatorState state = elevator.getState();
        int id = elevator.getId();
        // 当前楼层
        int curFloor = elevator.getCurFloor();
        // 更新楼层信息
        // TODO 判断范围
        if (state == ElevatorState.UP) {
            log.debug("Elevator:{}, move up from {} to {}", id, curFloor, curFloor + 1);
            elevator.moveUp();
        } else if (state == ElevatorState.DOWN) {
            log.debug("Elevator:{}, move down from {} to {}", id, curFloor, curFloor - 1);
            elevator.moveDown();
        } else {
            if (curFloor != minFloor) {
                // 回到初始层
                elevator.addTask(new Task(minFloor, Direction.DOWN));
                elevator.moveDown();
                log.debug("Elevator:{}, idle on {} floor, move back to {} floor", id, curFloor, minFloor);
            } else {
                // 空闲状态不移动
                log.debug("Elevator:{}, idle on {} floor", id, curFloor);
            }
        }
    }


    @Override
    public List<Passenger> handleGetIn(Floor floor) {
        List<Passenger> getInPassengers = new ArrayList<>();
        // 是否还有等待的乘客
        if (floor.hasPassengers()) {
            ElevatorState state = elevator.getState();
            // 获取与电梯方向一致的乘客
            Queue<Passenger> passengers;
            if (state == ElevatorState.UP) {
                passengers = floor.getWaitPassengersWithIntention(Direction.UP);
            } else if (state == ElevatorState.DOWN) {
                passengers = floor.getWaitPassengersWithIntention(Direction.DOWN);
            } else {
                // 否则获取第一个等待乘客的方向
                Passenger firstPassenger = floor.getWaitPassengers().peek();
                passengers = floor.getWaitPassengersWithIntention(ElevatorUtil.getPassengerDirection(firstPassenger));
            }
            // 遍历乘客
            for (Passenger passenger : passengers) {
                Direction direction = ElevatorUtil.getPassengerDirection(passenger);
                // 若该乘客能够成功进入电梯 则移除等待队列
                if (elevator.getIn(passenger)) {
                    // 进入电梯之后输入想去的楼层
                    int endFloor = passenger.getEndFloor();
                    // 楼层范围满足要求
                    if (endFloor <= maxFloor && endFloor >= minFloor) {
                        // 设置任务
                        elevator.addTask(new Task(endFloor, direction));
                        getInPassengers.add(passenger);
                    } else {
                        log.warn("Passenger:{} want go to {} floor,elevator only support {}-{}", passenger.getUser(), endFloor, minFloor, maxFloor);
                    }
                }
            }
            // 从等待队列删除
            floor.removePassengers(getInPassengers);
        }
        return getInPassengers;
    }


    @Override
    public Prediction calculateCost(Task startTask) {
        // 当前该电梯的任务队列
        Queue<Task> queue = elevator.getTaskQueue();
        int curFloor = elevator.getCurFloor();
        double upCost = properties.getUpCost();
        double downCost = properties.getDownCost();
        double ratio = properties.getRatio();
        ElevatorState state = elevator.getState();
        if (state != ElevatorState.IDLE) {
            // 计算加入任务之后的代价
            Queue<Task> newTaskQueue = new LinkedList<>(queue);
            newTaskQueue.add(startTask);
            newTaskQueue = ElevatorUtil.sortTask(newTaskQueue, state, curFloor);
            int[] newDistance = calculateDistance(newTaskQueue, curFloor,startTask);
            double newCost = calculateCost(newDistance, upCost, downCost, ratio);
            return new Prediction(elevator, newCost, newTaskQueue);
        } else {
            int targetFloor = startTask.getFloor();
            int distance = curFloor - targetFloor;
            int timeCost = Math.abs(distance);
            // 大于0向上 小于0向下
            double powerCost = distance > 0 ? distance * upCost : -distance * downCost;
            double cost = timeCost * ratio + (1 - ratio) * powerCost;
            Queue<Task> newTaskQueue = new LinkedList<>();
            newTaskQueue.add(startTask);
            return new Prediction(elevator, cost, newTaskQueue);
        }
    }


    /**
     * 计算移动的层数和停留的次数
     * <p>
     * distances[0]为向上移动的距离
     * distances[1]为向下移动的距离
     * distances[2]为需要停留的次数
     *
     * @param taskQueue 当前任务队列
     * @param curFloor  当前所在楼层
     * @return 数组
     */
    private static int[] calculateDistance(Queue<Task> taskQueue, int curFloor, Task targetTask) {
        int[] distances = new int[3];
        int lastFloor = curFloor;
        for (Task curTask : taskQueue) {
            int floor = curTask.getFloor();
            int distance = floor - lastFloor;
            // 说明向上
            if (distance > 0) {
                distances[0] += distance;
            } else if (distance < 0) {
                distances[1] += -distance;
            }
            lastFloor = floor;
            distances[2]++;
            // 计算到该任务的距离
            if (curTask.equals(targetTask)) {
                break;
            }
        }
        return distances;
    }

    /**
     * 计算代价
     *
     * @param distance 距离数组
     * @param upCost   向上运行时的能耗
     * @param downCost 向下运行时的能耗
     * @param ratio    占比
     * @return 结果
     */
    private static double calculateCost(int[] distance, double upCost, double downCost, double ratio) {
        // 移动的层数+停留时间
        int timeCost = distance[0] + distance[1] + distance[2];
        double powerCost = distance[0] * upCost + distance[1] * downCost;
        return timeCost * ratio + (1 - ratio) * powerCost;
    }

}
