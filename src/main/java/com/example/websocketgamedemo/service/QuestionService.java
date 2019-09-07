package com.example.websocketgamedemo.service;


import com.example.websocketgamedemo.model.Question;
import com.example.websocketgamedemo.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    public List<Question> getQuestions(int limit) {
        Set<Integer> ids = new HashSet<>();
        int min = 1;
        int max = (int) questionRepository.count();
        Random random = new Random();
        while (ids.size()<limit) {
            int id = random.nextInt(max) % (max - min + 1) + min;
            ids.add(id);
        }
        return ids.stream().map(questionRepository::findOne).collect(Collectors.toList());
    }
}
