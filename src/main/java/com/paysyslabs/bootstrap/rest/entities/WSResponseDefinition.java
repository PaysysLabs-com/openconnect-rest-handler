package com.paysyslabs.bootstrap.rest.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ws_response_definition")
public class WSResponseDefinition {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "config_id", nullable = false)
    private WSConfig config;

    @Column(name = "match_code")
    private String matchCode;

    @Column(name = "our_code")
    private String ourCode;

    @Column(name = "our_description")
    private String ourDescription;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WSConfig getConfig() {
        return config;
    }

    public void setConfig(WSConfig config) {
        this.config = config;
    }

    public String getMatchCode() {
        return matchCode;
    }

    public void setMatchCode(String matchCode) {
        this.matchCode = matchCode;
    }

    public String getOurCode() {
        return ourCode;
    }

    public void setOurCode(String ourCode) {
        this.ourCode = ourCode;
    }

    public String getOurDescription() {
        return ourDescription;
    }

    public void setOurDescription(String ourDescription) {
        this.ourDescription = ourDescription;
    }
}
