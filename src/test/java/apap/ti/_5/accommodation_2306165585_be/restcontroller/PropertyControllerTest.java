package apap.ti._5.accommodation_2306165585_be.restcontroller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Arrays;
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
import apap.ti._5.accommodation_2306165585_be.restdto.request.property.PropertyTransactionRequest;
import apap.ti._5.accommodation_2306165585_be.restdto.request.property.UpdatePropertyTransactionRequest;
import apap.ti._5.accommodation_2306165585_be.restdto.request.property.UpdatePropertyRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.request.roomtype.ListAddRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.request.roomtype.UpdateRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.property.AllPropertyResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.property.PropertyResponseDTO;
import apap.ti._5.accommodation_2306165585_be.service.property.PropertyService;

@SpringBootTest
@AutoConfigureMockMvc
public class PropertyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PropertyService propertyService;

    @Autowired
    private ObjectMapper objectMapper;

    private AllPropertyResponseDTO allPropertyResponseDTO1;
    private AllPropertyResponseDTO allPropertyResponseDTO2;
    private PropertyResponseDTO propertyResponseDTO;
    private PropertyTransactionRequest createRequest;
    private UpdatePropertyTransactionRequest updateRequest;
    private UpdatePropertyRequestDTO updatePropertyRequestDTO;
    private UpdateRoomTypeRequestDTO updateRoomTypeRequestDTO;
    private ListAddRoomTypeRequestDTO addRoomTypeRequest;

    @BeforeEach
    void setUp() {
        // Setup test data
        allPropertyResponseDTO1 = AllPropertyResponseDTO.builder()
                .propertyID("property-1")
                .propertyName("Hotel Paradise")
                .type(1)
                .totalRooms(50)
                .activeStatus(1)
                .build();
        
        allPropertyResponseDTO2 = AllPropertyResponseDTO.builder()
                .propertyID("property-2")
                .propertyName("Beach Resort")
                .type(2)
                .totalRooms(30)
                .activeStatus(1)
                .build();
        
        propertyResponseDTO = PropertyResponseDTO.builder()
                .propertyID("property-1")
                .propertyName("Hotel Paradise")
                .type(1)
                .address("123 Main St")
                .province(1)
                .description("A beautiful hotel in the city center")
                .totalRoom(50)
                .income(1000000)
                .activeStatus(1)
                .listRoomType(Arrays.asList())
                .ownerName("John Doe")
                .ownerID(UUID.randomUUID())
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();
        
        createRequest = new PropertyTransactionRequest();
        // Set necessary fields for createRequest
        
        updatePropertyRequestDTO = new UpdatePropertyRequestDTO();
        // Set necessary fields like propertyId, propertyName, type, address, etc.
        
        updateRoomTypeRequestDTO = new UpdateRoomTypeRequestDTO();
        // Set necessary fields for room type
        
        updateRequest = new UpdatePropertyTransactionRequest();
        updateRequest.setProperty(updatePropertyRequestDTO);
        updateRequest.setRoomTypes(Arrays.asList(updateRoomTypeRequestDTO));
        
        addRoomTypeRequest = new ListAddRoomTypeRequestDTO();
        // Set necessary fields for addRoomTypeRequest
    }

    @Test
    void testGetAllProperty_Success() throws Exception {
        // Arrange
        List<AllPropertyResponseDTO> properties = Arrays.asList(allPropertyResponseDTO1, allPropertyResponseDTO2);
        when(propertyService.getAllProperties()).thenReturn(properties);

        // Act & Assert
        mockMvc.perform(get("/api/property")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("List of all properties fetched successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].propertyID").value("property-1"))
                .andExpect(jsonPath("$.data[1].propertyID").value("property-2"));

        verify(propertyService, times(1)).getAllProperties();
    }

    @Test
    void testGetAllProperty_EmptyList() throws Exception {
        // Arrange
        when(propertyService.getAllProperties()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/property")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));

        verify(propertyService, times(1)).getAllProperties();
    }

    @Test
    void testGetAllActiveProperty_Success() throws Exception {
        // Arrange
        List<AllPropertyResponseDTO> activeProperties = Arrays.asList(allPropertyResponseDTO1);
        when(propertyService.getAllActiveProperties()).thenReturn(activeProperties);

        // Act & Assert
        mockMvc.perform(get("/api/property-active")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("List of all properties fetched successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].propertyID").value("property-1"));

        verify(propertyService, times(1)).getAllActiveProperties();
    }

    @Test
    void testGetPropertyById_Success() throws Exception {
        // Arrange
        String propertyId = "property-1";
        when(propertyService.getPropertyById(propertyId)).thenReturn(propertyResponseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/property/{propertyId}", propertyId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Property fetched successfully"))
                .andExpect(jsonPath("$.data.propertyID").value("property-1"))
                .andExpect(jsonPath("$.data.propertyName").value("Hotel Paradise"));

        verify(propertyService, times(1)).getPropertyById(propertyId);
    }

    @Test
    void testGetPropertyById_WithDateParams_Success() throws Exception {
        // Arrange
        String propertyId = "property-1";
        String checkIn = "2024-12-01T14:00:00";
        String checkOut = "2024-12-05T11:00:00";
        LocalDateTime checkInDate = LocalDateTime.parse(checkIn);
        LocalDateTime checkOutDate = LocalDateTime.parse(checkOut);
        
        when(propertyService.getPropertyById(propertyId, checkInDate, checkOutDate))
                .thenReturn(propertyResponseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/property/{propertyId}", propertyId)
                .param("checkIn", checkIn)
                .param("checkOut", checkOut)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Property fetched successfully"))
                .andExpect(jsonPath("$.data.propertyID").value("property-1"));

        verify(propertyService, times(1)).getPropertyById(propertyId, checkInDate, checkOutDate);
    }

    @Test
    void testGetPropertyById_NotFound() throws Exception {
        // Arrange
        String propertyId = "non-existent-property";
        when(propertyService.getPropertyById(propertyId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/property/{propertyId}", propertyId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(propertyService, times(1)).getPropertyById(propertyId);
    }

    @Test
    void testCreateProperty_Success() throws Exception {
        // Arrange
        when(propertyService.createPropertyTransaction(any(PropertyTransactionRequest.class)))
                .thenReturn(propertyResponseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/property/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Property created successfully"))
                .andExpect(jsonPath("$.data.propertyID").value("property-1"));

        verify(propertyService, times(1)).createPropertyTransaction(any(PropertyTransactionRequest.class));
    }

    @Test
    void testUpdateProperty_Success() throws Exception {
        // Arrange
        when(propertyService.updatePropertyTransaction(any(UpdatePropertyTransactionRequest.class)))
                .thenReturn(propertyResponseDTO);

        // Act & Assert
        mockMvc.perform(put("/api/property/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Property updated successfully"))
                .andExpect(jsonPath("$.data.propertyID").value("property-1"));

        verify(propertyService, times(1)).updatePropertyTransaction(any(UpdatePropertyTransactionRequest.class));
    }

    @Test
    void testUpdateProperty_NotFound() throws Exception {
        // Arrange
        when(propertyService.updatePropertyTransaction(any(UpdatePropertyTransactionRequest.class)))
                .thenThrow(new NotFoundException("Property not found"));

        // Act & Assert
        mockMvc.perform(put("/api/property/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());

        verify(propertyService, times(1)).updatePropertyTransaction(any(UpdatePropertyTransactionRequest.class));
    }

    @Test
    void testDeleteProperty_Success() throws Exception {
        // Arrange
        String propertyId = "property-1";
        doNothing().when(propertyService).deleteProperty(propertyId);

        // Act & Assert
        mockMvc.perform(delete("/api/property/delete/{propertyId}", propertyId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Property with ID " + propertyId + " deleted successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(propertyService, times(1)).deleteProperty(propertyId);
    }

    @Test
    void testDeleteProperty_NotFound() throws Exception {
        // Arrange
        String propertyId = "non-existent-property";
        doThrow(new NotFoundException("Property not found"))
                .when(propertyService).deleteProperty(propertyId);

        // Act & Assert
        mockMvc.perform(delete("/api/property/delete/{propertyId}", propertyId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(propertyService, times(1)).deleteProperty(propertyId);
    }

    @Test
    void testAddRoomTypeToProperty_Success() throws Exception {
        // Arrange
        String propertyId = "property-1";
        when(propertyService.addRoomTypeToProperty(eq(propertyId), any(ListAddRoomTypeRequestDTO.class)))
                .thenReturn(propertyResponseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/property/add-room-type/{propertyId}", propertyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addRoomTypeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Room types added to property successfully"))
                .andExpect(jsonPath("$.data.propertyID").value("property-1"));

        verify(propertyService, times(1)).addRoomTypeToProperty(eq(propertyId), any(ListAddRoomTypeRequestDTO.class));
    }

    @Test
    void testAddRoomTypeToProperty_PropertyNotFound() throws Exception {
        // Arrange
        String propertyId = "non-existent-property";
        when(propertyService.addRoomTypeToProperty(eq(propertyId), any(ListAddRoomTypeRequestDTO.class)))
                .thenThrow(new NotFoundException("Property not found"));

        // Act & Assert
        mockMvc.perform(post("/api/property/add-room-type/{propertyId}", propertyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addRoomTypeRequest)))
                .andExpect(status().isNotFound());

        verify(propertyService, times(1)).addRoomTypeToProperty(eq(propertyId), any(ListAddRoomTypeRequestDTO.class));
    }
}