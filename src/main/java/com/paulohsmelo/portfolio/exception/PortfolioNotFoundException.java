package com.paulohsmelo.portfolio.exception;

public class PortfolioNotFoundException extends RuntimeException {

    public PortfolioNotFoundException(Integer portfolioId) {
        super("Portfolio with id " + portfolioId + " not found");
    }
}
