package currency.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;


@Entity
@Table(name="exchangescount")
public class ExchangeCount implements ExchangesCountDTO{
	@Id
	@NotNull(message = "{exchange.from.missing}")
	@Column(name="exchangescount")
	private int exchangescount;

	@Override
	public int getExchangesCount() {
		return this.exchangescount;
	}
	public void updateExchangescount() {
		this.exchangescount = this.exchangescount+1;
	}

}
