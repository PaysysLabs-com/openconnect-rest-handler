package com.paysyslabs.bootstrap.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
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

import com.paysyslabs.bootstrap.rest.config.ServiceRequestInboundConfiguration;
import com.paysyslabs.bootstrap.rest.dummy.DummyChannel;
import com.paysyslabs.bootstrap.rest.entities.WSConfig;
import com.paysyslabs.bootstrap.rest.entities.WSEndpointConfig;
import com.paysyslabs.bootstrap.rest.entities.WSResponseDefinition;
import com.paysyslabs.bootstrap.rest.entities.WSTokenConfiguration;
import com.paysyslabs.bootstrap.rest.enums.WSRequestFormat;
import com.paysyslabs.bootstrap.rest.enums.WSResponseFormat;
import com.paysyslabs.bootstrap.rest.enums.WSTokenExpiryType;
import com.paysyslabs.bootstrap.rest.handler.ServiceQueueWorker;
import com.paysyslabs.bootstrap.rest.repo.WSConfigRepository;
import com.paysyslabs.bootstrap.rest.repo.WSEndpointConfigRepository;
import com.paysyslabs.bootstrap.rest.repo.WSResponseDefinitionRepository;
import com.paysyslabs.bootstrap.rest.repo.WSTokenConfigurationRepository;
import com.paysyslabs.bootstrap.rest.service.DataService;
import com.paysyslabs.bootstrap.rest.service.HooksService;
import com.paysyslabs.bootstrap.rest.service.HooksServiceImpl;
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
@ContextConfiguration(classes = { DataService.class, HooksServiceImpl.class, TokenService.class })
public class RequestTest {

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
    private WSTokenConfigurationRepository tokenConfigurationRepository;

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
    }

    @Test
    public void oauth() throws Exception {
        WSConfig config = new WSConfig();
        config.setType("apigee");
        config.setBaseUrl("http://40.68.189.168:9001");
        config = configRepository.save(config);

        WSConfig application = new WSConfig();
        application.setType("apigee-app");
        application.setBaseUrl("http://40.68.189.168:9002");
        application = configRepository.save(application);

        WSTokenConfiguration tokenConfiguration = new WSTokenConfiguration();
        tokenConfiguration.setTokenField("Token");
        tokenConfiguration.setExpiryField("expires_in");
        tokenConfiguration.setExpiryType(WSTokenExpiryType.SECONDS);

        tokenConfiguration = tokenConfigurationRepository.save(tokenConfiguration);

        String user = "bxOwwx3DkJr23xig4iPGbN9XbA3tzGbL";
        String pass = "VGEKbvKZA7oCligG";

        WSEndpointConfig login = new WSEndpointConfig();
        login.setConfig(config);
        login.setType("apigee-token");
        login.setDataTemplate("grant_type=client_credentials");
        login.setEndpointTemplate("/oauth/v1/generate");
        login.setRequestFormat(WSRequestFormat.QUERY_STRING);
        login.setResponseFormat(WSResponseFormat.JSON);
        login.setRequestHeaders(String.format("Host: 10.1.10.112:9001||Authorization: Basic %s",
                Base64.encodeBase64String(String.format("%s:%s", user, pass).getBytes())));
        login.setResponseIncludePaths("Token,expires_in");
        login.setTokenConfiguration(tokenConfiguration);
        login.setGuaranteed(false);

        login = endpointConfigRepository.save(login);

        WSResponseDefinition definition = new WSResponseDefinition();
        definition.setConfig(application);
        definition.setMatchCode("91");
        definition.setOurCode("91");
        definition.setOurDescription("Bad Request");

        definition = responseDefinitionRepository.save(definition);
        
        definition = new WSResponseDefinition();
        definition.setConfig(application);
        definition.setMatchCode("00");
        definition.setOurCode("00");
        definition.setOurDescription("Success");

        definition = responseDefinitionRepository.save(definition);
        
        definition = new WSResponseDefinition();
        definition.setConfig(application);
        definition.setMatchCode("10");
        definition.setOurCode("10");
        definition.setOurDescription("Duplicate Transaction");

        definition = responseDefinitionRepository.save(definition);

        WSEndpointConfig second = new WSEndpointConfig();
        second.setConfig(application);
        second.setType("apigee-authenticated");
        second.setEndpointTemplate("/v1/kpkpsc/payment/perform");
        second.setDataTemplate(
                "{\"ChannelId\":\"00000001\",\"AccountNumber\":\"4142148938\",\"AccountRelationshipId\":\"6243860100000075\",\"AccountType\":\"20\",\"AccountCurrency\":\"586\",\"TransactionAmount\":\"000000001000\",\"FeeAmount\":\"000000000100\",\"FEDAmount\":\"000000000100\",\"Email\":\"harisazfar@paysyslabs.com\",\"MobileNumber\":\"923453094958\",\"CNIC\":\"3520270471919\",\"BillTransactionId\":\"000000014\",\"ShortChannelId\":\"11\",\"TransactionType\":\"02\",\"TransactionDate\":\"20180416\",\"TransactionTime\":\"161701\",\"STAN\":\"611234\",\"RRN\":\"613456123583\",\"ReserveField\":\"\"}");
        second.setRequestFormat(WSRequestFormat.SOAP);
        second.setRequestHeaders("Host: 10.1.10.112:9002||Content-Type: application/json||Authorization: Bearer {AUTH_TOKEN}");
        second.setResponseFormat(WSResponseFormat.JSON);
        second.setResponseCodePath("ResponseCode");
        second.setResponseIncludePaths("CoreBankingAuthorizationID,TransactionLogId,ResponseMessage");
        second.setGuaranteed(false);
        second.setTokenRequest(login);

        second = endpointConfigRepository.save(second);

        Map<String, String> request = new HashMap<>();
        request.put("realm", "apigee-app");
        request.put("type", "apigee-authenticated");
        request.put("tran_ref", UUID.randomUUID().toString());

        String xml = xStream.toXML(request);

        worker.handle(1, null, xml.getBytes());
        //worker.handle(2, null, xml.getBytes());
    }

    // @Test
    public void test() throws Exception {
        WSConfig config = new WSConfig();
        config.setType("nbp");
        config.setBaseUrl("https://nbp-service-internal.nbp.p.azurewebsites.net");
        config = configRepository.save(config);

        WSEndpointConfig login = new WSEndpointConfig();
        login.setConfig(config);
        login.setType("nbp-login");
        login.setEndpointTemplate("/api/v1/authenticate");
        login.setExpectedFields("username,password");
        login.setDataTemplate("");
        login.setRequestFormat(WSRequestFormat.SOAP);
        login.setResponseFormat(WSResponseFormat.JSON);
        login.setResponseCodePath("responseCode");
        login.setResponseIncludePaths("data>token,data>details");
        login.setRequestHeaders(
                "X-Auth-Username: {username}||X-Auth-Password: {password}||X-Device-Token: 1234||X-Device-Type: 1234");
        login.setGuaranteed(false);

        login = endpointConfigRepository.save(login);

        WSResponseDefinition definition = new WSResponseDefinition();
        definition.setConfig(config);
        definition.setMatchCode("00");
        definition.setOurCode("00");
        definition.setOurDescription("Logged in!");

        definition = responseDefinitionRepository.save(definition);

        Map<String, String> request = new HashMap<>();
        request.put("realm", "nbp");
        request.put("type", "nbp-login");
        request.put("tran_ref", UUID.randomUUID().toString());
        request.put("username", "haristest");
        request.put("password", "Abcd@1234");

        String xml = xStream.toXML(request);

        worker.handle(1, null, xml.getBytes());
    }

}
