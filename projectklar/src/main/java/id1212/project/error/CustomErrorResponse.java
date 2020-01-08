package id1212.project.error;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
@Data
public class CustomErrorResponse {

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private List<CustomErrorResponse> subErrors;
    
  

}
