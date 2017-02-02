# Verslag

Naam: Rick Vergunst  
Studentnummer: 10793925  
Universiteit van Amsterdam

### Beschrijving

Er zijn veel mensen die willen sporten, maar dit niet graag alleen doen en deze app biedt daarvoor de oplossen. In deze app
maak je een profiel aan met een aantal parameters en geef je een adress op. Vervolgens kan je naar andere gebruikers zoeken
die voldoen aan jou parameters, waarbij je zelf de radius om het adres kan aangeven. Verder kan je ook aangeven of je hetzelfde 
geslacht zoekt en hoeveel ouder en jonger de andere gebruiker mag zijn. De gevonden gebruikers kan je vervolgens een verzoek sturen
en indien zij jou accepteren, kan je samen chatten en zodoende samen sporten. Buiten het zoeken naar andere sporters zijn er ook 
schema's beschikbaar in de app. Deze schema's worden gemaakt door de gebruikers en worden beoordeeld door de gebruikers. De top 5
van elke sport is te zien in een overzicht en daarnaast kan je ook specifiek zoeken naar schema's op basis van sleutelwoorden.

### Technisch design

#### Globale-overview

![Diagram](https://github.com/Lumpsum/Programmeerproject/blob/master/doc/VerslagDiagram.png)

Op het moment dat je de app voor het eerst opstart kom je uit bij het log in scherm, waar je naast inloggen ook de optie hebt om te registreren. Indien je wil registreren doorloop je een proces van drie schermen om je profiel op te zetten. Het eerste scherm is een email en een wachtwoord voor de FireBase registratie. Vervolgens geef je algemene informatie zoals je naam en je adres en als laatste kom je uit bij het scherm waar je de sport informatie geeft en een beschrijving kan geven. Als dit allemaal met succes doorlopen is kom je uit bij de Main Activity en is het profiel aangemaakt.  
Via de Main Activity kan je navigeren naar de andere basis functies van de app namelijk het zoeken van gebruikers, je chats en de schema's. Verder kan je via de Main Activity uitloggen, navigeren naar het editten van je profiel en naar de schema's die je zelf hebt gemaakt.  
Middels de Find User Activity kan je zoeken naar users waarna je op hun respectievelijke profielen komt. Op het moment dat je iemand een verzoek verstuurd kom je weer uit bij de Main Activty, maar als er geen gebruikers meer over zijn, kom je weer uit bij het zoek scherm en kan je de parameters aanpassen. Net zoals bij de Main Activity bevatten beide schermen ook weer het menu.  
Bij de Chat Activity is er een lijst met chats waarop je kan klikken die je navigeren naar die specifieke chat met een andere gebruiker. Binnen deze chats kan je berichten versturen. Beide schermen bevatten ook weer het menu.
Bij de Scheme Activity kan je navigeren naar de top 5 schema's van de sporten en heb je verder ook de optie om een eigen schema te maken en te zoeken naar schema's. Het zoeken van schema gaat via parameters waarna je op zoek drukt en de resultaten ziet. Door op deze resultaten te klikken kom je bij de specifieke schema's uit. Verder kan je dus ook je eigen schema maken via dezelfde parameters als het zoeken. Op de specifieke pagina's van schema's is er één knop die verandert naar de user. Indien je de auteur bent kan je het schema aanpassen. Op het moment dat je geen auteur bent, kan je het schema beoordelen via sterren.

- Activities
  - Chat Activity - Bevat een lijst met chats met andere gebruikers die jou verzoek hebben geaccepteerd, of wiens verzoek jij hebt geaccepteerd.
  - CreateProfileActivity - Is een scherm waar je de basis informatie kan geven in de vorm van je naam, je adres, je geslacht en je leeftijd.
  - CreateProfileSecondActivity - Het derde profiel aanmaak scherm die vraagt om je sport, je niveau en een optionele beschrijving.
  - CreateSchemeActivity - Het creëeren van een schema die een title, een beschrijving en tot 3 sleutelwoorden kan bevatten.
  - EditProfileActvitiy - Scherm om je profiel aan te passen, die de aanwezige data inlaadt en waar je in één scherm je profiel kan aanpassen.
  - EditSchemeActivity - Scherm om je schema aan te passen die de data inlaadt van het schema en dan aanpasbaar maakt.
  - FindUserActivity - Een scherm waar je middels een aantal parameters kan zoeken naar users. Deze parameters zijn de radius om je adres, of het geslacht hetzelfde moet zijn en of je een bepaalde maximale deviatie wil in de leeftijd.
  - LogInActivity - Log in scherm met een email en wachtwoord.
  - MainActivity - De main hub van de app waar je kan uitloggen en je profiel kan aanpassen. Verder kan je gebruikers verzoeken accepteren en weigeren, je schema's verwijderen en naar deze schema's kan navigeren.
  - SchemeActivity - Het basis schema scherm waar de top 5 van alle sporten te zien zijn en kan navigeren naar het zoeken en het creëeren van schema's.
  - SearchSchemeActivity - Zoekscherm voor schema's waar je middels de sleutelwoorden kan zoeken naar schema's.
  - SignUpActivity - Eerste pagina van het registreren proces waar je een email en wachtwoord moet aangeven.
  - SpecificChatActivity - Specifieke chat met een gebruiker die de naam van de gebruiker bevat, de uitgewisselde berichten en een veld om een nieuw bericht te maken en te versturen.
  - SpecificSchemeActivity - De informatie van een schema met de huidige beoordeling van het schema. Verder kan je hier het schema beoordelen via de knop, of aanpassen als het jou schema is.
  - SpecificUserActivity - Profiel van een andere gebruiker, waar je deze gebruiker kan accepteren of verwijderen.
- Adapters
  - ChatListAdapter - Adapter voor de chat die ChatMessages bevat en weergeeft. 
  - UserRequestAdapter - Adapter die de gebruikers verzoeken bevat en weergeeft.
- Classes
  - ChatMessage - Format voor het bericht die de afzender en het bericht bevat.
  - ListItem - Format voor het weergeven van de username, maar via een onzichtbaar veld het id meegeeft.
  - Scheme - Class voor schema's die de informatie uit de firebase bevat.
  - User - Class voor de gebruiker die de informatie van een specifieke gebruiker bevat.
  - UserRequestItem - Format voor een verzoek van een gebruiker die z'n naam, geslacht, leeftijd en beschrijving bevat.
- Network
  - ASyncTask - Exterene ASyncTask die de API calls naar de Google Geocode API behandeld en het resultaat teruggeeft.

#### Gedetailleerd



### Uitdagingen



### Verdedigen van keuzes


