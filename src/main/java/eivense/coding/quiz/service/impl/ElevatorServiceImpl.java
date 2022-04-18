package eivense.coding.quiz.service.impl;

import eivense.coding.quiz.config.ElevatorProperties;
import eivense.coding.quiz.entity.Prediction;
import eivense.coding.quiz.entity.elevator.*;
import eivense.coding.quiz.entity.floor.Floor;
import eivense.coding.quiz.entity.floor.FloorEntity;
import eivense.coding.quiz.entity.Direction;
import eivense.coding.quiz.entity.passenger.Passenger;
import eivense.coding.quiz.service.ElevatorService;
import eivense.coding.quiz.util.ElevatorUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 电梯控制实现
 */
@Slf4j
public class ElevatorServiceImpl implements ElevatorService {

    /**
     * 所有电梯映射
     */
    private final Map<Integer, ElevatorOperator> elevatorMap;

    /**
     * 楼层号 和当前在该楼层等待的乘客
     *
     * 同一楼层按照乘客的意图进行分类
     */
    public final Map<Integer, Floor> floorMap;


    public ElevatorServiceImpl(int elevatorNum, int minFloor, int maxFloor) {
        this.elevatorMap = new HashMap<>(elevatorNum);
        // 初始化楼层
        this.floorMap = new HashMap<>(maxFloor - minFloor + 1);
        for (int i = minFloor; i <= maxFloor; i++) {
            // 添加楼层
            this.floorMap.put(i, new FloorEntity(i));
        }
    }


    @Override
    public void registerElevator(Elevator elevator, int minFloor, int maxFloor, ElevatorProperties properties) {
        ElevatorOperatorImpl operator = new ElevatorOperatorImpl(elevator, minFloor, maxFloor, properties);
        this.elevatorMap.put(elevator.getId(), operator);
    }


    @Override
    public void processPassengers(List<Passenger> passengers, int worldClock) {
        if (!passengers.isEmpty()) {
            log.info("{} new requests",passengers.size());
            for (Passenger passenger : passengers) {
                // 设置时间
                passenger.setClock(worldClock);
                int startFloor = passenger.getStartFloor();
                // 添加到等待队列
                floorMap.get(startFloor).addPassenger(passenger);
            }
            // 处理请求 将楼层添加到各部电梯的执行任务中
            makeDecision(passengers);
        } else {
            log.info("No new request");
        }
    }

    @Override
    public List<Passenger> processAllElevator() {
        // 所有电梯中离开的乘客
        List<Passenger> getOutPassengersForAll = new ArrayList<>();
        // 处理每一部电梯
        for (ElevatorOperator operator : elevatorMap.values()) {
            Elevator elevator = operator.getElevator();
            // 当前楼层
            int curFloor = elevator.getCurFloor();
            Floor floor = floorMap.get(curFloor);
            // 是否停留
            if (elevator.isNeedWait()) {
                log.info("Elevator:{}, stop on {} floor", elevator.getId(), curFloor);
                // 处理需要离开电梯的乘客
                List<Passenger> getOutPassengers = operator.handleGetOut(floor);
                if (!getOutPassengers.isEmpty()) {
                    log.debug("{} passenger get out of elevator:{}, on the {} floor, names:{}", getOutPassengers.size(), elevator.getId(), elevator.getCurFloor(), ElevatorUtil.getPassengerName(getOutPassengers));
                }
                getOutPassengersForAll.addAll(getOutPassengers);

                // 处理需要进入电梯的乘客
                List<Passenger> getInPassengers = operator.handleGetIn(floor);
                if (!getInPassengers.isEmpty()) {
                    log.debug("{} passenger get in the elevator:{}, on the {} floor, names:{}", getInPassengers.size(), elevator.getId(), elevator.getCurFloor(), ElevatorUtil.getPassengerName(getInPassengers));
                }
            } else {
                // 不停留
                operator.moveElevator();
            }
        }
        // 返回在当前时间所有离开的乘客
        return getOutPassengersForAll;
    }


    @Override
    public List<ElevatorInfo> getElevatorInfo() {
        List<ElevatorInfo> list = new ArrayList<>(this.elevatorMap.size());
        for (ElevatorOperator operator : elevatorMap.values()) {
            list.add(ElevatorUtil.getInfo(operator.getElevator()));
        }
        return list;
    }

    /**
     * 为每位乘客分配电梯
     *
     * @param passengers 发出请求的乘客
     */
    private void makeDecision(List<Passenger> passengers) {
        for (Passenger passenger : passengers) {
            Direction direction = ElevatorUtil.getPassengerDirection(passenger);
            int startFloor = passenger.getStartFloor();
            Task task = new Task(startFloor, direction);
            // 小顶堆
            PriorityQueue<Prediction> plan = new PriorityQueue<>(elevatorMap.size(),
                    Comparator.comparingDouble(Prediction::getCost));
            // 遍历所有电梯
            for (ElevatorOperator operator : elevatorMap.values()) {
                // 优先选择空闲或者同方向的电梯
                plan.add(operator.calculateCost(task));
            }
            // 最优的选择
            Prediction minPrediction = plan.poll();
            Elevator selected = minPrediction.getElevator();
            selected.updateTaskQueue(minPrediction.getTaskQueue());
            log.debug("Passenger:{}, from {} to {} | best elevator:{}, cost:{}, current on {} floor",
                    passenger.getUser(), passenger.getStartFloor(), passenger.getEndFloor(), selected.getId(),
                    ElevatorUtil.doubleFormat(minPrediction.getCost()), selected.getCurFloor());
        }
    }


}
