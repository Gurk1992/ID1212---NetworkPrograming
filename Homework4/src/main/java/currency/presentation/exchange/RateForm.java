package currency.presentation.exchange;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

import javax.validation.constraints.Size;

import currency.util.FieldMatch;
import currency.util.Util;

@FieldMatch(first = "fromCurrency", second = "toCurrency", message="Kinda stupid changing rate of the same currency nei?!")
public class RateForm {
	
	@NotNull(message = "Please specify rate")
	@NotBlank(message = "Please select a currency")
	@Pattern(regexp = "^[\\p{L}\\p{M}*]*$", message = "Only letters are allowed")
	@Size(min=3, max=3, message= "Currency must be 3 chars long")
    private String fromCurrency;
	
	@NotNull(message = "Please specify rate")
	@NotBlank(message = "Please select a currency")
	@Pattern(regexp = "^[\\p{L}\\p{M}*]*$", message = "Only letters are allowed")
	@Size(min=3, max=3, message= "Currency must be 3 chars long")
	private String toCurrency;
	
    @NotNull(message = "Please specify rate")
    @Positive(message = "Rate must be greater than zero")
    private float rate;
     
    
    public float getRate() {
    	return rate;
    }
    public void setRate(float rate) {
    	this.rate = rate;
    }
    public String getFromCurrency() {
    	return fromCurrency;
    }
    public String getToCurrency() {
    	return toCurrency;
    }
    public void setToCurrency(String toCurrency) {
    	this.toCurrency=toCurrency;
    }
    public void setFromCurrency(String fromCurrency) {
    	this.fromCurrency=fromCurrency;
    }
    @Override
    public String toString() {
        return Util.toString(this);
    }

}
