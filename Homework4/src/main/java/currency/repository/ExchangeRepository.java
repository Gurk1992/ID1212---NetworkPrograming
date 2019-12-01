package currency.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import currency.domain.Exchange;


@Repository
@Transactional(propagation = Propagation.MANDATORY)
public interface ExchangeRepository extends JpaRepository<Exchange, Integer>{


Exchange findByFromCurrencyAndToCurrency(String fromCurrency, String toCurrency);
	
@Query(value="SELECT DISTINCT FROMCURRENCY from EXCHANGERATES",nativeQuery=true)
List<String> findDistinctCurrency();
	
}
