package com.cloud.ops.configuration.ws;

public class WebSocketMessagePayload {

    private String routingKey;
    private Object body;

    public WebSocketMessagePayload(String routingKey, Object body) {
        this.routingKey = routingKey;
        this.body = body;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }
}
