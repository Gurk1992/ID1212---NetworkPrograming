package currency.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import currency.domain.ExchangeCount;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public interface ExchangeCountRepository extends JpaRepository<ExchangeCount, Integer>{
	@Modifying
	@Query(value="UPDATE EXCHANGESCOUNT SET EXCHANGESCOUNT = EXCHANGESCOUNT+1", nativeQuery=true)
	void updateCount();

	@Query(value="SELECT * FROM EXCHANGESCOUNT", nativeQuery=true)
	ExchangeCount getCount();
}
