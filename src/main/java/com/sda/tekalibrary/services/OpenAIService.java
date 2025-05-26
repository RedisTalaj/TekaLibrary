//package com.sda.tekalibrary.services;
//
//import com.sda.tekalibrary.entities.Book;
//import com.theokanning.openai.completion.chat.ChatCompletionRequest;
//import com.theokanning.openai.completion.chat.ChatMessage;
//import com.theokanning.openai.completion.chat.ChatMessageRole;
//import com.theokanning.openai.service.OpenAiService;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.time.Duration;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicLong;
//import java.util.stream.Collectors;
//
//@Service
//public class OpenAIService {
//
//    private final OpenAiService service;
//    private final BookService bookService;
//    private final AtomicLong lastRequestTime = new AtomicLong(0);
//    private static final long MIN_REQUEST_INTERVAL = 2000;
//
//    public OpenAIService(@Value("${openai.api.key}") String apiKey, BookService bookService) {
//        this.service = new OpenAiService(apiKey, Duration.ofSeconds(60));
//        this.bookService = bookService;
//    }
//
//    public String getBookRecommendations(String userDescription) {
//        try {
//            // 1. Get books from database
//            List<Book> allBooks = bookService.getAllBooks().stream()
//                    .filter(book -> book.getDescription() != null && !book.getDescription().isEmpty())
//                    .limit(20) // Don't send too many books
//                    .collect(Collectors.toList());
//
//            if (allBooks.isEmpty()) {
//                return "No books available in the library.";
//            }
//
//            // 2. Prepare books information
//            StringBuilder booksInfo = new StringBuilder();
//            for (Book book : allBooks) {
//                booksInfo.append("Title: ").append(book.getTitle())
//                        .append(", Author: ").append(book.getAuthor())
//                        .append(", Description: ").append(book.getDescription())
//                        .append("\n\n");
//            }
//
//            // 3. Create clear instructions for AI
//            String systemMessage = "You are a helpful librarian. Recommend 1-3 books from " +
//                    "the following list that best match the user's request. " +
//                    "Return only the book titles and authors in this format:\n\n" +
//                    "1. [Title] by [Author] - [Brief reason]\n" +
//                    "2. [Title] by [Author] - [Brief reason]\n\n" +
//                    "Available books:\n" + booksInfo.toString();
//
//            // 4. Create the chat messages
//            List<ChatMessage> messages = new ArrayList<>();
//            messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), systemMessage));
//            messages.add(new ChatMessage(ChatMessageRole.USER.value(),
//                    "Recommend books matching this description: " + userDescription));
//
//            // 5. Make the API request
//            ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
//                    .model("gpt-3.5-turbo")
//                    .messages(messages)
//                    .temperature(0.7)
//                    .maxTokens(300)
//                    .build();
//
//            ChatMessage response = service.createChatCompletion(completionRequest)
//                    .getChoices()
//                    .get(0)
//                    .getMessage();
//
//            return response.getContent();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Sorry, I couldn't process your request. Please try again.";
//        }
//    }
//}