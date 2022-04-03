package com.paulohsmelo.portfolio.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Portfolio {

    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "idportfolio")
    private Integer id;

    @Column(name = "image_url")
    private String imageUrl;

    private String title;

    private String description;

    @Column(name = "twitter_user_id")
    private String twitterUserId;

}
