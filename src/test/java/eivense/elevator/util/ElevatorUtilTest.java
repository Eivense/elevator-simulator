package eivense.elevator.util;

import eivense.elevator.entity.Direction;
import eivense.elevator.entity.elevator.*;
import eivense.elevator.entity.passenger.Passenger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


@JsonTest
class ElevatorUtilTest {

    @Autowired
    private JacksonTester<ElevatorInfo> jacksonTester;

    @Test
    void getInfo() throws IOException {
        Set<Passenger> set = new HashSet<>();
        set.add(new Passenger(1, "user4", 4, 5));
        set.add(new Passenger(1, "user5", 7, 3));
        ElevatorInfo info = new ElevatorInfo(1, 2, set);

        JsonContent<ElevatorInfo> json = jacksonTester.write(info);
        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathNumberValue("$.floor").isEqualTo(2);
        assertThat(json).extractingJsonPathStringValue("$.users[0].user").isEqualTo("user4");
    }


    @Test
    void getPassengerDirection() {

        Passenger passengerUp = new Passenger(1, "user4", 4, 5);
        Direction directionUp = ElevatorUtil.getPassengerDirection(passengerUp);
        assertThat(directionUp).isEqualTo(Direction.UP);

        Passenger passengerDown = new Passenger(1, "user5", 7, 3);
        Direction directionDown = ElevatorUtil.getPassengerDirection(passengerDown);
        assertThat(directionDown).isEqualTo(Direction.DOWN);
    }

    @Test
    void doubleFormat() {
        double cost1 = 100.1556754d;
        assertThat(ElevatorUtil.doubleFormat(cost1)).isEqualTo("100.16");

        double cost2 = 0.145454d;
        assertThat(ElevatorUtil.doubleFormat(cost2)).isEqualTo("0.15");
    }

    @Test
    void sortTask() {

        Queue<Task> queue = new LinkedList<>();
        queue.add(new Task(5, Direction.UP));
        queue.add(new Task(7, Direction.UP));
        queue.add(new Task(5, Direction.DOWN));
        queue.add(new Task(4, Direction.UP));
        queue.add(new Task(2, Direction.UP));
        queue.add(new Task(11, Direction.UP));
        queue.add(new Task(4, Direction.DOWN));
        queue.add(new Task(6, Direction.UP));

        Elevator elevator = new ElevatorEntity(1, 5, 20);

        Queue<Task> expertQueue = new LinkedList<>();
        expertQueue.add(new Task(5, Direction.UP));
        expertQueue.add(new Task(6, Direction.UP));
        expertQueue.add(new Task(7, Direction.UP));
        expertQueue.add(new Task(11, Direction.UP));
        expertQueue.add(new Task(5, Direction.DOWN));
        expertQueue.add(new Task(4, Direction.DOWN));
        expertQueue.add(new Task(2, Direction.UP));
        expertQueue.add(new Task(4, Direction.UP));

        // 排序之后 5 7 6 11 5 4 2 4
        Queue<Task> newQueue = ElevatorUtil.sortTask(queue, ElevatorState.UP, elevator.getCurFloor());
        assertThat(newQueue.toArray()).isEqualTo(expertQueue.toArray());
    }
}