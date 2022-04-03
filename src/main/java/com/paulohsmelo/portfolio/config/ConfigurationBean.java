package com.paulohsmelo.portfolio.config;

import com.twitter.clientlib.TwitterCredentialsBearer;
import com.twitter.clientlib.api.TwitterApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigurationBean {

    @Bean
    public TwitterApi twitterApi(TwitterConfig twitterConfig) {
        TwitterApi twitterApi = new TwitterApi();
        TwitterCredentialsBearer credentials = new TwitterCredentialsBearer(twitterConfig.getBearerToken());
        twitterApi.setTwitterCredentials(credentials);
        return twitterApi;
    }
}
