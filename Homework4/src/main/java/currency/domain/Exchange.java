package currency.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;


import currency.domain.ExchangeDTO;

@Entity
@Table(name="exchangerates")
public class Exchange implements ExchangeDTO{
	
	@Id
	@Column(name="Id")
	private int id;
	
	@NotNull(message = "{exchange.from.missing}")
	@Column(name="fromCurrency")
	private String fromCurrency;
	

	@NotNull(message = "{exchange.to.missing}")
	@Column(name="toCurrency")
	private String toCurrency;
	
	@NotNull(message = "{rate.is.missing}")
	@Column(name="Rate")
	private float rate;
	
	

	
	public float doExchange(int Ammount) {
		return Ammount*this.rate;
	}

	
	@Override
	public String getToCurrency() {
		
		return this.toCurrency;
	}


	@Override
	public String getFromCurrency() {

		return this.fromCurrency;
	}


	@Override
	public float getRate() {
		
		return this.rate;
	}


	public void updateRate(float rate) throws IllegalExchangeException {
		if(rate<=0) {
			throw new IllegalExchangeException("Attempted to update rate with a negative value: "+rate);
		}
		this.rate= rate;
	}
	
	
}
