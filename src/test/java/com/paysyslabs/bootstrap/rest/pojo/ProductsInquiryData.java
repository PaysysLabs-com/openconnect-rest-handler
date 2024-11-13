package com.paysyslabs.bootstrap.rest.pojo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementWrapper;

@XmlAccessorType(XmlAccessType.FIELD)
public class ProductsInquiryData {
	
	private String documentType;

    private String customerType;

    private String status;

    private String remarks;

    private String branchCode;

    private String investorTypeCode;

    private String createdBy;

    private String custIdentityValue;

    private String customerCode;

    private String action;

    private String lastModifiedDate;

    private String displayName;

    private String createdDate;

    @XmlElementWrapper(name = "products_wrapper")
    private Products[] products;

    private String custIdentityKey;

    public String getDocumentType ()
    {
        return documentType;
    }

    public void setDocumentType (String documentType)
    {
        this.documentType = documentType;
    }

    public String getCustomerType ()
    {
        return customerType;
    }

    public void setCustomerType (String customerType)
    {
        this.customerType = customerType;
    }

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    public String getRemarks ()
    {
        return remarks;
    }

    public void setRemarks (String remarks)
    {
        this.remarks = remarks;
    }

    public String getBranchCode ()
    {
        return branchCode;
    }

    public void setBranchCode (String branchCode)
    {
        this.branchCode = branchCode;
    }

    public String getInvestorTypeCode ()
    {
        return investorTypeCode;
    }

    public void setInvestorTypeCode (String investorTypeCode)
    {
        this.investorTypeCode = investorTypeCode;
    }

    public String getCreatedBy ()
    {
        return createdBy;
    }

    public void setCreatedBy (String createdBy)
    {
        this.createdBy = createdBy;
    }

    public String getCustIdentityValue ()
    {
        return custIdentityValue;
    }

    public void setCustIdentityValue (String custIdentityValue)
    {
        this.custIdentityValue = custIdentityValue;
    }

    public String getCustomerCode ()
    {
        return customerCode;
    }

    public void setCustomerCode (String customerCode)
    {
        this.customerCode = customerCode;
    }

    public String getAction ()
    {
        return action;
    }

    public void setAction (String action)
    {
        this.action = action;
    }

    public String getLastModifiedDate ()
    {
        return lastModifiedDate;
    }

    public void setLastModifiedDate (String lastModifiedDate)
    {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getDisplayName ()
    {
        return displayName;
    }

    public void setDisplayName (String displayName)
    {
        this.displayName = displayName;
    }

    public String getCreatedDate ()
    {
        return createdDate;
    }

    public void setCreatedDate (String createdDate)
    {
        this.createdDate = createdDate;
    }

    public Products[] getProducts ()
    {
        return products;
    }

    public void setProducts (Products[] products)
    {
        this.products = products;
    }

    public String getCustIdentityKey ()
    {
        return custIdentityKey;
    }

    public void setCustIdentityKey (String custIdentityKey)
    {
        this.custIdentityKey = custIdentityKey;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [documentType = "+documentType+", customerType = "+customerType+", status = "+status+", remarks = "+remarks+", branchCode = "+branchCode+", investorTypeCode = "+investorTypeCode+", createdBy = "+createdBy+", custIdentityValue = "+custIdentityValue+", customerCode = "+customerCode+", action = "+action+", lastModifiedDate = "+lastModifiedDate+", displayName = "+displayName+", createdDate = "+createdDate+", products = "+products+", custIdentityKey = "+custIdentityKey+"]";
    }
}
