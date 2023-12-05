package com.project.restaurantbooking.messagetemplates;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;

@Getter
@NoArgsConstructor
public class AgentCommand implements Serializable {
    private String targetAgent;
    private String content;
    private String correlationId;
    private String task;
    @Getter
    private transient CompletableFuture<Object> futureResult;

    public AgentCommand(String targetAgent, String content, String correlationId, String task) {
        this.targetAgent = targetAgent;
        this.content = content;
        this.correlationId = correlationId;
        this.task = task;

        this.futureResult = new CompletableFuture<Object>();
    }

    public void completeFutureResult(Object result) {
        this.futureResult.complete(result);
    }

    public void setTargetAgent(String targetAgent) {
        this.targetAgent = targetAgent;
    }

    public void setContent(String content) {
        this.content = content;
    }

}

