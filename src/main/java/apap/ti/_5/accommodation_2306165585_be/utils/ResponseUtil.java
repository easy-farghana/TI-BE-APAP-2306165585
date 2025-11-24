package apap.ti._5.accommodation_2306165585_be.utils;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import apap.ti._5.accommodation_2306165585_be.restdto.response.BaseResponseDTO;

@Component
public class ResponseUtil {

    /**
     * Build a success response with given data, message and status.
     * 
     * @param data the data to be returned in the response
     * @param message the message to be returned in the response
     * @param status the status of the response
     * @return a ResponseEntity containing the response
     */
    public <T> ResponseEntity<BaseResponseDTO<T>> success(T data, String message, HttpStatus status) {
        BaseResponseDTO<T> response = new BaseResponseDTO<>();
        response.setStatus(status.value());
        response.setMessage(message);
        response.setData(data);
        response.setTimestamp(new Date());
        return new ResponseEntity<>(response, status);
    }

    /**
     * Build an error response with given message and status.
     * 
     * @param message the message to be returned in the response
     * @param status the status of the response
     * @return a ResponseEntity containing the response
     */
    public <T> ResponseEntity<BaseResponseDTO<T>> error(String message, HttpStatus status) {
        BaseResponseDTO<T> response = new BaseResponseDTO<>();
        response.setStatus(status.value());
        response.setMessage(message);
        response.setData(null);
        response.setTimestamp(new Date());
        return new ResponseEntity<>(response, status);
    }
}
