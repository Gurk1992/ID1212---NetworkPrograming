DROP TABLE IF EXISTS exchangerates;

CREATE TABLE exchangerates (
  Id INT AUTO_INCREMENT  PRIMARY KEY,
  fromCurrency char(3) NOT NULL,
  toCurrency char(3) NOT NULL,
  Rate decimal(9,2) NOT NULL
);

INSERT INTO exchangerates(fromCurrency, toCurrency, Rate) VALUES 
('SEK','USD',0.10),('USD','SEK',9.56),('USD','EUR',0.91),('EUR','USD',1.10),('CHF','SEK',9.57),('SEK','CHF',0.10),('SEK','GDP',0.08),
('GDP','SEK',12.39),('GDP','USD',1.29),('GDP','CHF',1.29),('CHF','USD',1.00),('CHF','GDP',0.77),('CHF','EUR',0.91),('GDP','EUR',1.17),
('SEK','EUR',0.09),('USD','GDP',0.77),('USD','CHF',1.00),('EUR','SEK',10.55),('EUR','GDP',0.85),('EUR','CHF',1.10);

DROP TABLE IF EXISTS exchangescount;

CREATE TABLE exchangescount (
	exchangescount INT NOT NULL PRIMARY KEY
);
INSERT INTO exchangescount VALUES (0);