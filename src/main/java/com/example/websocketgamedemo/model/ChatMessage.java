package com.example.websocketgamedemo.model;


import com.example.websocketgamedemo.constants.MessageTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {
    private MessageTypeEnum type;
    private String content;
    private String sender;
    private List<String> receiver;
}

