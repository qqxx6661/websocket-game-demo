package com.example.websocketgamedemo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: minshengwu
 * @Date: 2019-08-28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Answer {
    private Integer topic_answer_id;
    private Integer topic_id;
    private String answer_name;
    private Integer is_standard_answer;
}
