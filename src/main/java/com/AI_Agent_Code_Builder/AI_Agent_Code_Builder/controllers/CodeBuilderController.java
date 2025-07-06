package com.AI_Agent_Code_Builder.AI_Agent_Code_Builder.controllers;

import com.AI_Agent_Code_Builder.AI_Agent_Code_Builder.dtos.requestBody.AgentBuilderRequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/AgentBuilder")
public class CodeBuilderController {
    @PostMapping("/schedule")
    public void schedule(AgentBuilderRequestBody agentBuilderRequestBody){

    }
}
