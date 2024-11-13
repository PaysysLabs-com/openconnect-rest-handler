package com.paysyslabs.bootstrap.rest.pojo;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "products")
public class Products {
	
	private String accountTitle;

    private String avaliableBalance;

    private String investorTypeCode;

    private String corelatedAccountId;

    private String accountNumber;

    private String allowCorrelation;

    private String description;

    private String name;

    private String categoryCode;

    private String operatingCode;

    private String active;

    private String code;

    private String registrationCode;

    public String getAccountTitle ()
    {
        return accountTitle;
    }

    public void setAccountTitle (String accountTitle)
    {
        this.accountTitle = accountTitle;
    }

    public String getAvaliableBalance ()
    {
        return avaliableBalance;
    }

    public void setAvaliableBalance (String avaliableBalance)
    {
        this.avaliableBalance = avaliableBalance;
    }

    public String getInvestorTypeCode ()
    {
        return investorTypeCode;
    }

    public void setInvestorTypeCode (String investorTypeCode)
    {
        this.investorTypeCode = investorTypeCode;
    }

    public String getCorelatedAccountId ()
    {
        return corelatedAccountId;
    }

    public void setCorelatedAccountId (String corelatedAccountId)
    {
        this.corelatedAccountId = corelatedAccountId;
    }

    public String getAccountNumber ()
    {
        return accountNumber;
    }

    public void setAccountNumber (String accountNumber)
    {
        this.accountNumber = accountNumber;
    }

    public String getAllowCorrelation ()
    {
        return allowCorrelation;
    }

    public void setAllowCorrelation (String allowCorrelation)
    {
        this.allowCorrelation = allowCorrelation;
    }

    public String getDescription ()
    {
        return description;
    }

    public void setDescription (String description)
    {
        this.description = description;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getCategoryCode ()
    {
        return categoryCode;
    }

    public void setCategoryCode (String categoryCode)
    {
        this.categoryCode = categoryCode;
    }

    public String getOperatingCode ()
    {
        return operatingCode;
    }

    public void setOperatingCode (String operatingCode)
    {
        this.operatingCode = operatingCode;
    }

    public String getActive ()
    {
        return active;
    }

    public void setActive (String active)
    {
        this.active = active;
    }

    public String getCode ()
    {
        return code;
    }

    public void setCode (String code)
    {
        this.code = code;
    }

    public String getRegistrationCode ()
    {
        return registrationCode;
    }

    public void setRegistrationCode (String registrationCode)
    {
        this.registrationCode = registrationCode;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [accountTitle = "+accountTitle+", avaliableBalance = "+avaliableBalance+", investorTypeCode = "+investorTypeCode+", corelatedAccountId = "+corelatedAccountId+", accountNumber = "+accountNumber+", allowCorrelation = "+allowCorrelation+", description = "+description+", name = "+name+", categoryCode = "+categoryCode+", operatingCode = "+operatingCode+", active = "+active+", code = "+code+", registrationCode = "+registrationCode+"]";
    }
    
}

