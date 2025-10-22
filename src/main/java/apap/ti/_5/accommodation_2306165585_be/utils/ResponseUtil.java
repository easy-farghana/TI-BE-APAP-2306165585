package apap.ti._5.accommodation_2306165585_be.utils;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import apap.ti._5.accommodation_2306165585_be.restdto.response.BaseResponseDTO;

@Component
public class ResponseUtil {
    public <T> ResponseEntity<BaseResponseDTO<T>> success(T data, String message, HttpStatus status) {
        BaseResponseDTO<T> response = new BaseResponseDTO<>();
        response.setStatus(status.value());
        response.setMessage(message);
        response.setData(data);
        response.setTimestamp(new Date());
        return new ResponseEntity<>(response, status);
    }

    public <T> ResponseEntity<BaseResponseDTO<T>> error(String message, HttpStatus status) {
        BaseResponseDTO<T> response = new BaseResponseDTO<>();
        response.setStatus(status.value());
        response.setMessage(message);
        response.setData(null);
        response.setTimestamp(new Date());
        return new ResponseEntity<>(response, status);
    }
}
