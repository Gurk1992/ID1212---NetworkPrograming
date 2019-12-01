package currency.presentation.exchange;

import currency.util.FieldMatch;
import currency.util.Util;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@FieldMatch(first = "fromCurrency", second = "toCurrency", message="Kinda stupid exchanging same currency nei?!")
public class ExchangeForm {
	
	@NotBlank(message = "Please select a currency")
	@Pattern(regexp = "^[\\p{L}\\p{M}*]*$", message = "Only letters are allowed")
	@Size(min=3, max=3, message= "Currency must be 3 chars long")
    private String fromCurrency;
	
	@NotBlank(message = "Please select a currency")
	@Pattern(regexp = "^[\\p{L}\\p{M}*]*$", message = "Only letters are allowed")
	@Size(min=3, max=3, message= "Currency must be 3 chars long")
	private String toCurrency;
	
    @NotNull(message = "Please specify Ammount")
    @PositiveOrZero(message = "Ammount must be zero or greater")
    private Integer amount;
     
    
    public Integer getAmount() {
    	return amount;
    }
    public void setAmount(Integer amount) {
    	this.amount = amount;
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
