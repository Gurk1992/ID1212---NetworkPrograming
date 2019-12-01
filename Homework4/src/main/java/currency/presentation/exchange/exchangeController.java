package currency.presentation.exchange;


import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;

import currency.application.ExchangeService;


@Controller
@Scope("session")
public class exchangeController {
static final String DEFAULT_PAGE_URL = "/";
static final String EXCHANGE_PAGE_URL = "exchange";
static final String DO_EXCHANGE_URL = "do-exchange";

private List<String> currencies;
@Autowired
private ExchangeService service;

	@GetMapping(DEFAULT_PAGE_URL)
	public String showDefaultView() {
		return "redirect:" +EXCHANGE_PAGE_URL;
	
	}

	@GetMapping("/"+EXCHANGE_PAGE_URL)
	public String showExchangeView(ExchangeForm exchangeForm, Model model) {
		currencies = service.findAllCurrencies();
		if(currencies !=null) {
			model.addAttribute("currencies", currencies);
		}
    	return EXCHANGE_PAGE_URL;
	}
	@GetMapping("/"+DO_EXCHANGE_URL)
	public String doExchangeView(@Valid ExchangeForm exchangeForm, BindingResult bindingResult, Model model) {
			return "redirect:"+DEFAULT_PAGE_URL;
		}
	@PostMapping("/"+DO_EXCHANGE_URL)
	public String doExchange(@Valid ExchangeForm exchangeForm, BindingResult bindingResult, Model model) {
		if(bindingResult.hasErrors()) {
			model.addAttribute("ExchangeForm", new ExchangeForm());
			model.addAttribute("currencies", currencies);
			return EXCHANGE_PAGE_URL;
		}
	
		float exchanged = service.doExchange(exchangeForm.getFromCurrency(), exchangeForm.getToCurrency(), exchangeForm.getAmount());

		model.addAttribute("exchanged", exchanged);
		model.addAttribute("currencies", currencies);
		return EXCHANGE_PAGE_URL;
	}
}
