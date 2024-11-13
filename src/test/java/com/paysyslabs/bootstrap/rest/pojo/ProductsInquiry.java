package com.paysyslabs.bootstrap.rest.pojo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class ProductsInquiry {

    @XmlElement(name = "data")
	private ProductsInquiryData productsInquiryData;

	public ProductsInquiryData getProductsInquiryData() {
		return productsInquiryData;
	}

	public void setProducts_inquiry(ProductsInquiryData productsInquiryData) {
		this.productsInquiryData = productsInquiryData;
	}

	@Override
	public String toString() {
		return "Products_inquiry [productsInquiryData = " + productsInquiryData + "]";
	}

}