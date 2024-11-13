package com.paysyslabs.bootstrap.rest;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.json.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paysyslabs.bootstrap.rest.pojo.ProductsInquiryResponse;
import com.paysyslabs.bootstrap.rest.utils.XML;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class JsonTest {
    
    private static final Logger LOG = LoggerFactory.getLogger(JsonTest.class);
    
    @Test
    public void test() {
        @SuppressWarnings("unused")
		XStream xstream = new XStream(new DomDriver());
        String str = "{\"code\":200,\"message\":\"SUCCESS\",\"data\":{\"customerCode\":\"212900000001401\",\"action\":null,\"investorTypeCode\":null,\"documentType\":null,\"customerType\":\"001\",\"displayName\":\"Muhammad Tariq\",\"status\":\"Y\",\"branchCode\":\"2129\",\"createdBy\":\"Dearling Officer\",\"remarks\":null,\"custIdentityKey\":\"CNIC\",\"custIdentityValue\":\"9090990909099\",\"createdDate\":1523272126000,\"lastModifiedDate\":1523272126000,\"products\":[{\"code\":\"0010\",\"active\":\"Y\",\"name\":\"0010 - Savings Account\",\"categoryCode\":\"0002\",\"description\":\"Accounts\",\"accountNumber\":\"2129001002454032\",\"accountTitle\":\"Muhammad Tariq\",\"avaliableBalance\":0,\"registrationCode\":null,\"investorTypeCode\":\"001\",\"operatingCode\":\"001\",\"corelatedAccountId\":\"0\",\"allowCorrelation\":\"N\"},{\"code\":\"0003\",\"active\":\"Y\",\"name\":\"0003 - Defence Savings Certificates\",\"categoryCode\":\"0001\",\"description\":\"Certificate\",\"accountNumber\":null,\"accountTitle\":\"Muhammad Tariq\",\"avaliableBalance\":10000,\"registrationCode\":\"212900030000465\",\"investorTypeCode\":\"M\",\"operatingCode\":\"001\",\"corelatedAccountId\":\"0\",\"allowCorrelation\":\"Y\"}]}}";
        JSONObject json = new JSONObject(str);
        JSONObject wrapper = new JSONObject();
        wrapper.put("data", json.get("data"));
        LOG.info("{}", wrapper);
        LOG.info("{}", XML.toString(wrapper));
        //LOG.info("{}", xstream.toXML(wrapper));
    }
    
    @Test
    public void deserialize() throws Exception {
        @SuppressWarnings("unused")
		String xml = "<response>\r\n" + 
                "  <response_code>00</response_code>\r\n" + 
                "  <response_desc>SUCCESS</response_desc>\r\n" + 
                "  <tran_ref>001217443891</tran_ref>\r\n" + 
                "  <products_inquiry>\r\n" + 
                "    <data>\r\n" + 
                "      <documentType>null</documentType>\r\n" + 
                "      <lastModifiedDate>1523272126000</lastModifiedDate>\r\n" + 
                "      <displayName>Muhammad Tariq</displayName>\r\n" + 
                "      <customerCode>212900000001401</customerCode>\r\n" + 
                "      <custIdentityValue>9090990909099</custIdentityValue>\r\n" + 
                "      <products_wrapper>\r\n" + 
                "        <products>\r\n" + 
                "          <code>0010</code>\r\n" + 
                "          <accountTitle>Muhammad Tariq</accountTitle>\r\n" + 
                "          <active>Y</active>\r\n" + 
                "          <description>Accounts</description>\r\n" + 
                "          <avaliableBalance>0</avaliableBalance>\r\n" + 
                "          <corelatedAccountId>0</corelatedAccountId>\r\n" + 
                "          <categoryCode>0002</categoryCode>\r\n" + 
                "          <accountNumber>2129001002454032</accountNumber>\r\n" + 
                "          <operatingCode>001</operatingCode>\r\n" + 
                "          <allowCorrelation>N</allowCorrelation>\r\n" + 
                "          <investorTypeCode>001</investorTypeCode>\r\n" + 
                "          <name>0010 - Savings Account</name>\r\n" + 
                "          <registrationCode>null</registrationCode>\r\n" + 
                "        </products>\r\n" + 
                "        <products>\r\n" + 
                "          <code>0003</code>\r\n" + 
                "          <accountTitle>Muhammad Tariq</accountTitle>\r\n" + 
                "          <active>Y</active>\r\n" + 
                "          <description>Certificate</description>\r\n" + 
                "          <avaliableBalance>10000</avaliableBalance>\r\n" + 
                "          <corelatedAccountId>0</corelatedAccountId>\r\n" + 
                "          <categoryCode>0001</categoryCode>\r\n" + 
                "          <accountNumber>null</accountNumber>\r\n" + 
                "          <operatingCode>001</operatingCode>\r\n" + 
                "          <allowCorrelation>Y</allowCorrelation>\r\n" + 
                "          <investorTypeCode>M</investorTypeCode>\r\n" + 
                "          <name>0003 - Defence Savings Certificates</name>\r\n" + 
                "          <registrationCode>212900030000465</registrationCode>\r\n" + 
                "        </products>\r\n" + 
                "      </products_wrapper>\r\n" + 
                "      <branchCode>2129</branchCode>\r\n" + 
                "      <customerType>Local</customerType>\r\n" + 
                "      <createdDate>1523272126000</createdDate>\r\n" + 
                "      <investorTypeCode>null</investorTypeCode>\r\n" + 
                "      <createdBy>Dearling Officer</createdBy>\r\n" + 
                "      <action>null</action>\r\n" + 
                "      <custIdentityKey>CNIC</custIdentityKey>\r\n" + 
                "      <remarks>null</remarks>\r\n" + 
                "      <status>Y</status>\r\n" + 
                "    </data>\r\n" + 
                "  </products_inquiry>\r\n" + 
                "</response>";

        String nullXml = "<response>\r\n" + 
                "  <response_code>00</response_code>\r\n" + 
                "  <response_desc>SUCCESS</response_desc>\r\n" + 
                "  <tran_ref>001217443891</tran_ref>\r\n" + 
                "  <products_inquiry>\r\n" + 
                "    <data>null</data>\r\n" + 
                "  </products_inquiry>\r\n" + 
                "</response>";
        
        JAXBContext context = JAXBContext.newInstance(ProductsInquiryResponse.class);
        Unmarshaller jaxbUnmarshaller = context.createUnmarshaller();
        ProductsInquiryResponse response = (ProductsInquiryResponse) jaxbUnmarshaller.unmarshal(new StringReader(nullXml));
        LOG.info("{}", new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(response));
    }

}
