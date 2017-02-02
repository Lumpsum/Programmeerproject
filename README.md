# Programmeerproject 16/17

Naam: Rick Vergunst  
Studentnummer: 10793925  
Universiteit van Amsterdam  

---

### Probleem

Er zijn veel mensen die willen sporten, maar het probleem dat er dan heerst is dat ze niet de discipline hebben om alleen te gaan sporten. De oplossing hiervoor zou zijn om samen met iemand te gaan sporten, echter is dit nog vrij lastig en kent niet iedereen een persoon die ook wil gaan sporten. Verder zijn er ook geen goede wegen om nieuwe mensen te ontmoeten die wel willen sporten aangezien die personen met hetzelfde probleem rondlopen. Deze app zal daarom trachten om deze mensen te verbinden, zodat mensen die wel willen sporten, maar liever niet alleen gaan, toch gaan sporten en hun persoonlijke doelen kunnen behalen.

---

### Features

- Een profiel maken met gegevens over wat jou sport doelen zijn.
- Het vinden van personen met dezelfde doelen binnen een bepaalde radius.
- Contact hebben (via chat?) met personen die wederzijdse interesse hebben.
- Gedetailleerd zoeken naar personen met genoeg parameters.
- Contact maken met personen als beide personen elkaar accepteren.
- Het delen en beoordelen van sport schema's

---

### Wireframes

<img src="https://github.com/Lumpsum/Programmeerproject/blob/master/doc/1.png" alt="Drawing" width="33%"/>
<img src="https://github.com/Lumpsum/Programmeerproject/blob/master/doc/2.png" alt="Drawing" width="33%"/>
<img src="https://github.com/Lumpsum/Programmeerproject/blob/master/doc/3.png" alt="Drawing" width="33%"/>
<img src="https://github.com/Lumpsum/Programmeerproject/blob/master/doc/4.png" alt="Drawing" width="33%"/>
<img src="https://github.com/Lumpsum/Programmeerproject/blob/master/doc/5.png" alt="Drawing" width="33%"/>
<img src="https://github.com/Lumpsum/Programmeerproject/blob/master/doc/6.png" alt="Drawing" width="33%"/>
<img src="https://github.com/Lumpsum/Programmeerproject/blob/master/doc/7.png" alt="Drawing" width="33%"/>
<img src="https://github.com/Lumpsum/Programmeerproject/blob/master/doc/10.png" alt="Drawing" width="33%"/>
<img src="https://github.com/Lumpsum/Programmeerproject/blob/master/doc/9.png" alt="Drawing" width="33%"/>
<img src="https://github.com/Lumpsum/Programmeerproject/blob/master/doc/8.png" alt="Drawing" width="100%"/>

---

### Datasets en datasources

Elke user heeft een profiel met bepaalde data over wat voor sport diegene wil gaan beoefen en hoe ervaren de persoon is. Hierbij kan je denken aan hardlopen of fitness en bij de ervaring hoeveel jaar de persoon de sport al beoefent of bepaalde behaalde resultaten. Verder wordt er ook genoteerd wat de doelen zijn van de persoon, zodat de app personen met dezelfde doelen makkelijk kan matchen. Om al deze data bij te houden wordt er gebruik gemaakt van een FireBase, omdat je hiermee eenvoudig data kan opvragen en naartoe kan schrijven.

---

### Componenten

- Log in en registartie formulieren, gebaseerd op e-mail en wachtwoord en verder aangevuld met formulieren om het profiel vorm te geven
- Main Hub waar je personen ziet die met jou willen sporten en links om verder te navigeren door de app.
- Het zoeken naar personen in je omgeving en het navigeren naar die profielen om deze vervolgens af te wijzen of te accepteren (Tinder achtig)
- Het creëeren van sportschema's die beoordeeld kunnen worden.
- Het beoordelen en vinden van sportschema's uit verscheiden sporten.

---

### API's

- Google Maps Geocoding API
- Wger

---

### Technische problemen en en mogelijkheden

Het implementeren van een chat kan moeilijk zijn en de FireBase volgooien, FireBase biedt chat opties wat dus intressant kan zijn. Verder zal het vinden van relevante personen duidelijk gespecificeerd moeten worden waarbij de profielen veel gelijkenissen moeten tonen. Hierbij moet er ook op basis van locatie voor elke user berekend worden hoever ze zich van elkaar bevinden wat veel rekenkracht kan kosten. Een oplossing hiervoor kan zijn het opslaan van al berekende afstanden die dan alleen opnieuw worden berekend bij verandering van locaties. Wat ook een probleem kan zijn is het vinden van dezelfde personen, er zal dus iets in moeten komen dat ervoor zorgt dat personen niet constant dezelfde peronsen vinden als ze deze weigeren. Dit kan waarschijnlijk gerealiseerd worden via FireBase en bepaalde childs of branches toevoegen die de relaties tussen mensen beschrijft.

---

### Vergelijkbare apps

De eerste app die naar voren komt is Tinder, aangezien deze app op dezelfde wijze werkt. Het vinden van personen en als zij wederwzijde interesse tonen kan je met elkaar chatten. Zij maken gebruik van een Facebook profiel wat de app zeer versimpeld echter is dit geen optie voor deze app omdat je specifieke informatie nodig hebt om te matchen. Verder maken zij gebruik van een chat wat hopelijk ook mogelijk is voor deze app.  
Een andere app dit op deze app lijkt is Fitocracy die jou workout geeft om te volgen. Echter is deze app meer gebaseerd op het community aspect en worden de workouts hier gemaakt door de gebruikers van de app die deze vervolgens beoordelen. Hierdoor komen hopelijke de beste schema's naar boven zodat iedereen de maximale resultaten kan halen.

### Better Code Hub

[![BCH compliance](https://bettercodehub.com/edge/badge/Lumpsum/Programmeerproject)](https://bettercodehub.com) 
