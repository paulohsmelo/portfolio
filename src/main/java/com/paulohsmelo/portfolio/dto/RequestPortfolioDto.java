package com.paulohsmelo.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestPortfolioDto {

    @NotBlank(message = "imageUrl is required")
    private String imageUrl;

    @NotBlank(message = "title is required")
    private String title;

    @NotBlank(message = "description is required")
    @Size(max = 255, message = "description must have a maximum of 255 characters")
    private String description;

    @NotNull(message = "twitterUserId is required")
    private Long twitterUserId;
}
