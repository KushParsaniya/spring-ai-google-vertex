package dev.kush.springaivertex.config;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class ChatMemoryConfig {

    @Bean
    ChatMemory jdbcChatMemory(
            ActiveSessionsRepository activeSessionsRepository,
            ChatHistoryRepository chatHistoryRepository,
            JdbcTemplate jdbcTemplate) {
        return new ChatMemory() {
            @Override
            public void add(String conversationId, List<Message> messages) {
                activeSessionsRepository.save(new ActiveSessions(conversationId, 1, 1));
                List<ChatHistory> chats = messages.stream()
                        .map(message -> new ChatHistory(null, message.getContent(), conversationId))
                        .toList();

                chatHistoryRepository.saveAll(chats);
            }

            @Override
            public List<Message> get(String conversationId, int lastN) {
                var chatHistories = chatHistoryRepository.findAllBySessionId(conversationId, lastN);

                return chatHistories.stream()
                        .map(chatHistory -> new UserMessage(chatHistory.content()))
                        .collect(Collectors.toList());
            }

            @Override
            public void clear(String conversationId) {
                String updateQuery = """
                        update active_sessions set is_active = ? where session_id = ?
                        """;
                jdbcTemplate.update(updateQuery, 0, conversationId);
            }
        };
    }
}

@Table("active_sessions")
record ActiveSessions(@Id @Column("session_id") String sessionId, int contactId, int isActive) {
}

@Table("chats_history")
record ChatHistory(@Id Integer id, String content, String sessionId) {
}

@Repository
interface ActiveSessionsRepository extends ListCrudRepository<ActiveSessions, String> {
}

@Repository
interface ChatHistoryRepository extends CrudRepository<ChatHistory, Integer> {

    @Query("select * from chats_history where session_id=:sessionId order by id desc limit :limit")
    List<ChatHistory> findAllBySessionId(String sessionId, int limit);
}