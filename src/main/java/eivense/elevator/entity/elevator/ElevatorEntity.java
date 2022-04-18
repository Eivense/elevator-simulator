package eivense.elevator.entity.elevator;


import eivense.elevator.entity.Direction;
import eivense.elevator.entity.passenger.Passenger;
import eivense.elevator.util.ElevatorUtil;
import lombok.Data;

import java.util.*;

/**
 * 电梯实体
 */
@Data
public class ElevatorEntity implements Elevator {

    /**
     * 电梯编号
     */
    private int id;

    /**
     * 当前楼层
     */
    private int curFloor;

    /**
     * 当前电梯的状态
     */
    private ElevatorState state;

    /**
     * 电梯中的乘客
     */
    private Set<Passenger> passengers;

    /**
     * 电梯最大人数
     */
    private int maxPassengers;

    /**
     * 当前电梯人数
     */
    private int nums;

    /**
     * 任务队列
     */
    private Queue<Task> taskQueue;

    /**
     * 构造函数
     *
     * @param id    电梯id
     * @param floor 初始楼层
     */
    public ElevatorEntity(int id, int floor, int maxPassengers) {
        this.id = id;
        // 初始在最低楼层
        this.curFloor = floor;
        // 初始状态
        this.state = ElevatorState.IDLE;
        // 乘客上限
        this.passengers = new HashSet<>(maxPassengers);
        this.maxPassengers = maxPassengers;
        // 初始化任务队列
        this.taskQueue = new LinkedList<>();
    }


    /**
     * 更新状态
     */
    public void updateState() {
        if (taskQueue.isEmpty()) {
            state = ElevatorState.IDLE;
        } else {
            Task task = taskQueue.peek();
            int targetFloor = task.getFloor();
            if (targetFloor != curFloor) {
                updateStateByFloor(targetFloor);
            } else {
                updateStateByDirection(task.getDirection());
            }
        }
    }

    @Override
    public Set<Passenger> getUsers() {
        return passengers;
    }

    @Override
    public boolean isFullLoaded() {
        return nums == maxPassengers;
    }

    @Override
    public void moveUp() {
        curFloor++;
    }

    @Override
    public void moveDown() {
        curFloor--;
    }

    @Override
    public void addTask(Task task) {
        // lombok重写了task的equals
        if (!taskQueue.contains(task)) {
            taskQueue.add(task);
            // 重新排序
            updateTaskQueue(ElevatorUtil.sortTask(taskQueue, state, curFloor));
        }
    }


    @Override
    public boolean isNeedWait() {
        if (!taskQueue.isEmpty()) {
            // 检查任务方向
            Task task = taskQueue.peek();
            // 如果该任务在该楼层则停留
            if (curFloor == task.getFloor()) {
                // 当前层停留
                taskQueue.poll();
                if (taskQueue.isEmpty()) {
                    updateStateByDirection(task.getDirection());
                } else {
                    // 更新状态
                    updateState();
                    // 如果之后的任务在同一层则出队
                    if (!taskQueue.isEmpty() && taskQueue.peek().getFloor() == curFloor) {
                        taskQueue.poll();
                    }
                }
                return true;
            }
        } else {
            state = ElevatorState.IDLE;
        }
        return false;
    }


    @Override
    public boolean getIn(Passenger passenger) {
        if (!isFullLoaded()) {
            if (passengers.add(passenger)) {
                nums++;
                return true;
            }
        }
        return false;
    }

    @Override
    public void getOut(Passenger passenger) {
        if (passengers.remove(passenger)) {
            nums--;
        }
    }

    @Override
    public Queue<Task> getTaskQueue() {
        return taskQueue;
    }

    @Override
    public void updateTaskQueue(Queue<Task> taskQueue) {
        this.taskQueue = taskQueue;
        updateState();
    }

    /**
     * 根据任务方向更新状态
     *
     * @param direction 任务方向
     */
    private void updateStateByDirection(Direction direction) {
        if (direction == Direction.UP) {
            state = ElevatorState.UP;
        } else {
            state = ElevatorState.DOWN;
        }
    }

    /**
     * 根据楼层方向更新状态
     *
     * @param targetFloor 目标楼层
     */
    private void updateStateByFloor(int targetFloor) {
        if (targetFloor > curFloor) {
            state = ElevatorState.UP;
        } else {
            state = ElevatorState.DOWN;
        }
    }
}


