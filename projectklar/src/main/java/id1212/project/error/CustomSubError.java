package id1212.project.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

public abstract class CustomSubError {
	@Data
	@EqualsAndHashCode(callSuper = false)
	@AllArgsConstructor
	class ApiValidationError extends CustomSubError {
	   private String object;
	   private String field;
	   private Object rejectedValue;
	   private String message;
	 
	   ApiValidationError(String object, String message) {
	       this.object = object;
	       this.message = message;
	   }
	}
}
