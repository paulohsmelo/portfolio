package com.paulohsmelo.portfolio.service;

import com.paulohsmelo.portfolio.dto.RequestPortfolioDto;
import com.paulohsmelo.portfolio.exception.PortfolioNotFoundException;
import com.paulohsmelo.portfolio.model.Portfolio;
import com.paulohsmelo.portfolio.repository.PortfolioRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;

    public Portfolio findPortfolio(Integer id) {
        return portfolioRepository.findById(id).orElseThrow(() -> new PortfolioNotFoundException(id));
    }

    public Portfolio saveOrUpdate(Integer id, RequestPortfolioDto portfolioRequest) {
        final Portfolio portfolio = new Portfolio(id,
                portfolioRequest.getImageUrl(),
                portfolioRequest.getTitle(),
                portfolioRequest.getDescription(),
                portfolioRequest.getTwitterUserId().toString());

        return portfolioRepository.save(portfolio);
    }

}
