package com.sda.tekalibrary.controllers;

import com.sda.tekalibrary.services.DeepSeekService;
//import com.sda.tekalibrary.services.OpenAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import retrofit2.HttpException;

@Controller
@RequestMapping("/chat")
public class ChatController {

    private final DeepSeekService deepSeekService;

    @Autowired
    public ChatController(DeepSeekService deepSeekService) {
        this.deepSeekService = deepSeekService;
    }

    @GetMapping("/book-recommendations")
    public String showChatPage(Model model) {
        model.addAttribute("userInput", "");
        model.addAttribute("aiResponse", "");
        return "MainPage/book-chat";
    }

    @PostMapping("/book-recommendations")
    public String getRecommendations(@RequestParam String userInput, Model model) {
        try {
            String response = deepSeekService.getRecommendations(userInput);
            model.addAttribute("aiResponse", response);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.PAYMENT_REQUIRED) {
                model.addAttribute("warning",
                        "Premium recommendation service is currently unavailable. " +
                                "Showing local results instead.");
            } else {
                model.addAttribute("error",
                        "Sorry, we couldn't process your request. Please try again.");
            }
        }
        model.addAttribute("userInput", userInput);
        return "MainPage/book-chat";
    }
}