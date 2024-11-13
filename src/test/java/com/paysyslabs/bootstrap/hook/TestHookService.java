package com.paysyslabs.bootstrap.hook;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.paysyslabs.bootstrap.rest.hook.AfterParsingParametersHook;
import com.paysyslabs.bootstrap.rest.hook.AfterResponseHook;
import com.paysyslabs.bootstrap.rest.model.ServiceBasicResponse;
import com.paysyslabs.bootstrap.rest.model.ServiceRequest;
import com.paysyslabs.bootstrap.rest.service.HooksService;

public class TestHookService implements HooksService {

	private static final Logger LOG = LoggerFactory.getLogger(TestHookService.class);
	
	@Value("${1link.service.userid}")
	private String userId;
	
	@Value("${1link.service.password}")
	private String password;
	
	@Value("${1link.service.signaturekey}")
	private String signatureKey;
	
	@Value("${1link.service.clientid}")
	private String clientId;
	
	@Value("${1link.service.channelid}")
	private String channelId;
	
	@Override
	public AfterResponseHook getAfterResponseHook() {
		// TODO Auto-generated method stub
		return new AfterResponseHook() {
			
			@Override
			public ServiceBasicResponse hook(ServiceRequest request, ServiceBasicResponse response) {
				
				ServiceBasicResponse result = response;
				
				if ( request.getType().equals("bill-inquiry") && request.getRealm().toLowerCase().trim().equals("1link-ubps") ) {
					//Map<String, Object> data = ;
					String responseData = response.getData().get("BillInquiryResult").toString();
					LOG.info("{}", responseData);
					
					try{
						String code = responseData.substring(0,2).trim();
						
						if (code.equals("00")) 
							result = new ServiceBasicResponse(code, createXml( responseData, code, "SUCCESS", request.getRef(), request.getType()) );
						
						else 
							result = new ServiceBasicResponse(code, null);
					
					}
					catch(Exception ex) {
						LOG.error("{}", ex);
						result = new ServiceBasicResponse("99", null);
					}
					
				} else if ( request.getType().toLowerCase().trim().equals("bill-payment") && request.getRealm().toLowerCase().trim().equals("1link-ubps") ) {
					//Map<String, Object> data = ;
					String responseData = response.getData().get("BillPaymentResult").toString();
					LOG.info("{}", responseData);
					
					try{
						String code = responseData.substring(0,2).trim();
						
						if (code.equals("00")) 
							result = new ServiceBasicResponse( code, createXml( responseData, code, "Success", request.getRef(), request.getType() ) );
						
						else 
							result = new ServiceBasicResponse(code, null);
					
					}
					catch(Exception ex) {
						LOG.error("{}", ex);
						result = new ServiceBasicResponse("99", null);
					}
					
				}
				
				return result;
			}
		};
	}

	@Override
	public AfterParsingParametersHook getAfterParsingParametersHook() {
		// TODO Auto-generated method stub
		return new AfterParsingParametersHook() {
			
			@Override
			public ServiceRequest hook(ServiceRequest params) {
				LOG.info("inside hook");

				LOG.info("userId: {} - password: {} - signatureKey: {} - clientId: {} - channelId: {}", userId, password,
						signatureKey, clientId, channelId);
				
				try {
					if ( params.getType().equals("bill-inquiry") && params.getRealm().toLowerCase().trim().equals("1link-ubps") ) {
						
						LOG.info("Parameters Changing and adding (bill-inquiry)");
						
						String stan = params.getParameters().get("stan").toString();
						String ucid = params.getParameters().get("ucid").toString();
						String consumerNo = params.getParameters().get("consumer_no").toString();
						
						String strToBeSigned = userId + password + clientId + channelId + stan + ucid + consumerNo;
						
						
						String signature = Sign_String(strToBeSigned, signatureKey.getBytes());
						
						params.getParameters().put("user_id", userId);
						params.getParameters().put("password", password);
						params.getParameters().put("client_id", clientId);
						params.getParameters().put("channel_id", channelId);
						params.getParameters().put("signature", signature);
						
					} else if( params.getType().equals("bill-payment") && params.getRealm().toLowerCase().trim().equals("1link-ubps") ) {
						
						LOG.info("Parameters Changing and adding (bill-payment)");
						
						String stan = params.getParameters().get("stan").toString();
						String ucid = params.getParameters().get("ucid").toString();
						String consumerNo = params.getParameters().get("consumer_no").toString();
						String authId = params.getParameters().get("auth_id");
						String amount = params.getParameters().get("amount");
						String tranDateTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
						
						String strToBeSigned = userId + password + clientId + channelId + stan + ucid + consumerNo + authId + amount + tranDateTime;
						String signature = Sign_String(strToBeSigned, signatureKey.getBytes());
						
						params.getParameters().put("user_id", userId);
						params.getParameters().put("password", password);
						params.getParameters().put("client_id", clientId);
						params.getParameters().put("channel_id", channelId);
						params.getParameters().put("signature", signature);
						params.getParameters().put("tran_datetime", tranDateTime);
						
					}
					
				} catch (Exception ex) {
					LOG.error("{}", ex);
				}
				
				return params;
			}
		};
	}
	
	public synchronized static String Sign_String(String plainText, byte[] saltBytes)
    {
        String hashValue = "";
        try
        {
            // Convert plain text into a byte array. 
            byte[] plainTextBytes = plainText.getBytes();
            // Allocate array, which will hold plain text and salt. 
            byte[] plainTextWithSaltBytes = new byte[plainTextBytes.length + saltBytes.length];
            // Copy plain text bytes into resulting array. 
            for (int i = 0; i < plainTextBytes.length; i++)
                plainTextWithSaltBytes[i] = plainTextBytes[i];

            // Append salt bytes to the resulting array. 
            for (int i = 0; i < saltBytes.length; i++)
                plainTextWithSaltBytes[plainTextBytes.length + i] = saltBytes[i];

            // Compute hash value of our plain text with appended salt. 
            byte[] hashBytes = getSHA512(plainTextWithSaltBytes);
            
            // Convert result into a base64-encoded String. 
            byte[] encoded = Base64.getEncoder().encode(hashBytes);
            hashValue = new String(encoded);
        }
        catch (Exception ex)
        {
            ex.getStackTrace();
        }        

        return hashValue;
    }
	
	private static byte[] getSHA512(byte[] sha512toBytes) {
		return DigestUtils.sha512(sha512toBytes);
	}

	private static String createXml(String responseData, String responseCode, String responseDesc, String rrn, String type)
	{
		String xmlResp = "";
		try{
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();
			// root element
			Element rootElement = doc.createElement("data");
			doc.appendChild(rootElement);
	
			if ( responseCode.equals("00") && type.trim().toLowerCase().equals("bill-inquiry") )
				doc = appendBillInquiryXmlFragment(doc, rootElement, responseData);
			
			else if ( responseCode.equals("00") && type.trim().toLowerCase().equals("bill-payment") )
				doc = appendBillPaymentXmlFragment(doc, rootElement, responseData);
	
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(writer));
			xmlResp = writer.getBuffer().toString();
		}
		catch(Exception e)
		{
			LOG.error("{}",e);
		}
		
		return xmlResp;
	}
	
	private static Document appendBillInquiryXmlFragment(Document doc, Node rootElement, String responseData)
	{
		String dueDate = "";
		String amountWithInDueDate = "";
		String amountAfterDueDate = "";
		String custName = "";
		String billMonth = "";
		String billStatus = "";
		String onelinkRRN = "";
		
		try{
			custName = responseData.substring(58, 88).trim();
			billStatus = responseData.substring(88,89).trim();
			billMonth = responseData.substring(89, 93).trim();
			dueDate = responseData.substring(93,99).trim();
			amountWithInDueDate = responseData.substring(99,113).trim();
			amountAfterDueDate = responseData.substring(113,127).trim();
			onelinkRRN = responseData.substring(127,139).trim();
			
			Element custNameElement = doc.createElement("customer_name");
			rootElement.appendChild(custNameElement);
			custNameElement.appendChild(doc.createTextNode(custName));
			
			Element billStatusElement = doc.createElement("bill_status");
			rootElement.appendChild(billStatusElement);
			billStatusElement.appendChild(doc.createTextNode(billStatus));
			
			Element billMonthElement = doc.createElement("bill_month");
			rootElement.appendChild(billMonthElement);
			billMonthElement.appendChild(doc.createTextNode(billMonth));
			
			Element dueDateElement = doc.createElement("due_date");
			rootElement.appendChild(dueDateElement);
			dueDateElement.appendChild(doc.createTextNode(dueDate));
			
			Element amountWithInDueDateElement = doc.createElement("amount_with_in_due_date");
			rootElement.appendChild(amountWithInDueDateElement);
			amountWithInDueDateElement.appendChild(doc.createTextNode(amountWithInDueDate));
			
			Element amountAfterDueDateElement = doc.createElement("amount_after_due_date");
			rootElement.appendChild(amountAfterDueDateElement);
			amountAfterDueDateElement.appendChild(doc.createTextNode(amountAfterDueDate));
			
			Element _1linkRRNElement = doc.createElement("onelink_rrn");
			rootElement.appendChild(_1linkRRNElement);
			_1linkRRNElement.appendChild(doc.createTextNode(onelinkRRN));
		}
		catch(Exception ex)
		{
			LOG.error("{}",ex);
		}
			
		return doc;
	}
	
	private static Document appendBillPaymentXmlFragment(Document doc, Node rootElement, String responseData)
	{
		String responseStan = "";
		String responseRRN = "";
		String responseDetails = "";
		
		try{
			responseStan = responseData.substring(20, 26).trim();
			responseRRN = responseData.substring(64,76).trim();
			responseDetails = responseData.substring(76, 506).trim();
			
			Element rspStanElement = doc.createElement("response_stan");
			rootElement.appendChild(rspStanElement);
			rspStanElement.appendChild(doc.createTextNode(responseStan));
			
			Element rspRRNElement = doc.createElement("response_rrn");
			rootElement.appendChild(rspRRNElement);
			rspRRNElement.appendChild(doc.createTextNode(responseRRN));
			
			Element rspDetailsElement = doc.createElement("response_details");
			rootElement.appendChild(rspDetailsElement);
			rspDetailsElement.appendChild(doc.createTextNode(responseDetails));
			
		} catch(Exception ex) {
			LOG.error("{}",ex);
		}
			
		return doc;
	}
}
