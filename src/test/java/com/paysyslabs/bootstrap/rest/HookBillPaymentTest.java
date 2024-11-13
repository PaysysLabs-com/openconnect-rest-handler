package com.paysyslabs.bootstrap.rest;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.mashape.unirest.http.Unirest;
import com.paysyslabs.bootstrap.hook.TestHookService;
import com.paysyslabs.bootstrap.rest.config.ServiceRequestInboundConfiguration;
import com.paysyslabs.bootstrap.rest.dummy.DummyChannel;
import com.paysyslabs.bootstrap.rest.entities.WSConfig;
import com.paysyslabs.bootstrap.rest.entities.WSEndpointConfig;
import com.paysyslabs.bootstrap.rest.entities.WSResponseDefinition;
import com.paysyslabs.bootstrap.rest.enums.WSRequestFormat;
import com.paysyslabs.bootstrap.rest.enums.WSResponseFormat;
import com.paysyslabs.bootstrap.rest.handler.ServiceQueueWorker;
import com.paysyslabs.bootstrap.rest.repo.WSConfigRepository;
import com.paysyslabs.bootstrap.rest.repo.WSEndpointConfigRepository;
import com.paysyslabs.bootstrap.rest.repo.WSResponseDefinitionRepository;
import com.paysyslabs.bootstrap.rest.service.DataService;
import com.paysyslabs.bootstrap.rest.service.HooksService;
import com.paysyslabs.bootstrap.rest.service.TokenService;
import com.paysyslabs.bootstrap.rest.utils.MapEntryConverter;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import com.thoughtworks.xstream.io.xml.Xpp3Driver;

@Ignore
@SuppressWarnings("deprecation")
@RunWith(SpringRunner.class)
@EnableJpaRepositories(basePackages = { "com.paysyslabs.bootstrap.rest" })
@EntityScan(basePackages = { "com.paysyslabs.bootstrap.rest" })
@DataJpaTest(showSql = true)
@ContextConfiguration(classes = { DataService.class, TestHookService.class, TokenService.class })
public class HookBillPaymentTest {

    @SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(RequestTest.class);

    @Autowired
    private DataService dataService;

    @Autowired
    private HooksService hooksService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private WSEndpointConfigRepository endpointConfigRepository;

    @Autowired
    private WSResponseDefinitionRepository responseDefinitionRepository;

    @Autowired
    private WSConfigRepository configRepository;

    private ServiceQueueWorker worker;

    private XStream xStream;

    @Before
    public void setup() throws Exception {
        worker = new ServiceQueueWorker("test-worker", 1, false, new DummyChannel(), "queue", hooksService, dataService,
                tokenService, "result-queue", "saf-queue", ServiceRequestInboundConfiguration.SUPPORTED_REQUESTS,
                ServiceRequestInboundConfiguration.RESPONSE_FORMATTERS);

        xStream = new XStream((new Xpp3Driver(new XmlFriendlyReplacer("_-", "_"))));
        xStream.registerConverter(new MapEntryConverter());
        xStream.alias("request", Map.class);
        
        Unirest.setHttpClient(ServiceRequestInboundConfiguration.unsafeHttpClient());
    }
    
    @Test
    public void request() throws Exception {
    	WSConfig config = new WSConfig();
        config.setType("1link-ubps");
        config.setBaseUrl("https://40.68.189.168:444/UBPSWebService/UBPSWS_Service.asmx");
        config = configRepository.save(config);
        
        WSResponseDefinition definition = new WSResponseDefinition();
        definition.setConfig(config);
        definition.setMatchCode("NA");
        definition.setOurCode("00");
        definition.setOurDescription("Success");

        definition = responseDefinitionRepository.save(definition);
        
        definition = new WSResponseDefinition();
        definition.setConfig(config);
        definition.setMatchCode("00");
        definition.setOurCode("00");
        definition.setOurDescription("Success");

        definition = responseDefinitionRepository.save(definition);
        
        definition = new WSResponseDefinition();
        definition.setConfig(config);
        definition.setMatchCode("22");
        definition.setOurCode("22");
        definition.setOurDescription("Invalid Signature");

        definition = responseDefinitionRepository.save(definition);
        
        definition = new WSResponseDefinition();
        definition.setConfig(config);
        definition.setMatchCode("95");
        definition.setOurCode("95");
        definition.setOurDescription("Web Service timed out or is not available");

        definition = responseDefinitionRepository.save(definition);
        
        definition = new WSResponseDefinition();
        definition.setConfig(config);
        definition.setMatchCode("89");
        definition.setOurCode("89");
        definition.setOurDescription("Bill already paid");

        definition = responseDefinitionRepository.save(definition);
        
        WSEndpointConfig second = new WSEndpointConfig();
        second.setConfig(config);
        second.setType("bill-payment");
        second.setEndpointTemplate(null);
        second.setDataTemplate(
                "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' xmlns:tem='http://tempuri.org/'>\r\n" + 
                "		<soapenv:Header/>\r\n" + 
                "		<soapenv:Body>\r\n" + 
                "			<tem:BillPayment>\r\n" + 
                "				<tem:_sUserID>{user_id}</tem:_sUserID>\r\n" + 
                "				<tem:_sPwd>{password}</tem:_sPwd>\r\n" + 
                "				<tem:_sClientID>{client_id}</tem:_sClientID>\r\n" + 
                "				<tem:_sChannelID>{channel_id}</tem:_sChannelID>\r\n" + 
                "				<tem:_sSTAN>{stan}</tem:_sSTAN>\r\n" + 
                "				<tem:_sCompanyID>{ucid}</tem:_sCompanyID>\r\n" + 
                "		        <tem:_sConsumerNo>{consumer_no}</tem:_sConsumerNo>\r\n" + 
                "		        <tem:_sAuthID>{auth_id}</tem:_sAuthID>\r\n" + 
                "		        <tem:_sAmount>{amount}</tem:_sAmount>\r\n" + 
                "		        <tem:_sTransactionDateTime>{tran_datetime}</tem:_sTransactionDateTime>\r\n" + 
                "		        <tem:_sSignature>{signature}</tem:_sSignature>\r\n" + 
                "	         </tem:BillPayment>\r\n" + 
                "	   </soapenv:Body>\r\n" + 
                "  </soapenv:Envelope>");
        second.setExpectedFields("user_id,password,client_id,channel_id,amount,consumer_no,ucid,stan,auth_id,tran_ref,tran_datetime,signature");
        second.setRequestFormat(WSRequestFormat.SOAP);
        second.setRequestHeaders("Content-Type: text/xml || SOAPAction: \"http://tempuri.org/BillPayment\"");
        second.setResponseFormat(WSResponseFormat.XML);
        second.setResponseCodePath(null);
        second.setResponseIncludePaths("s|Envelope>s|Body>BillPaymentResponse>BillPaymentResult");
        second.setGuaranteed(false);

        second = endpointConfigRepository.save(second);
        
        //bill-payment,10,03452653695,TELNOR01,100001,000001,123456789012
        Map<String, String> request = new HashMap<>();
        request.put("realm", "1link-ubps");
        request.put("type", "bill-payment");
        request.put("amount", "10");
        request.put("consumer_no", "03452653695");
        request.put("ucid", "TELNOR01");
        request.put("stan", "000005");
        request.put("auth_id", "100005");
        request.put("tran_ref", "100000000005");

        String xml = xStream.toXML(request);

        worker.handle(1, null, xml.getBytes());
    }
}
