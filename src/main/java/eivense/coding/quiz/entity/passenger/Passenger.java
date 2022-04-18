package eivense.coding.quiz.entity.passenger;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 乘客实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Passenger {

    /**
     * 开始等待的时间
     */
    @JsonIgnore
    private int clock;

    /**
     * 乘客名称
     */
    private String user;

    /**
     * 起始楼层
     */
    private int startFloor;

    /**
     * 目标楼层
     */
    private int endFloor;


}
