package eivense.coding.quiz.entity.floor;

import eivense.coding.quiz.entity.Direction;
import eivense.coding.quiz.entity.passenger.Passenger;

import java.util.List;
import java.util.Queue;

/**
 * 楼层接口
 */
public interface Floor {

    /**
     * 获取楼层号
     *
     * @return 楼层号
     */
    int getId();

    /**
     * 获取在该楼层等待的所有乘客
     */
    Queue<Passenger> getWaitPassengers();

    /**
     * 根据乘客意图获取乘客
     *
     * @param direction 意图
     * @return 对应意图的乘客
     */
    Queue<Passenger> getWaitPassengersWithIntention(Direction direction);

    /**
     * 是否还有乘客需要上电梯
     *
     * @return 是否还有乘客需要上电梯
     */
    boolean hasPassengers();


    /**
     * 乘客在该楼层等待
     *
     * @param passengerRequest 乘客
     */
    void addPassenger(Passenger passengerRequest);


    /**
     * 删除该楼层等待的乘客
     *
     * @param passengerRequest 乘客
     */
    void removePassenger(Passenger passengerRequest);

    /**
     * 批量删除
     *
     * @param passengerRequests 批量乘客
     */
    void removePassengers(List<Passenger> passengerRequests);
}
