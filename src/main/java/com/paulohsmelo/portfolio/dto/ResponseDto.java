package com.paulohsmelo.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseDto {

    private ResponsePortfolioDto portfolio;

    private ResponseErrorDto error;

    public ResponseDto(ResponsePortfolioDto portfolio) {
        this.portfolio = portfolio;
    }

    public ResponseDto(ResponseErrorDto error) {
        this.error = error;
    }
}
