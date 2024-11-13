package com.paysyslabs.bootstrap.rest.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.paysyslabs.bootstrap.rest.enums.WSTokenExpiryType;

@Entity
@Table(name = "ws_token_config")
public class WSTokenConfiguration {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "current_token")
    private String currentToken;

    @Column(name = "current_expiry_epoch")
    private Long currentExpiryEpoch;

    @Column(name = "token_field")
    private String tokenField;

    @Column(name = "expiry_field")
    private String expiryField;

    @Column(name = "expiry_type")
    @Enumerated(EnumType.STRING)
    private WSTokenExpiryType expiryType;
    
    public boolean isValid() {
        if (currentToken == null || currentExpiryEpoch == null)
            return false;
        
        return System.currentTimeMillis() < currentExpiryEpoch.longValue();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCurrentToken() {
        return currentToken;
    }

    public void setCurrentToken(String currentToken) {
        this.currentToken = currentToken;
    }

    public Long getCurrentExpiryEpoch() {
        return currentExpiryEpoch;
    }

    public void setCurrentExpiryEpoch(Long currentExpiryEpoch) {
        this.currentExpiryEpoch = currentExpiryEpoch;
    }

    public String getTokenField() {
        return tokenField;
    }

    public void setTokenField(String tokenField) {
        this.tokenField = tokenField;
    }

    public String getExpiryField() {
        return expiryField;
    }

    public void setExpiryField(String expiryField) {
        this.expiryField = expiryField;
    }

    public WSTokenExpiryType getExpiryType() {
        return expiryType;
    }

    public void setExpiryType(WSTokenExpiryType expiryType) {
        this.expiryType = expiryType;
    }

}
