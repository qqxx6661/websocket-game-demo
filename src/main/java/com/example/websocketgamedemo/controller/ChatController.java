package com.example.websocketgamedemo.controller;

import com.example.websocketgamedemo.constants.MessageTypeEnum;
import com.example.websocketgamedemo.constants.StatusEnum;
import com.example.websocketgamedemo.model.Answer;
import com.example.websocketgamedemo.model.ChatMessage;
import com.example.websocketgamedemo.model.MessageReply;
import com.example.websocketgamedemo.model.QuestionRelayDTO;
import com.example.websocketgamedemo.service.QuestionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.*;

@Log4j
@Controller
public class ChatController {

    private Map<String, StatusEnum> userToStatus = new HashMap<>();
    private Map<String, String> userToPlay = new HashMap<>();
    private static final int limit = 10;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private QuestionService questionService;

    @MessageMapping("/game.add_user")
    @SendTo("/topic/game")
    public MessageReply addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) throws JsonProcessingException {
        MessageReply message = new MessageReply();
        String sender = chatMessage.getSender();
        ChatMessage result = new ChatMessage();
        result.setType(MessageTypeEnum.ADD_USER);
        result.setReceiver(Collections.singletonList(sender));
        if (userToStatus.containsKey(sender)) {
            message.setCode(201);
            message.setStatus("该用户名已存在");
            message.setChatMessage(result);
            log.warn("addUser[" + sender + "]: " + message.toString());
        } else {
            result.setContent(mapper.writeValueAsString(userToStatus.keySet().stream().filter(k -> userToStatus.get(k).equals(StatusEnum.IDLE)).toArray()));
            message.setCode(200);
            message.setStatus("成功");
            message.setChatMessage(result);
            userToStatus.put(sender, StatusEnum.IDLE);
            headerAccessor.getSessionAttributes().put("username",sender);
            log.warn("addUser[" + sender + "]: " + message.toString());
        }
        return message;
    }

    @MessageMapping("/game.choose_user")
    @SendTo("/topic/game")
    public MessageReply chooseUser(@Payload ChatMessage chatMessage) throws JsonProcessingException {
        MessageReply message = new MessageReply();
        String receiver = chatMessage.getContent();
        String sender = chatMessage.getSender();
        ChatMessage result = new ChatMessage();
        result.setType(MessageTypeEnum.CHOOSE_USER);
        if (userToStatus.containsKey(receiver) && userToStatus.get(receiver).equals(StatusEnum.IDLE)) {
            List<QuestionRelayDTO> list=new ArrayList<>();
            questionService.getQuestions(limit).forEach(item->{
                QuestionRelayDTO relayDTO=new QuestionRelayDTO();
                relayDTO.setTopic_id(item.getId());
                relayDTO.setTopic_name(item.getQuestion());
                List<Answer> answers=new ArrayList<>();
                answers.add(new Answer(1,item.getId(),item.getOptionA(),item.getResult()==1?1:0));
                answers.add(new Answer(2,item.getId(),item.getOptionB(),item.getResult()==2?1:0));
                answers.add(new Answer(3,item.getId(),item.getOptionC(),item.getResult()==3?1:0));
                answers.add(new Answer(4,item.getId(),item.getOptionD(),item.getResult()==4?1:0));
                relayDTO.setTopic_answer(answers);
                list.add(relayDTO);
            });
            result.setContent(mapper.writeValueAsString(list));
            result.setReceiver(Arrays.asList(sender, receiver));
            message.setCode(200);
            message.setStatus("匹配成功");
            message.setChatMessage(result);
            userToStatus.put(receiver, StatusEnum.IN_GAME);
            userToStatus.put(sender, StatusEnum.IN_GAME);
            userToPlay.put(receiver,sender);
            userToPlay.put(sender,receiver);
            log.warn("chooseUser[" + sender + "," + receiver + "]: " + message.toString());
        } else {
            result.setContent(mapper.writeValueAsString(userToStatus.keySet().stream().filter(k -> userToStatus.get(k).equals(StatusEnum.IDLE)).toArray()));
            result.setReceiver(Collections.singletonList(sender));
            message.setCode(202);
            message.setStatus("该用户不存在或已在游戏中");
            message.setChatMessage(result);
            log.warn("chooseUser[" + sender + "]: " + message.toString());
        }
        return message;
    }

    @MessageMapping("/game.do_exam")
    @SendTo("/topic/game")
    public MessageReply doExam(@Payload ChatMessage chatMessage) throws JsonProcessingException {
        MessageReply message = new MessageReply();
        String sender = chatMessage.getSender();
        String receiver = userToPlay.get(sender);
        ChatMessage result = new ChatMessage();
        result.setType(MessageTypeEnum.DO_EXAM);
        log.warn("userToStatus:" + mapper.writeValueAsString(userToStatus));
        if (userToStatus.containsKey(receiver) && userToStatus.get(receiver).equals(StatusEnum.IN_GAME)) {
            result.setContent(chatMessage.getContent());
            result.setSender(sender);
            result.setReceiver(Collections.singletonList(receiver));
            message.setCode(200);
            message.setStatus("成功");
            message.setChatMessage(result);
            log.warn("doExam[" + receiver + "]: " + message.toString());
        }else{
            result.setReceiver(Collections.singletonList(sender));
            message.setCode(203);
            message.setStatus("该用户不存在或已退出游戏");
            message.setChatMessage(result);
            log.warn("doExam[" + sender + "]: " + message.toString());
        }
        return message;
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if(username != null) {
            log.info("User Disconnected : " + username);
            userToStatus.remove(username);
            userToPlay.remove(username);
        }
    }
}