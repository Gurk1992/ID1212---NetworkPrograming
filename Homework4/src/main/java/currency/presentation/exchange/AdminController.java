package currency.presentation.exchange;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import currency.application.ExchangeService;
import currency.domain.ExchangeDTO;
import currency.domain.ExchangesCountDTO;
import currency.domain.IllegalExchangeException;

@Controller
@Scope("session")
public class AdminController {
	static final String ADMIN_PAGE_URL = "admin";
	static final String UPDATE_RATE_URL = "update-rate";
	private List<String> currencies;
	private ExchangesCountDTO count;
	
	@Autowired
	private ExchangeService service;
	
	

	@GetMapping("/"+ADMIN_PAGE_URL)
	public String showAdminView(RateForm rateForm, Model model) {
		
		currencies = service.findAllCurrencies();
		count = service.getCount();
		
			model.addAttribute("currencies", currencies);
			model.addAttribute("count", count.getExchangesCount());
		
    	return ADMIN_PAGE_URL;
	}
	
	@GetMapping("/"+UPDATE_RATE_URL)
	public String showUpdateView(@Valid RateForm rateForm, BindingResult bindingResult, Model model){
			
		
		return "redirect:"+ ADMIN_PAGE_URL;
	}
	@PostMapping("/"+UPDATE_RATE_URL)
	public String updateRate(@Valid RateForm rateForm, BindingResult bindingResult, Model model) throws IllegalExchangeException {
		if(bindingResult.hasErrors()) {
			model.addAttribute("RateForm", new RateForm());
			model.addAttribute("currencies", currencies);
			model.addAttribute("count", count.getExchangesCount());
			return ADMIN_PAGE_URL;
		}
		ExchangeDTO rateChange = service.doRateUpdate(rateForm.getFromCurrency(), rateForm.getToCurrency(), rateForm.getRate());
		count = service.getCount();
		model.addAttribute("RateChange", rateChange);
		model.addAttribute("count", count.getExchangesCount());
		model.addAttribute("currencies", currencies);
		return ADMIN_PAGE_URL;
	}
}
