package com.paulohsmelo.portfolio.repository;

import com.paulohsmelo.portfolio.model.Portfolio;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortfolioRepository extends CrudRepository<Portfolio, Integer> {
}
