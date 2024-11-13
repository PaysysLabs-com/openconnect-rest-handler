package com.paysyslabs.bootstrap.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
public class CRMRequestTest {

    @SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(CRMRequestTest.class);

    @Autowired
    private WSEndpointConfigRepository endpointConfigRepository;

    @Autowired
    private WSResponseDefinitionRepository responseDefinitionRepository;

    @Autowired
    private WSConfigRepository configRepository;

    @Autowired
    private DataService dataService;

    @Autowired
    private HooksService hooksService;
    
    @Autowired
    private TokenService tokenService;
    
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
    }
    
    @Test
    public void crmAccountInquiry() throws Exception {
        WSConfig config = new WSConfig();
        config.setType("nbpl-conv-host");
        config.setBaseUrl("http://13.73.143.92:81/AccInformation/ProfileConnector.asmx");
        config = configRepository.save(config);
        
        /*WSConfig application = new WSConfig();
        application.setType("nbpl-islamic-host");
        application.setBaseUrl("http://40.68.189.168/AccInformation/ProfileConnectorIslamic.asmx");
        application = configRepository.save(application);*/

        WSEndpointConfig endpointConfig = new WSEndpointConfig();
        
        endpointConfig.setDataTemplate("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\">\r\n" + 
        		"    <soapenv:Header />\r\n" + 
        		"    <soapenv:Body>\r\n" + 
        		"        <tem:CustomerAccInfo>\r\n" + 
        		"            <tem:AccountNo>{account_no}</tem:AccountNo>\r\n" + 
        		"        </tem:CustomerAccInfo>\r\n" + 
        		"    </soapenv:Body>\r\n" + 
        		"</soapenv:Envelope>");
        //endpointConfig.setEndpointTemplate(null);
        endpointConfig.setExpectedFields("account_no,tran_ref");
        endpointConfig.setRequestFormat(WSRequestFormat.SOAP);
        endpointConfig.setResponseCodePath("soap|Envelope>soap|Body>CustomerAccInfoResponse>CustomerAccInfoResult>dtCustomerInfo>DataTable>ERROR");
        endpointConfig.setResponseFormat(WSResponseFormat.NBP_XML);
        endpointConfig.setResponseIncludePaths("soap|Envelope>soap|Body>CustomerAccInfoResponse>CustomerAccInfoResult");
        endpointConfig.setType("GetAccountInformation");
        endpointConfig.setConfig(config);
        endpointConfig.setRequestHeaders("SOAPAction: \"http://tempuri.org/CustomerAccInfo\"||Content-Type: text/xml;charset=UTF-8");
        endpointConfig.setGuaranteed(false);
        //endpointConfig.setTokenRequest(null);
        //endpointConfig.setTokenConfiguration(null);
        
        endpointConfig = endpointConfigRepository.save(endpointConfig);
        
        WSResponseDefinition definition = new WSResponseDefinition();
        definition.setConfig(config);
        definition.setMatchCode("NO ERROR");
        definition.setOurCode("00");
        definition.setOurDescription("SUCCESS");

        definition = responseDefinitionRepository.save(definition);
        
        definition = new WSResponseDefinition();
        definition.setConfig(config);
        definition.setMatchCode("NO TRANSACTIONS FOUND");
        definition.setOurCode("02");
        definition.setOurDescription("NO TRANSACTIONS FOUND");

        definition = responseDefinitionRepository.save(definition);
        
        definition = new WSResponseDefinition();
        definition.setConfig(config);
        definition.setMatchCode("Invalid Account Number");
        definition.setOurCode("01");
        definition.setOurDescription("Invalid Account Number");

        definition = responseDefinitionRepository.save(definition);
        
        definition = new WSResponseDefinition();
        definition.setConfig(config);
        definition.setMatchCode("Account Number length must be 10 digit");
        definition.setOurCode("05");
        definition.setOurDescription("Account Number length must be 10 digit");

        definition = responseDefinitionRepository.save(definition);
        
        Map<String, String> request = new HashMap<>();
        request.put("realm", "nbpl-conv-host");
        request.put("type", "GetAccountInformation");
        request.put("account_no", "4142148938");
        request.put("tran_ref", UUID.randomUUID().toString());

        String xml = xStream.toXML(request);

        worker.handle(1, null, xml.getBytes());
        //worker.handle(2, null, xml.getBytes());
    }

}
