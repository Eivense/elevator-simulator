package eivense.coding.quiz.controller;


import eivense.coding.quiz.entity.elevator.ElevatorInfo;
import eivense.coding.quiz.entity.passenger.Passenger;
import eivense.coding.quiz.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * api接口
 */
@RestController
public class ApiController {

    @Autowired
    private SystemService service;

    @PostMapping("/reset")
    public List<ElevatorInfo> reset() {
        return service.reset();
    }

    @PostMapping("/workload")
    public List<ElevatorInfo> workload(@RequestBody List<Passenger> passengers) {
        return service.workload(passengers);
    }

}
