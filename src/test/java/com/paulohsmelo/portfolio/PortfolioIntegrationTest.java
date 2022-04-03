package com.paulohsmelo.portfolio;

import com.paulohsmelo.portfolio.PortfolioApplication;
import com.paulohsmelo.portfolio.dto.RequestPortfolioDto;
import com.paulohsmelo.portfolio.dto.ResponseDto;
import com.paulohsmelo.portfolio.model.Portfolio;
import com.paulohsmelo.portfolio.repository.PortfolioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = PortfolioApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/cleanup-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class PortfolioIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Test
    void getInvalidPortfolioId() {
        assertEquals(0, getDatabasePortfolios().size());

        final ResponseEntity<ResponseDto> response = testRestTemplate.getForEntity("/portfolio/1", ResponseDto.class);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

        final ResponseDto responseDto = response.getBody();
        assertNull(responseDto.getPortfolio());
        assertNotNull(responseDto.getError());
        assertEquals("Portfolio with id 1 not found", responseDto.getError().getMessage());
    }

    @Test
    void getValidPortfolioWithInvalidTwitterUser() {
        final Portfolio portfolio = createPortfolio();
        portfolio.setTwitterUserId("invalid");
        portfolioRepository.save(portfolio);
        assertEquals(1, getDatabasePortfolios().size());

        final ResponseEntity<ResponseDto> response = testRestTemplate.getForEntity("/portfolio/1", ResponseDto.class);
        assertNotNull(response);
        assertEquals(500, response.getStatusCodeValue());

        final ResponseDto responseDto = response.getBody();
        assertNull(responseDto.getPortfolio());
        assertNotNull(responseDto.getError());
        assertThat(responseDto.getError().getMessage(), containsString("Twitter integration is unavailable"));
    }

    @Test
    void getValidPortfolio() {
        portfolioRepository.save(createPortfolio());
        assertEquals(1, getDatabasePortfolios().size());

        final ResponseEntity<ResponseDto> response = testRestTemplate.getForEntity("/portfolio/1", ResponseDto.class);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        final ResponseDto responseDto = response.getBody();
        assertNotNull(responseDto.getPortfolio());
        assertEquals("https://imageUrl/image.png", responseDto.getPortfolio().getImageUrl());
        assertEquals("Paulo", responseDto.getPortfolio().getTitle());
        assertEquals("Java Developer", responseDto.getPortfolio().getDescription());
        assertNotNull(responseDto.getPortfolio().getTweets());
        assertNull(responseDto.getError());
        assertEquals(1, getDatabasePortfolios().size());
    }

    @Test
    void modifyNonexistentPortfolioShouldCreateNewOne() {
        final ResponseEntity<ResponseDto> response = testRestTemplate.exchange("/portfolio/1",
                HttpMethod.PUT, new HttpEntity<>(createRequestDto()), ResponseDto.class);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody().getPortfolio());

        final List<Portfolio> portfolios = getDatabasePortfolios();
        assertEquals(1, portfolios.size());
        assertEquals("https://imageUrl/image.png", portfolios.get(0).getImageUrl());
        assertEquals("Paulo", portfolios.get(0).getTitle());
        assertEquals("Java Developer", portfolios.get(0).getDescription());
        assertEquals("1509631869335265287", portfolios.get(0).getTwitterUserId());
    }

    @Test
    void modifyExistentPortfolio() {
        portfolioRepository.save(createPortfolio());
        assertEquals(1, getDatabasePortfolios().size());

        final RequestPortfolioDto requestDto = createRequestDto();
        requestDto.setDescription("Changing the description");
        requestDto.setTwitterUserId(123L);

        final ResponseEntity<ResponseDto> response = testRestTemplate.exchange("/portfolio/1",
                HttpMethod.PUT, new HttpEntity<>(requestDto), ResponseDto.class);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody().getPortfolio());

        final List<Portfolio> portfolios = getDatabasePortfolios();
        assertEquals(1, portfolios.size());
        assertEquals("https://imageUrl/image.png", portfolios.get(0).getImageUrl());
        assertEquals("Paulo", portfolios.get(0).getTitle());
        assertEquals("Changing the description", portfolios.get(0).getDescription());
        assertEquals("123", portfolios.get(0).getTwitterUserId());
    }

    @Test
    void invalidInputShouldNotModifyPortfolio() {
        portfolioRepository.save(createPortfolio());
        assertEquals(1, getDatabasePortfolios().size());

        final RequestPortfolioDto requestDto = createRequestDto();
        requestDto.setDescription("Changing the description");
        requestDto.setTitle("");

        final ResponseEntity<ResponseDto> response = testRestTemplate.exchange("/portfolio/1",
                HttpMethod.PUT, new HttpEntity<>(requestDto), ResponseDto.class);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());

        final ResponseDto responseDto = response.getBody();
        assertNull(responseDto.getPortfolio());
        assertNotNull(responseDto.getError());
        assertThat(responseDto.getError().getMessage(), containsString("title is required"));
    }

    private List<Portfolio> getDatabasePortfolios() {
         return StreamSupport
                .stream(portfolioRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    private Portfolio createPortfolio() {
        return new Portfolio(1, "https://imageUrl/image.png", "Paulo",
                "Java Developer", "1509631869335265287");
    }

    private RequestPortfolioDto createRequestDto() {
        return new RequestPortfolioDto("https://imageUrl/image.png", "Paulo",
                "Java Developer", 1509631869335265287L);
    }
}
