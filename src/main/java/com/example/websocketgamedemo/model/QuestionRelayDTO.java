package com.example.websocketgamedemo.model;

import lombok.Data;

import java.util.List;

/**
 * @Author: minshengwu
 * @Date: 2019-08-28
 */
@Data
public class QuestionRelayDTO {
       private Integer topic_id;
       private Integer active_topic_id;
       private String type;
       private String topic_name;
       private String active_id;
       private String active_title;
       private String active_topic_phase;
       private String active_start_time;
       private String active_end_time;
       private List<Answer> topic_answer;
}
