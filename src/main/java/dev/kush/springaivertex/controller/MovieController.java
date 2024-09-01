package dev.kush.springaivertex.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final ChatClient chatClient;

    @Value("classpath:prompts/movie-rec-prompt.txt")
    private Resource movieRecPrompt;

    public MovieController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/suggest/{preference}")
    public List<RecommendedMovie> suggest(@PathVariable String preference) {
        return chatClient
                .prompt()
                .system(movieRecPrompt)
                .user(String.format(
                        """
                            suggest me some movie i like watching movie of %s genre.
                        """, preference
                )).call()
                .entity(new ParameterizedTypeReference<>() {
                });
    }

    @GetMapping("/contact/{id}")
    public List<RecommendedMovie> suggestFromLikedMovies(@PathVariable int id) {
        return chatClient
                .prompt()
                .system(movieRecPrompt)
                .user("""
                        here is my id %d, suggest me some movies
                        """.formatted(id))
                .call()
                .entity(new ParameterizedTypeReference<>() {});
    }

}

record RecommendedMoviesList(List<RecommendedMovie> movies) {}

record RecommendedMovie(String title, String overview, float imdb, int releaseYear, List<Actor> actors) {}

record Actor(String name, int age, List<Award> awards) {}

record Award(String awardName, String movieName) {}