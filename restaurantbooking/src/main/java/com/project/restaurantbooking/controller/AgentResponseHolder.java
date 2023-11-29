package com.project.restaurantbooking.controller;

import lombok.Getter;

import java.util.concurrent.CompletableFuture;

@Getter
public class AgentResponseHolder {
    private final CompletableFuture<String> responseFuture = new CompletableFuture<>();

    public void complete(String response) {
        responseFuture.complete(response);
    }

}

