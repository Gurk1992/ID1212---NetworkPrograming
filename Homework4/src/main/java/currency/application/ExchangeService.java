package currency.application;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import currency.domain.*;
import currency.repository.*;

@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
@Service
public class ExchangeService {
	 @Autowired
	    private ExchangeRepository exchangeRepo;
	 @Autowired
	 private ExchangeCountRepository countRepo;

	public List<String> findAllCurrencies() {
		return exchangeRepo.findDistinctCurrency();
	}

	public float doExchange(String fromCurrency, String toCurrency, Integer amount) {
	
		Exchange exchange = exchangeRepo.findByFromCurrencyAndToCurrency(fromCurrency, toCurrency);
		float exchanged = exchange.doExchange(amount);
		countRepo.updateCount();
		return exchanged;
	}

	public ExchangeDTO doRateUpdate(String fromCurrency, String toCurrency, float rate) throws IllegalExchangeException {
		Exchange exchange = exchangeRepo.findByFromCurrencyAndToCurrency(fromCurrency, toCurrency);
		if(exchange == null) {
			throw new IllegalExchangeException("Currecnies do not exist");
		}
		exchange.updateRate(rate);
		return exchange;
	}
	public ExchangesCountDTO getCount() {
	
		return countRepo.getCount();
	}
	
	 

	
}
 
