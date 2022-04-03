package com.paulohsmelo.portfolio.service;

import com.paulohsmelo.portfolio.exception.TwitterIntegrationException;
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.api.TwitterApi;
import com.twitter.clientlib.model.Tweet;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TwitterIntegrationService {

    private final TwitterApi twitterApi;

    public List<Tweet> getTweets(String twitterUserId) {
        try {
            return Optional.ofNullable(twitterApi.tweets().usersIdTweets(
                            twitterUserId, null, null, null, null, null,
                            null, null, null, null, null, null,
                            null, null)
                    .getData()).orElse(new ArrayList<>());
        } catch (ApiException e) {
            throw new TwitterIntegrationException(e.getResponseBody());
        }
    }
}
