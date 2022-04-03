package com.paulohsmelo.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponsePortfolioDto {

    private String title;

    private String description;

    private String imageUrl;

    private String twitterUserId;

    private List<ResponseTweetDto> tweets;
}
