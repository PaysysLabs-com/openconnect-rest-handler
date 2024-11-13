package com.paysyslabs.bootstrap.rest.pojo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductsInquiryResponse {
	
	private String response_desc;

    private String response_code;

    private String tran_ref;

    private ProductsInquiry products_inquiry;

    public ProductsInquiryResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

    public ProductsInquiryResponse(String response_desc, String response_code, String tran_ref,
			ProductsInquiry products_inquiry) {
		super();
		this.response_desc = response_desc;
		this.response_code = response_code;
		this.tran_ref = tran_ref;
		this.products_inquiry = products_inquiry;
	}
    
	public String getResponse_desc ()
    {
        return response_desc;
    }

    public void setResponse_desc (String response_desc)
    {
        this.response_desc = response_desc;
    }

    public String getResponse_code ()
    {
        return response_code;
    }

    public void setResponse_code (String response_code)
    {
        this.response_code = response_code;
    }

    public String getTran_ref ()
    {
        return tran_ref;
    }

    public void setTran_ref (String tran_ref)
    {
        this.tran_ref = tran_ref;
    }

    public ProductsInquiry getProducts_inquiry ()
    {
        return products_inquiry;
    }

    public void setProducts_inquiry (ProductsInquiry products_inquiry)
    {
        this.products_inquiry = products_inquiry;
    }

    @Override
    public String toString()
    {
        return "ProductsInquiry [response_desc = "+response_desc+", response_code = "+response_code+", tran_ref = "+tran_ref+", products_inquiry = "+products_inquiry+"]";
    }
	
}
