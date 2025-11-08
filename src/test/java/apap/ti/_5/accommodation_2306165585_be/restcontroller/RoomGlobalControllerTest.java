package apap.ti._5.accommodation_2306165585_be.restcontroller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import apap.ti._5.accommodation_2306165585_be.restdto.response.room.RoomResponseDTO;
import apap.ti._5.accommodation_2306165585_be.restdto.response.roomtype.RoomTypeResponseDTO;
import apap.ti._5.accommodation_2306165585_be.service.room.RoomService;
import apap.ti._5.accommodation_2306165585_be.service.roomtype.RoomTypeService;

@SpringBootTest
@AutoConfigureMockMvc
public class RoomGlobalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomTypeService roomTypeService;

    @MockBean
    private RoomService roomService;

    private RoomTypeResponseDTO roomType1;
    private RoomTypeResponseDTO roomType2;

    private RoomResponseDTO room1;
    private RoomResponseDTO room2;

    @BeforeEach
    void setUp() {
        roomType1 = RoomTypeResponseDTO.builder()
                .roomTypeID("type-1")
                .name("Deluxe")
                .capacity(2)
                .price(500000)
                .build();

        roomType2 = RoomTypeResponseDTO.builder()
                .roomTypeID("type-2")
                .name("Suite")
                .capacity(4)
                .price(800000)
                .build();

        room1 = RoomResponseDTO.builder()
                .roomID("room-1")
                .name("101")
                .build();

        room2 = RoomResponseDTO.builder()
                .roomID("room-2")
                .name("102")
                .availabilityStatus(0)
                .build();
    }

    @Test
    void testGetAllRoomTypes_Success() throws Exception {
        List<RoomTypeResponseDTO> roomTypes = Arrays.asList(roomType1, roomType2);
        when(roomTypeService.getAllRoomTypes()).thenReturn(roomTypes);

        mockMvc.perform(get("/api/room-type")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("List of all room types fetched successfully"))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].name").value("Deluxe"))
                .andExpect(jsonPath("$.data[1].name").value("Suite"));

        verify(roomTypeService, times(1)).getAllRoomTypes();
    }

    @Test
    void testGetAllRooms_Success() throws Exception {
        List<RoomResponseDTO> rooms = Arrays.asList(room1, room2);
        when(roomService.getAllRooms()).thenReturn(rooms);

        mockMvc.perform(get("/api/room")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("List of all rooms fetched successfully"))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].name").value("101"))
                .andExpect(jsonPath("$.data[1].name").value("102"));

        verify(roomService, times(1)).getAllRooms();
    }

    @Test
    void testGetAllRoomTypes_EmptyList() throws Exception {
        when(roomTypeService.getAllRoomTypes()).thenReturn(List.of());

        mockMvc.perform(get("/api/room-type")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(0))
                .andExpect(jsonPath("$.message").value("List of all room types fetched successfully"));

        verify(roomTypeService, times(1)).getAllRoomTypes();
    }

    @Test
    void testGetAllRooms_EmptyList() throws Exception {
        when(roomService.getAllRooms()).thenReturn(List.of());

        mockMvc.perform(get("/api/room")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(0))
                .andExpect(jsonPath("$.message").value("List of all rooms fetched successfully"));

        verify(roomService, times(1)).getAllRooms();
    }
}
