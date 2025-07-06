package com.AI_Agent_Code_Builder.AI_Agent_Code_Builder.dtos.requestBody;

import java.util.Objects;

public class AgentBuilderRequestBody {
    private String prompt;

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgentBuilderRequestBody that = (AgentBuilderRequestBody) o;
        return Objects.equals(prompt, that.prompt);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(prompt);
    }

    @Override
    public String toString() {
        return "AgentBuilderRequestBody{" +
                "prompt='" + prompt + '\'' +
                '}';
    }
}
