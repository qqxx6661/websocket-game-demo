package com.example.websocketgamedemo.repository;

import com.example.websocketgamedemo.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
}
