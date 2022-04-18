package eivense.elevator.entity.floor;

import eivense.elevator.entity.Direction;
import eivense.elevator.entity.passenger.Passenger;
import eivense.elevator.util.ElevatorUtil;

import java.util.*;

/**
 * 楼层实体
 */
public class FloorEntity implements Floor{

    /**
     * 楼层号
     */
    private final int id;

    /**
     * 在该层等待的所有乘客
     */
    private final Queue<Passenger> waitPassengers;

    /**
     * 按照乘客方向分类
     */
    private final Map<Direction, Queue<Passenger>> waitPassengersMap;


    public FloorEntity(int id) {
        this.id = id;
        this.waitPassengers = new LinkedList<>();
        // 按照乘客方向 分为两类
        Map<Direction, Queue<Passenger>> map = new HashMap<>(16);
        map.put(Direction.UP, new LinkedList<>());
        map.put(Direction.DOWN, new LinkedList<>());
        this.waitPassengersMap = map;
    }


    @Override
    public void addPassenger(Passenger passenger) {
        Direction direction= ElevatorUtil.getPassengerDirection(passenger);
        waitPassengers.add(passenger);
        waitPassengersMap.get(direction).add(passenger);
    }


    @Override
    public void removePassenger(Passenger passenger) {
        Direction direction = ElevatorUtil.getPassengerDirection(passenger);
        waitPassengers.remove(passenger);
        waitPassengersMap.get(direction).remove(passenger);
    }

    @Override
    public void removePassengers(List<Passenger> passengers) {
        for (Passenger passenger : passengers) {
            removePassenger(passenger);
        }
    }


    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public Queue<Passenger> getWaitPassengers() {
        return waitPassengers;
    }

    @Override
    public Queue<Passenger> getWaitPassengersWithIntention(Direction direction) {
        return waitPassengersMap.get(direction);
    }

    @Override
    public boolean hasPassengers() {
        return !waitPassengers.isEmpty();
    }


}
