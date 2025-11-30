package apap.ti._5.accommodation_2306165585_be.restcontroller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import apap.ti._5.accommodation_2306165585_be.exception.NotFoundException;
import apap.ti._5.accommodation_2306165585_be.restdto.request.property.AddPropertyRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.request.property.PropertyTransactionRequest;
import apap.ti._5.accommodation_2306165585_be.restdto.request.property.UpdatePropertyRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.request.property.UpdatePropertyTransactionRequest;
import apap.ti._5.accommodation_2306165585_be.restdto.request.room.AddMaintenanceRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.request.roomtype.AddRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.request.roomtype.ListAddRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.property.AllPropertyResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.property.PropertyResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.roomtype.RoomTypeResponseDTO;
import apap.ti._5.accommodation_2306165585_be.service.property.PropertyService;
import apap.ti._5.accommodation_2306165585_be.service.room.RoomService;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class PropertyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PropertyService propertyService;

    @MockBean
    private RoomService roomService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID propertyId;
    private UUID ownerId;
    private UUID roomTypeId;
    private AllPropertyResponseDTO allPropertyResponse;
    private PropertyResponseDTO propertyResponse;
    private RoomTypeResponseDTO roomTypeResponse;

    @BeforeEach
    void setUp() {
        propertyId = UUID.randomUUID();
        ownerId = UUID.randomUUID();
        roomTypeId = UUID.randomUUID();

        // Setup AllPropertyResponseDTO
        allPropertyResponse = AllPropertyResponseDTO.builder()
                .propertyID(propertyId)
                .propertyName("Test Hotel")
                .type(1)
                .totalRooms(10)
                .activeStatus(1)
                .build();

        // Setup RoomTypeResponseDTO
        roomTypeResponse = RoomTypeResponseDTO.builder()
                .roomTypeID(roomTypeId)
                .name("Deluxe")
                .floor(1)
                .capacity(2)
                .price(100000)
                .facility("WiFi, TV")
                .description("Deluxe Room")
                .capacity(5)
                .build();

        // Setup PropertyResponseDTO
        propertyResponse = PropertyResponseDTO.builder()
                .propertyID(propertyId)
                .propertyName("Test Hotel")
                .type(1)
                .province(1)
                .address("Test Address")
                .description("Test Description")
                .totalRoom(10)
                .ownerID(ownerId)
                .ownerName("Test Owner")
                .activeStatus(1)
                .listRoomType(Collections.singletonList(roomTypeResponse))
                .build();
    }

    // Test getAllProperty - Success
    @Test
    void testGetAllProperty_Success() throws Exception {
        List<AllPropertyResponseDTO> mockList = Arrays.asList(allPropertyResponse);
        when(propertyService.getAllProperties(anyMap())).thenReturn(mockList);

        mockMvc.perform(get("/api/property")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("List of all properties fetched successfully"))
                .andExpect(jsonPath("$.data[0].propertyID").value(propertyId.toString()))
                .andExpect(jsonPath("$.data[0].propertyName").value("Test Hotel"));

        verify(propertyService, times(1)).getAllProperties(anyMap());
    }

    // Test getAllProperty - With Filters
    @Test
    void testGetAllProperty_WithFilters() throws Exception {
        List<AllPropertyResponseDTO> mockList = Arrays.asList(allPropertyResponse);
        when(propertyService.getAllProperties(anyMap())).thenReturn(mockList);

        mockMvc.perform(get("/api/property")
                .param("name", "Test")
                .param("type", "1")
                .param("province", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data[0].propertyName").value("Test Hotel"));

        verify(propertyService, times(1)).getAllProperties(anyMap());
    }

    // Test getAllProperty - Empty List
    @Test
    void testGetAllProperty_EmptyList() throws Exception {
        when(propertyService.getAllProperties(anyMap())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/property")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());

        verify(propertyService, times(1)).getAllProperties(anyMap());
    }

    // Test getAllProperty - Multiple Properties
    @Test
    void testGetAllProperty_MultipleProperties() throws Exception {
        AllPropertyResponseDTO property2 = AllPropertyResponseDTO.builder()
                .propertyID(UUID.randomUUID())
                .propertyName("Hotel 2")
                .type(2)
                .build();

        List<AllPropertyResponseDTO> mockList = Arrays.asList(allPropertyResponse, property2);
        when(propertyService.getAllProperties(anyMap())).thenReturn(mockList);

        mockMvc.perform(get("/api/property")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].propertyName").value("Test Hotel"))
                .andExpect(jsonPath("$.data[1].propertyName").value("Hotel 2"));

        verify(propertyService, times(1)).getAllProperties(anyMap());
    }

    // Test getAllActiveProperty - Success
    @Test
    void testGetAllActiveProperty_Success() throws Exception {
        List<AllPropertyResponseDTO> mockList = Arrays.asList(allPropertyResponse);
        when(propertyService.getAllActiveProperties(anyMap())).thenReturn(mockList);

        mockMvc.perform(get("/api/property-active")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("List of all properties fetched successfully"))
                .andExpect(jsonPath("$.data[0].propertyID").value(propertyId.toString()));

        verify(propertyService, times(1)).getAllActiveProperties(anyMap());
    }

    // Test getAllActiveProperty - With Filters
    @Test
    void testGetAllActiveProperty_WithFilters() throws Exception {
        List<AllPropertyResponseDTO> mockList = Arrays.asList(allPropertyResponse);
        when(propertyService.getAllActiveProperties(anyMap())).thenReturn(mockList);

        mockMvc.perform(get("/api/property-active")
                .param("name", "Test")
                .param("type", "1")
                .param("province", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].propertyName").value("Test Hotel"));

        verify(propertyService, times(1)).getAllActiveProperties(anyMap());
    }


    // Test createProperty - Success
    @Test
    void testCreateProperty_Success() throws Exception {
        AddPropertyRequestDTO propertyRequest = new AddPropertyRequestDTO();
        propertyRequest.setPropertyName("New Hotel");
        propertyRequest.setType(1);
        propertyRequest.setProvince(1);
        propertyRequest.setAddress("New Address");
        propertyRequest.setDescription("New Description");
        propertyRequest.setOwnerId(ownerId);
        propertyRequest.setOwnerName("Owner Name");

        AddRoomTypeRequestDTO roomTypeRequest = new AddRoomTypeRequestDTO();
        roomTypeRequest.setName("Standard");
        roomTypeRequest.setFloor(1);
        roomTypeRequest.setUnit(5);
        roomTypeRequest.setCapacity(2);
        roomTypeRequest.setPrice(100000);
        roomTypeRequest.setFacility("WiFi");
        roomTypeRequest.setDescription("Standard Room");

        PropertyTransactionRequest request = new PropertyTransactionRequest();
        request.setProperty(propertyRequest);
        request.setRoomTypes(Collections.singletonList(roomTypeRequest));

        when(propertyService.createPropertyTransaction(any(PropertyTransactionRequest.class)))
                .thenReturn(propertyResponse);

        mockMvc.perform(post("/api/property/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Property created successfully"))
                .andExpect(jsonPath("$.data.propertyID").value(propertyId.toString()));

        verify(propertyService, times(1)).createPropertyTransaction(any(PropertyTransactionRequest.class));
    }

    // Test deleteProperty - Success
    @Test
    void testDeleteProperty_Success() throws Exception {
        doNothing().when(propertyService).deleteProperty(propertyId);

        mockMvc.perform(delete("/api/property/delete/{propertyId}", propertyId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Property with ID " + propertyId + " deleted successfully"));

        verify(propertyService, times(1)).deleteProperty(propertyId);
    }

    // Test deleteProperty - Not Found
    @Test
    void testDeleteProperty_NotFound() throws Exception {
        doThrow(new NotFoundException("Property not found")).when(propertyService).deleteProperty(propertyId);

        mockMvc.perform(delete("/api/property/delete/{propertyId}", propertyId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(propertyService, times(1)).deleteProperty(propertyId);
    }

    // Test addRoomTypeToProperty - Success
    @Test
    void testAddRoomTypeToProperty_Success() throws Exception {
        AddRoomTypeRequestDTO roomTypeRequest = new AddRoomTypeRequestDTO();
        roomTypeRequest.setName("Suite");
        roomTypeRequest.setFloor(2);
        roomTypeRequest.setUnit(3);
        roomTypeRequest.setCapacity(4);
        roomTypeRequest.setPrice(200000);
        roomTypeRequest.setFacility("WiFi, TV, Minibar");
        roomTypeRequest.setDescription("Suite Room");

        ListAddRoomTypeRequestDTO request = new ListAddRoomTypeRequestDTO();
        request.setRoomTypes(Collections.singletonList(roomTypeRequest));

        when(propertyService.addRoomTypeToProperty(eq(propertyId), any(ListAddRoomTypeRequestDTO.class)))
                .thenReturn(propertyResponse);

        mockMvc.perform(post("/api/property/add-room-type/{propertyId}", propertyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Room types added to property successfully"))
                .andExpect(jsonPath("$.data.propertyID").value(propertyId.toString()));

        verify(propertyService, times(1)).addRoomTypeToProperty(eq(propertyId), any(ListAddRoomTypeRequestDTO.class));
    }

    // Test addRoomTypeToProperty - Property Not Found
    @Test
    void testAddRoomTypeToProperty_PropertyNotFound() throws Exception {
        ListAddRoomTypeRequestDTO request = new ListAddRoomTypeRequestDTO();
        request.setRoomTypes(Collections.emptyList());

        when(propertyService.addRoomTypeToProperty(eq(propertyId), any(ListAddRoomTypeRequestDTO.class)))
                .thenThrow(new NotFoundException("Property not found"));

        mockMvc.perform(post("/api/property/add-room-type/{propertyId}", propertyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(propertyService, times(1)).addRoomTypeToProperty(eq(propertyId), any(ListAddRoomTypeRequestDTO.class));
    }

    // Test getAllProperty - With Only Name Filter
    @Test
    void testGetAllProperty_WithOnlyNameFilter() throws Exception {
        List<AllPropertyResponseDTO> mockList = Arrays.asList(allPropertyResponse);
        when(propertyService.getAllProperties(anyMap())).thenReturn(mockList);

        mockMvc.perform(get("/api/property")
                .param("name", "Test")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].propertyName").value("Test Hotel"));

        verify(propertyService, times(1)).getAllProperties(anyMap());
    }

    // Test getAllProperty - With Only Type Filter
    @Test
    void testGetAllProperty_WithOnlyTypeFilter() throws Exception {
        List<AllPropertyResponseDTO> mockList = Arrays.asList(allPropertyResponse);
        when(propertyService.getAllProperties(anyMap())).thenReturn(mockList);

        mockMvc.perform(get("/api/property")
                .param("type", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].type").value(1));

        verify(propertyService, times(1)).getAllProperties(anyMap());
    }

    // Test getAllProperty - With Only Province Filter
    @Test
    void testGetAllProperty_WithOnlyProvinceFilter() throws Exception {
        List<AllPropertyResponseDTO> mockList = Arrays.asList(allPropertyResponse);
        when(propertyService.getAllProperties(anyMap())).thenReturn(mockList);

        mockMvc.perform(get("/api/property")
                .param("province", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        

        verify(propertyService, times(1)).getAllProperties(anyMap());
    }
}