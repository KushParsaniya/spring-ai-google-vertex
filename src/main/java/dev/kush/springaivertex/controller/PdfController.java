package dev.kush.springaivertex.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pdf")
public class PdfController {

    private final ChatClient chatClient;

    private final PgVectorStore pgVectorStore;

    @Value("classpath:prompts/doc-chat-prompt.txt")
    private Resource resource;

    public PdfController(ChatClient chatClient, PgVectorStore pgVectorStore) {
        this.chatClient = chatClient;
        this.pgVectorStore = pgVectorStore;
    }

    @GetMapping("/chat")
    public String chat(@RequestParam String prompt) {
        return chatClient
                .prompt()
                .advisors(new QuestionAnswerAdvisor(pgVectorStore))
                .system(resource)
                .user(prompt)
                .call().content();
    }
}
