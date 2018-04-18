package com.cloud.ops.common.ws;

import com.cloud.ops.common.utils.MD5Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/1/25.
 */
@Service
public class CustomWebSocketHandler extends AbstractWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(CustomWebSocketHandler.class);
    private static final Map<String, List<WebSocketSession>> users;
    @Autowired
    private ObjectMapper objectMapper;

    static {
        users = new HashMap<>();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String routingKey = parse(session.getUri().getQuery()).get("routing-key");
        assert StringUtils.isNotBlank(routingKey);
        if (users.get(routingKey) == null) {
            List<WebSocketSession> sessions = new ArrayList<>();
            sessions.add(session);
            users.put(routingKey, sessions);
        } else {
            users.get(routingKey).add(session);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String routingKey = parse(session.getUri().getQuery()).get("routing-key");
        assert StringUtils.isNotBlank(routingKey);
        List<WebSocketSession> sessions = users.get(routingKey);
        if (sessions != null && !sessions.isEmpty()) {
            sessions.remove(session);
        }
    }

    public void sendMsg(String routingKey, Object body) {
        List<WebSocketSession> sessions = users.get(routingKey);
        if (sessions == null) {
            return;
        }
        for (WebSocketSession user : sessions) {
            try {
                if (user != null && user.isOpen()) {
                    user.sendMessage(new TextMessage(objectMapper.writeValueAsString(body)));
                }
            } catch (IOException e) {
                logger.error("websocket send message: ", e);
            }
        }
    }

    public static Map<String, String> parse(String queryString) {

        Map<String, String> map = new HashMap<String, String>();

        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length > 1) {
                map.put(keyValue[0], keyValue[1]);
            } else {
                map.put(keyValue[0], null);
            }
        }

        return map;
    }
}
