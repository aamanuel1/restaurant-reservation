package com.project.restaurantbooking.agent;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class AgentCommand implements Serializable {
    private String targetAgent;
    private String content;

    public AgentCommand(String targetAgent, String content) {
        this.targetAgent = targetAgent;
        this.content = content;
    }

    public void setTargetAgent(String targetAgent) {
        this.targetAgent = targetAgent;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

