package com.example.websocketgamedemo.model;

import lombok.Data;

@Data
public class MessageReply {
    private Integer code;
    private String status;
    private ChatMessage chatMessage;
}
