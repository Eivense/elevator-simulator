package eivense.coding.quiz.entity.elevator;

import eivense.coding.quiz.entity.passenger.Passenger;

import java.util.Queue;
import java.util.Set;

/**
 * 电梯接口
 *
 */
public interface Elevator {

    /**
     * 获取电梯id
     *
     * @return id
     */
    int getId();

    /**
     * 当前所在楼层
     * @return 楼层号
     */
    int getCurFloor();

    /**
     * 获取电梯状态
     *
     * @return 状态
     */
    ElevatorState getState();


    /**
     * 获取当前电梯里的乘客
     *
     * @return 当前在电梯里的乘客
     */
    Set<Passenger> getUsers();

    /**
     * 乘客进入电梯
     *
     * @param passenger 乘客
     * @return 是否能进入电梯
     */
    boolean getIn(Passenger passenger);

    /**
     * 乘客离开电梯
     *
     * @param passenger 乘客
     */
    void getOut(Passenger passenger);

    /**
     * 电梯是否满载
     *
     * @return 是否满载
     */
    boolean isFullLoaded();

    /**
     * 向上移动
     */
    void moveUp();

    /**
     * 向下移动
     */
    void moveDown();

    /**
     * 添加任务
     *
     * @param task 任务
     */
    void addTask(Task task);

    /**
     * 是否需要停留
     *
     * @return true表示需要停留
     */
    boolean isNeedWait();


    /**
     * 获取任务队列
     *
     * @return 队列
     */
    Queue<Task> getTaskQueue();


    /**
     * 更新任务队列
     * @param taskQueue 新的任务队列
     */
    void updateTaskQueue(Queue<Task> taskQueue);

}
