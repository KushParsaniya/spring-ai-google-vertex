package dev.kush.springaivertex.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/memory")
public class ChatMemoryController {

    private final ChatClient chatClient;

    private final ChatMemory jdbcChatMemory;

    public ChatMemoryController(ChatClient chatClient, ChatMemory jdbcChatMemory) {
        this.chatClient = chatClient;
        this.jdbcChatMemory = jdbcChatMemory;
    }

    @GetMapping("/{prompt}")
    public String prompt(@PathVariable String prompt, HttpServletRequest request) {
        return chatClient
                .prompt()
                .advisors(new PromptChatMemoryAdvisor(jdbcChatMemory, request.getHeader("id"), 20,
                        """
                                
                                Use the conversation memory from the MEMORY section to provide accurate answers.
                                
                                ---------------------
                                MEMORY:
                                {memory}
                                ---------------------
                                
                                """))
                .user(prompt)
                .call().content();
    }
}
