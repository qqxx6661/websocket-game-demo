package com.example.websocketgamedemo.model;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class Question {
    @Id
    private Integer id;
    @Column
    private String question;
    @Column(name="option_a")
    private String optionA;
    @Column(name="option_b")
    private String optionB;
    @Column(name="option_c")
    private String optionC;
    @Column(name="option_d")
    private String optionD;
    @Column
    private Integer result;
    @Column
    private String analysis;
}
