package com.paulohsmelo.portfolio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paulohsmelo.portfolio.dto.RequestPortfolioDto;
import com.paulohsmelo.portfolio.exception.PortfolioNotFoundException;
import com.paulohsmelo.portfolio.exception.TwitterIntegrationException;
import com.paulohsmelo.portfolio.model.Portfolio;
import com.paulohsmelo.portfolio.service.PortfolioService;
import com.paulohsmelo.portfolio.service.TwitterIntegrationService;
import com.twitter.clientlib.model.Tweet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PortfolioController.class)
public class PortfolioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PortfolioService portfolioService;

    @MockBean
    private TwitterIntegrationService twitterIntegrationService;

    @Test
    void portfolioNotFound() throws Exception {
        when(portfolioService.findPortfolio(1)).thenThrow(new PortfolioNotFoundException(1));

        mockMvc.perform(get("/portfolio/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Portfolio with id 1 not found")));
    }

    @Test
    void twitterIntegrationException() throws Exception {
        when(portfolioService.findPortfolio(1)).thenReturn(createPortfolio());
        when(twitterIntegrationService.getTweets(anyString())).thenThrow(new TwitterIntegrationException("Account is disabled"));

        mockMvc.perform(get("/portfolio/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Twitter integration is unavailable")));
    }

    @Test
    void getPortfolioSuccess() throws Exception {
        when(portfolioService.findPortfolio(1)).thenReturn(createPortfolio());
        when(twitterIntegrationService.getTweets(anyString())).thenReturn(createTweets());

        mockMvc.perform(get("/portfolio/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(getExpectedResponse("expectedGetSuccessResponse.json"))));
    }

    @Test
    void modifyPortfolioInvalid() throws Exception {
        mockMvc.perform(put("/portfolio/1")
                .content(asJsonString(new RequestPortfolioDto()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("imageUrl is required")))
                .andExpect(content().string(containsString("title is required")))
                .andExpect(content().string(containsString("description is required")))
                .andExpect(content().string(containsString("twitterUserId is required")));
    }

    @Test
    void modifyPortfolioInvalidDescription() throws Exception {
        mockMvc.perform(put("/portfolio/1")
                        .content("{\n" +
                                "    \"imageUrl\": \"The image URL\"," +
                                "    \"title\": \"A title\"," +
                                "    \"description\": \"Lorem Ipsum is simply dummy text of the printing and typesetting " +
                                " industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, " +
                                " when an unknown printer took a galley of type and scrambled it to make a type specimen " +
                                " book. It has survived not only five centuries, but also the leap into electronic typesetting, " +
                                " remaining essentially unchanged. It was popularised in the 1960s with the release of " +
                                " Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing " +
                                " software like Aldus PageMaker including versions of Lorem Ipsum.\"," +
                                "    \"twitterUserId\": \"12345\"" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("description must have a maximum of 255 characters")));
    }

    @Test
    void modifyPortfolioTwitterUserIdMustBeNumber() throws Exception {
        mockMvc.perform(put("/portfolio/1")
                        .content("{\n" +
                                "    \"imageUrl\": \"The image URL\"," +
                                "    \"title\": \"A title\"," +
                                "    \"description\": \"A description\"," +
                                "    \"twitterUserId\": \"This should be a number\"" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Cannot deserialize value of type `long` from String \\\"This should be a number\\\": not a valid `long` value")));
    }

    @Test
    void modifyPortfolioSuccess() throws Exception {
        final RequestPortfolioDto portfolioRequest = createPortfolioRequest();
        when(portfolioService.saveOrUpdate(1, portfolioRequest)).thenReturn(createPortfolio());

        mockMvc.perform(put("/portfolio/1")
                .content(asJsonString(portfolioRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(getExpectedResponse("expectedPutSuccessResponse.json"))));
    }

    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getExpectedResponse(String fileName) throws IOException {
        File expectedResponse = new ClassPathResource(fileName).getFile();
        return new String(Files.readAllBytes(expectedResponse.toPath()));
    }

    private RequestPortfolioDto createPortfolioRequest() {
       return new RequestPortfolioDto("The Image Url", "A Title", "A Description", 123L);
    }

    private Portfolio createPortfolio() {
        return new Portfolio(1, "The Image Url", "A Title", "A Description", "123");
    }

    private List<Tweet> createTweets() {
        List<Tweet> tweets = new ArrayList<>();
        tweets.add(new Tweet().authorId("An Author").text("First tweet"));
        tweets.add(new Tweet().authorId("Another Author").text("Second tweet"));
        return tweets;
    }

}
