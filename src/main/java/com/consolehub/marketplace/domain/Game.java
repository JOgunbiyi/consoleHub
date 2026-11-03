package com.consolehub.marketplace.domain;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity 
@Table(name = "games")
public class Game {

    @Id 
    private UUID id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "size_mb", nullable = false)
    private Integer sizeMb;

    @Column(nullable = false)
    private boolean published;

    @Version
    private Long version;

    protected Game(){
        //Needed by JPA
    }

    public Game(String title, String description, BigDecimal price, Integer sizeMb){
        this.id = UUID.randomUUID();
        this.title = title;
        this.description = description;
        this.price = price;
        this.currency = "EUR";
        this.sizeMb = sizeMb;
        this.published = false;
    }

    /* GETTERS */
    public String getCurrency() {
        return currency;
    }

    public String getDescription() {
        return description;
    }

    public UUID getId() {
        return id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getSizeMb() {
        return sizeMb;
    }

    public String getTitle() {
        return title;
    }

    public Long getVersion() {
        return version;
    }
}
