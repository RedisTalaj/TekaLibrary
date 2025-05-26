package com.sda.tekalibrary.services;

import com.sda.tekalibrary.entities.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DeepSeekService {
    private static final String DEEPSEEK_API_URL = "https://api.deepseek.com/v1/chat/completions";
    private static final int MAX_LOCAL_RESULTS = 3;
    private static final int MAX_DESCRIPTION_PREVIEW = 100;

    private final BookService bookService;
    private final RestTemplate restTemplate;

    @Value("${deepseek.api.key:}")
    private String deepSeekApiKey;

    @Value("${recommendation.use-deepseek:true}")
    private boolean useDeepSeek;

    @Autowired
    public DeepSeekService(BookService bookService,
                                     RestTemplate restTemplate) {
        this.bookService = bookService;
        this.restTemplate = restTemplate;
    }

    public String getRecommendations(String userDescription) {
        if (useDeepSeek && deepSeekApiKey != null && !deepSeekApiKey.isEmpty()) {
            try {
                return getDeepSeekRecommendations(userDescription);
            } catch (HttpClientErrorException e) {
                if (e.getStatusCode() == HttpStatus.PAYMENT_REQUIRED) {
                    return getLocalRecommendations(userDescription) +
                            "\n\n(Note: Premium recommendation service currently unavailable)";
                }
                return "Error: " + e.getStatusCode() + " - " + e.getStatusText();
            } catch (Exception e) {
                return getLocalRecommendations(userDescription) +
                        "\n\n(Note: Using local recommendations due to service error)";
            }
        }
        return getLocalRecommendations(userDescription);
    }

    private String getDeepSeekRecommendations(String userDescription) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + deepSeekApiKey);

        List<Book> allBooks = bookService.getAllBooks();
        if (allBooks.isEmpty()) {
            return "No books available in the library.";
        }

        String booksPrompt = allBooks.stream()
                .limit(20)
                .map(book -> String.format(
                        "Title: %s, Author: %s, Description: %s",
                        book.getTitle(),
                        book.getAuthor(),
                        book.getDescription().length() > MAX_DESCRIPTION_PREVIEW ?
                                book.getDescription().substring(0, MAX_DESCRIPTION_PREVIEW) + "..." :
                                book.getDescription()
                ))
                .collect(Collectors.joining("\n\n"));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "deepseek-chat");

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of(
                "role", "system",
                "content", "You are a helpful librarian. Recommend 1-3 books from " +
                        "this list that best match the user's request. Return only " +
                        "the book titles and authors in this format:\n\n" +
                        "1. [Title] by [Author] - [Brief reason]\n" +
                        "2. [Title] by [Author] - [Brief reason]\n\n" +
                        "Available books:\n" + booksPrompt
        ));
        messages.add(Map.of(
                "role", "user",
                "content", "Recommend books matching: " + userDescription
        ));

        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 300);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                DEEPSEEK_API_URL,
                HttpMethod.POST,
                entity,
                Map.class
        );

        if (response.getStatusCode().is2xxSuccessful() &&
                response.getBody() != null) {
            Map<String, Object> responseBody = response.getBody();
            List<Map<String, Object>> choices = (List<Map<String, Object>>)
                    responseBody.get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> message = (Map<String, Object>)
                        choices.get(0).get("message");
                return (String) message.get("content");
            }
        }
        return getLocalRecommendations(userDescription);
    }

    private String getLocalRecommendations(String userDescription) {
        if (userDescription == null || userDescription.trim().isEmpty()) {
            return "Please describe what kind of book you're looking for.";
        }

        List<Book> allBooks = bookService.getAllBooks();
        if (allBooks.isEmpty()) {
            return "No books available in the library.";
        }

        String lowerDesc = userDescription.toLowerCase();
        String[] keywords = lowerDesc.split("\\s+");

        List<Book> matches = allBooks.stream()
                .filter(book -> Arrays.stream(keywords)
                        .anyMatch(keyword ->
                                book.getTitle().toLowerCase().contains(keyword) ||
                                        book.getDescription().toLowerCase().contains(keyword) ||
                                        book.getAuthor().toLowerCase().contains(keyword) ||
                                        book.getCategory().toLowerCase().contains(keyword))
                )
                .sorted(Comparator.comparing(Book::getTitle))
                .limit(MAX_LOCAL_RESULTS)
                .collect(Collectors.toList());

        if (matches.isEmpty()) {
            return "No books matched your description. Try different keywords or check our full collection.";
        }
        
        StringBuilder result = new StringBuilder();
        result.append("We recommend these books:\n\n");
        for (int i = 0; i < matches.size(); i++) {
            Book book = matches.get(i);
            result.append(i + 1).append(". ")
                    .append(book.getTitle()).append(" by ")
                    .append(book.getAuthor()).append("\n")
                    .append("Category: ").append(book.getCategory()).append("\n")
                    .append(book.getDescription().length() > MAX_DESCRIPTION_PREVIEW ?
                            book.getDescription().substring(0, MAX_DESCRIPTION_PREVIEW) + "..." :
                            book.getDescription())
                    .append("\n\n");
        }
        return result.toString();
    }
}