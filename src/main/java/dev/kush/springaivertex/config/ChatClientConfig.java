package dev.kush.springaivertex.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Configuration
public class ChatClientConfig {

    @Bean
    ChatClient chatClient(
            ChatClient.Builder builder,
            @Value("classpath:prompts/movie-rec-prompt.txt") Resource movieRecPrompt,
            PgVectorStore pgVectorStore, ContactRepository contactRepository) {

//        contactRepository.findAll()
//                .forEach(contact -> {
//                    Document document = new Document("id: %d, name: %s, contact: %s liked-movies: %s"
//                            .formatted(contact.id(), contact.name(), contact.phoneNumber(), contact.interests()));
//                    pgVectorStore.add(List.of(document));
//                });

        return builder
//                .defaultSystem(movieRecPrompt)
//                .defaultAdvisors(new QuestionAnswerAdvisor(pgVectorStore))
                .build();
    }

}

@Table
record Contact(@Id int id, String name, String phoneNumber, String interests) {
}

@Repository
interface ContactRepository extends CrudRepository<Contact, Integer> {
}
