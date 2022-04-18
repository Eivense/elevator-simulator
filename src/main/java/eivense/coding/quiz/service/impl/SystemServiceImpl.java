package eivense.coding.quiz.service.impl;

import eivense.coding.quiz.config.ElevatorProperties;
import eivense.coding.quiz.config.SystemProperties;
import eivense.coding.quiz.entity.passenger.Passenger;
import eivense.coding.quiz.entity.elevator.ElevatorEntity;
import eivense.coding.quiz.entity.elevator.ElevatorInfo;
import eivense.coding.quiz.service.ElevatorService;
import eivense.coding.quiz.service.SystemService;
import eivense.coding.quiz.util.ElevatorUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * 控制接口实现类
 */
@Slf4j
@Service
public class SystemServiceImpl implements SystemService {

    /**
     * 系统配置
     */
    private final SystemProperties systemProperties;

    /**
     * 时间
     */
    private int worldClock;

    /**
     * 电梯服务
     */
    private ElevatorService elevatorService;

    /**
     * 用户set
     */
    private Set<String> passengerSet;

    /**
     * 总共等待时间
     */
    private long totalWaitTime;

    /**
     * 完成的乘客数量
     */
    private long finishedPassengerNum;


    @Autowired
    public SystemServiceImpl(SystemProperties systemProperties) {
        this.systemProperties = systemProperties;
        init(systemProperties);
    }

    /**
     * 初始化
     */
    private void init(SystemProperties properties) {
        // 时钟初始化为0
        this.worldClock = 0;
        this.passengerSet = new HashSet<>();
        // 电梯数量
        int elevatorNum = properties.getElevatorNum();
        // 楼层范围
        int minFloorNum = properties.getMinFloor();
        int maxFloorNum = properties.getMaxFloor();
        // 初始化电梯服务
        this.elevatorService = new ElevatorServiceImpl(elevatorNum, minFloorNum, maxFloorNum);
        // 电梯配置
        ElevatorProperties elevatorProperties = properties.getProperties();
        // 初始化电梯
        for (int i = 1; i <= elevatorNum; i++) {
            // 电梯实体
            ElevatorEntity elevator = new ElevatorEntity(i, minFloorNum, properties.getProperties().getMaxPassengers());
            this.elevatorService.registerElevator(elevator, minFloorNum, maxFloorNum, elevatorProperties);
        }

        // 用于统计等待时间
        this.totalWaitTime = 0;
        this.finishedPassengerNum = 0;
        log.info("Init with floors from {} to {},{} elevators", minFloorNum, maxFloorNum, elevatorNum);
        log.info("Elevator upCost:{},downCost:{}, ratio:{}", elevatorProperties.getUpCost(), elevatorProperties.getDownCost(), elevatorProperties.getRatio());
    }

    /**
     * 重新初始化
     */
    private void reInit() {
        init(systemProperties);
    }

    @Override
    public List<ElevatorInfo> reset() {
        log.info("Reset");
        // 重新初始化
        reInit();
        return elevatorService.getElevatorInfo();
    }

    @Override
    public List<ElevatorInfo> workload(List<Passenger> passengers) {
        // 时间自增
        worldClock++;
        log.info("----Workload,current world clock:{}----", worldClock);
        // 校验请求中的乘客
        List<Passenger> validPassengers = checkPassengers(passengers);
        // 处理新增的事件
        elevatorService.processPassengers(validPassengers, worldClock);
        log.info("---------------Execute-----------------");
        // 执行电梯
        List<Passenger> getOutPassengers = elevatorService.processAllElevator();
        log.info("--------------Statistics---------------");
        // 已经离开的乘客从set移除
        for (Passenger passenger : getOutPassengers) {
            // 计算等待时间 总共时间-本来就需要乘坐的层数-进入电梯需要等待一次
            long waitTime = worldClock - passenger.getClock() - Math.abs(passenger.getStartFloor() - passenger.getEndFloor())-1;
            log.debug("Passenger:{}, wait:{} clock", passenger.getUser(), waitTime);
            totalWaitTime += waitTime;
            finishedPassengerNum++;
            String name = passenger.getUser();
            passengerSet.remove(name);
        }
        // 是否还有乘客
        if (passengerSet.isEmpty()) {
            log.info("No passengers");
        } else {
            log.info("Remaining {} passengers", passengerSet.size());
        }
        if (finishedPassengerNum != 0) {
            log.info("Average wait time {} clock", ElevatorUtil.doubleFormat((double) totalWaitTime / finishedPassengerNum));
        }
        log.info("---------------------------------------\n");
        // 返回状态
        return elevatorService.getElevatorInfo();
    }


    /**
     * 校验请求中的乘客 过滤不合法的乘客
     *
     * @param passengers 请求的乘客
     * @return 符合条件的乘客
     */
    private List<Passenger> checkPassengers(List<Passenger> passengers) {
        List<Passenger> list = new ArrayList<>();
        for (Passenger passenger : passengers) {
            String name = passenger.getUser();
            if (!passengerSet.contains(name)) {
                int startFloor = passenger.getStartFloor();
                int endFloor = passenger.getEndFloor();
                int minFloor = systemProperties.getMinFloor();
                int maxFloor = systemProperties.getMaxFloor();
                // 楼层范围符合
                if (startFloor >= minFloor && endFloor <= maxFloor && startFloor != endFloor) {
                    list.add(passenger);
                    passengerSet.add(name);
                } else {
                    if (startFloor == endFloor) {
                        log.warn("Passenger:{} ,startFloor:{} and endFloor:{} can not be equal", name, startFloor, endFloor);
                    } else {
                        log.warn("Passenger:{} ,startFloor:{} ,endFloor:{} ,only support {}-{}", name, startFloor, endFloor, minFloor, maxFloor);
                    }
                }
            } else {
                // 该用户存在
                log.warn("Passenger:{} is exist", name);
            }
        }
        return list;
    }


}
