package com.paulohsmelo.portfolio.controller;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.paulohsmelo.portfolio.dto.RequestPortfolioDto;
import com.paulohsmelo.portfolio.dto.ResponseDto;
import com.paulohsmelo.portfolio.dto.ResponseErrorDto;
import com.paulohsmelo.portfolio.dto.ResponsePortfolioDto;
import com.paulohsmelo.portfolio.dto.ResponseTweetDto;
import com.paulohsmelo.portfolio.exception.PortfolioNotFoundException;
import com.paulohsmelo.portfolio.exception.TwitterIntegrationException;
import com.paulohsmelo.portfolio.model.Portfolio;
import com.paulohsmelo.portfolio.service.PortfolioService;
import com.paulohsmelo.portfolio.service.TwitterIntegrationService;
import com.twitter.clientlib.model.Tweet;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@AllArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final TwitterIntegrationService twitterIntegrationService;

    @GetMapping(value = "/portfolio/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDto> getPortfolio(@PathVariable Integer id) {
        Portfolio portfolio = portfolioService.findPortfolio(id);
        List<Tweet> tweets = twitterIntegrationService.getTweets(portfolio.getTwitterUserId());
        return ResponseEntity.ok(buildSuccessResponse(portfolio, tweets));
    }

    @PutMapping(value = "/portfolio/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDto> modifyPortfolio(@PathVariable Integer id,
                                                     @RequestBody @Valid RequestPortfolioDto portfolioRequest) {
        final Portfolio portfolio = portfolioService.saveOrUpdate(id, portfolioRequest);
        return ResponseEntity.ok(buildSuccessResponse(portfolio, new ArrayList<>()));
    }

    @ExceptionHandler(PortfolioNotFoundException.class)
    public ResponseEntity<ResponseDto> handleNotFoundException(Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(TwitterIntegrationException.class)
    public ResponseEntity<ResponseDto> handleTwitterIntegrationException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(buildErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto> handleValidationExceptions(MethodArgumentNotValidException e) {
        final String errors = e.getBindingResult().getAllErrors().stream().map(
                (error) -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    return fieldName + " : " + errorMessage;
                }).collect(Collectors.joining(", "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildErrorResponse(errors));
    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<ResponseDto> handleInvalidFormatException(InvalidFormatException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildErrorResponse(e.getOriginalMessage()));
    }

    private ResponseDto buildSuccessResponse(Portfolio portfolio, List<Tweet> tweets) {
        final List<ResponseTweetDto> tweetsDto = tweets.stream()
                .map(t -> new ResponseTweetDto(t.getAuthorId(), t.getText()))
                .collect(Collectors.toList());

        ResponsePortfolioDto portfolioDto = new ResponsePortfolioDto(portfolio.getTitle(),
                portfolio.getDescription(), portfolio.getImageUrl(), portfolio.getTwitterUserId(), tweetsDto);

        return new ResponseDto(portfolioDto);
    }

    private ResponseDto buildErrorResponse(String errorMessage) {
        ResponseErrorDto errorDto = new ResponseErrorDto(errorMessage);
        return new ResponseDto(errorDto);
    }

}
