package com.paymentchain.billing;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BasicApplicationTests {

@Autowired
    private MockMvc mockMvc;
    @MockBean //mock the repository layer in order to have a unit test for weblayer 
    private InvoiceRepository ir;
    @MockBean //mock the mapper layer in order to have a unit test for weblayer 
    InvoiceRequestMapper irm;
    @MockBean //mock the mapper layer in order to have a unit test for weblayer 
    InvoiceResposeMapper irspm;
    private static final String PASSWORD = "admin";
    private static final String USER = "admin";

    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final String jsonContent = mapper.writeValueAsString(obj);
            return jsonContent;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Test call of create method, on weblayer.
     */
    @Test
    public void testCreate() throws Exception {
        Base64.Encoder encoder = Base64.getEncoder();
        String encoding = encoder.encodeToString((USER + ":" + PASSWORD).getBytes());
        Invoice mockdto = new Invoice();
        Mockito.when(ir.save(mockdto)).thenReturn(mockdto);
        Mockito.when(irm.InvoiceRequestToInvoice(new InvoiceRequest())).thenReturn(mockdto);
        Mockito.when(irspm.InvoiceToInvoiceRespose(mockdto)).thenReturn(new InvoiceResponse());
        this.mockMvc.perform(post("/billing").header("Authorization", "Basic " + encoding)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(mockdto))
        ).andDo(print()).andExpect(status().isOk());
    }

    /**
     * Test call of create method, on weblayer.
     */
    @Test
    public void testFindById() throws Exception {
        Base64.Encoder encoder = Base64.getEncoder();
        String encoding = encoder.encodeToString((USER + ":" + PASSWORD).getBytes());
        Invoice mockdto = new Invoice();
        mockdto.setId(1);
        Mockito.when(ir.findById(mockdto.getId())).thenReturn(Optional.of(mockdto));
        Mockito.when(irm.InvoiceRequestToInvoice(new InvoiceRequest())).thenReturn(mockdto);
        InvoiceResponse invoiceResponse = new InvoiceResponse();
        invoiceResponse.setInvoiceId(1);
        Mockito.when(irspm.InvoiceToInvoiceRespose(mockdto)).thenReturn(invoiceResponse);
        this.mockMvc.perform(get("/billing/{id}", mockdto.getId()).header("Authorization", "Basic " + encoding)
                .accept(MediaType.APPLICATION_JSON)               
        ).andDo(print()).andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.invoiceId").value(1));
    }
}

