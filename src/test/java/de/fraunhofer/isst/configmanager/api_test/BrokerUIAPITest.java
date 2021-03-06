package de.fraunhofer.isst.configmanager.api_test;

import de.fraunhofer.isst.configmanager.communication.clients.DefaultConnectorClient;
import de.fraunhofer.isst.configmanager.configmanagement.entities.config.CustomBroker;
import de.fraunhofer.isst.configmanager.configmanagement.service.BrokerService;
import de.fraunhofer.isst.configmanager.configmanagement.service.ResourceService;
import de.fraunhofer.isst.configmanager.controller.BrokerUIController;
import de.fraunhofer.isst.configmanager.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BrokerUIController.class)
public class BrokerUIAPITest {

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private transient BrokerService brokerService;

    @MockBean
    private DefaultConnectorClient defaultConnectorClient;

    @MockBean
    private ResourceService resourceService;

    @Test
    public void should_get_current_broker() throws Exception {

        CustomBroker broker = TestUtil.createCustomBroker();
        Mockito.when(brokerService.getById(broker.getBrokerUri())).thenReturn(broker);

        MvcResult result = this.mockMvc.perform(get("/api/ui/broker").
                param("brokerUri", broker.getBrokerUri().toString())).andReturn();


        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    public void should_add_new_broker() throws Exception {

        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("brokerUri", "https://example.com");
        requestParams.add("title", "CustomBroker");

        CustomBroker broker = TestUtil.createCustomBroker();
        Mockito.when(brokerService.createCustomBroker(URI.create("https://example.com"), "CustomBroker")).thenReturn(broker);

        this.mockMvc.perform(post("/api/ui/broker").params(requestParams)).andExpect(status().isOk());
    }

    @Test
    public void should_update_broker() throws Exception {

        CustomBroker broker = TestUtil.createCustomBroker();

        Mockito.when(brokerService.updateBroker(broker.getBrokerUri(), "titleNew")).thenReturn(true);

        MvcResult result = this.mockMvc.perform(put("/api/ui/broker")
                .param("brokerUri", String.valueOf(broker.getBrokerUri()))
                .param("title", "titleNew"))
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    public void should_return_broker_list() throws Exception {

        List<CustomBroker> brokers = TestUtil.brokers();
        Mockito.when(brokerService.getCustomBrokers()).thenReturn(brokers);
        MvcResult result = this.mockMvc.perform(get("/api/ui/brokers")).andReturn();

        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    public void should_delete_a_broker() throws Exception {

        CustomBroker customBroker = TestUtil.createCustomBroker();
        Mockito.when(brokerService.deleteBroker(Mockito.any(URI.class))).thenReturn(true);
        MvcResult result = this.mockMvc.perform(delete("/api/ui/broker")
                .param("brokerUri", String.valueOf(customBroker.getBrokerUri()))).andReturn();

        assertEquals(200, result.getResponse().getStatus());
    }

}
