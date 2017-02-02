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
<img src="https://github.com/Lumpsum/Programmeerproject/blob/master/doc/4.png" alt="Drawing" width="40%"/>

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
  
#### FireBase

- Chats
  - UserId
    - Unique message ID
      - messageText
      - messageUser
      
- Schemes
  - Category
    - Title
      - Author
      - Description
      - RateAmount
      - Rating
      - Keywords
        - Keyword
      - Users
        - UserId
        
- Users
  - UserId
    - Age
    - City
    - Description
    - FirstName
    - LastName
    - Gender
    - Level
    - Location
    - Number
    - Sport
    - Street
    - Chats
      - UserId
    - RefusedUsers
      - UserId
    - Schemes
      - Category
        - Title

#### Gedetailleerd

##### ASyncTask

De eerste relevante classes die uitleg verdient is de ASyncTask. In deze class worden calls gedaan naar de Google Geocode API. Hierbij is het belangrijk om de data zo aan te leveren dat de resultaten eruit komen zoals je wilt. Hiervoor is er dus een preset gemaakt om zo de call altijd goed te laten werken. Een specifieke API call ziet er als volgt uit: https://maps.googleapis.com/maps/api/geocode/json?address=STREET+NUMBER,CITY&key=API_KEY . Deze class krijgt dus drie parameters gegeven en geeft hier een resultaat terug. Dit resultaat is in JSON format, dus kan eenvoudig gebruikt worden om te kijken wat het resultaat is. Allereerst wordt er naar de status van de results gekeken. Indien deze een OK geeft is het resultaat goed en is de locatie daadwerkelijk bestaand en vervolgens worden de breedte- en lengtegraden opgehaald.

##### User

De User class bevat de informatie van een profiel. Via FireBase kunnen deze gegevens gemakkelijk opgehaald en vervolgens gebruikt worden. Hiervoor is het belangrijk dat de database referentie precies verwijst naar waar een profiel is. Binnen deze ap p zou dit het volgende pad als gevolg hebben: Users/UserId. Als je vervolgens hiervan de values ophaald is de user class gevuld en kan je informatie direct gebruiken. Deze class wordt gebruikt bij de Main Activity en bij de FindUserActivity om data snel op te halen en weer te geven.

##### FindUserActivity

In deze activity worden gebruikers gematcht aan de hand van gegeven voorwaarde. Allereerst wordt er gekeken naar de RefusedUsers van de gebruiker en of de andere gebruiker hierin voorkomt. Als dit zo is, hebben beide gebruikers elkaar al geweigerd en is er sowieso geen match. Als de gebruiker echter niet voorkomt wordt er  gekeken naar of de sport en het niveau van gebruikers matchen. Verder kan er ook nog een afstand aangegeven worden die als radius fungeert om te kijken. Om dit te berekenen worden de Lat en Long van beide gebruikers bekeken en gestopt in de distance methode. Indien deze afstand binnen de radius valt en de andere variabelen ook matchen wordt de gebruiker voorgesteld als match. Echter is er ook nog de optie om extra te controleren voor geslacht en of de leeftijd binnen een bepaalde marge valt. De app zoekt eerst door gebruiker basis heen en vindt alle matchende gebruikers. Deze lijst geeft hij vervolgens door naar de volgende activiteit die deze lijst afloopt tot er geen gebruikers meer zijn, of de gebruiker een verzoek verzendt.

##### SpecificChatActivity

De chat wordt voornamelijk verzorgd door de ingebouwde FireBaseAdapter. Deze adapter neemt een bepaald object aan en geeft wijzigen weer binnen de ListView indien de gebruiker iets verzendt. In dit geval bestaat dit uit ChatMessage objecten. Deze bevatten op z'n beurt het bericht en de zender van het bericht. Al deze data wordt opgeslagen in de FireBase en op het moment dat de adapter nieuwe data ziet in de FireBase geeft hij het meteen weer.

##### SearchSchemeActivity

Deze activity bevat de zoekfunctie, die zoekt naar schema's die matchen aan jou criteria. Om dit te verwezenlijken kan de gebruiker tot 3 sleutelwoorden kiezen en deze worden opgeslagen. Vervolgens loopt hij door de FireBase heen bij de corresponderende categorie en bekijkt de sleutelwoorden van elk schema. Indien ze overeenkomen is het een resultaat en wordt hij toegevoegd aan de resultaten, waarna de gebruiker erheen kan navigeren.

#### ListItem

Deze class is gemaakt om de userId's om te zetten naar de naam van de gebruiker. Op het moment dat je dit wil doen binnen FireBase zal je diep in de code moeten kijken en constant dezelfde informatie op moeten vragen, wat inefficient is. Deze class bevat zowel de username als het userId, waarbij de laatste onzichtbaar is gemaakt. Zo kan elke listview dus gevuld worden met userNames maar kunnen in de backend toch nog de userId's gebruikt worden.

### Uitdagingen

#### Asynchroon

Het eerste probleem waar ik tegenaan ben gelopen tijdens het proces was de asyncroniteit van de FireBase calls. Op het moment dat je dus een FireBase call maakt gebeurt dit asycnhroon en laadt de app verder, als je vervolgens de resultaten wil gebruiken moet je dus op de één of andere manier weten dat de call klaar is en de resultaten er zijn. Dit is geen probleem als je text in de activiteit wil aanpassen, aangezien je dit later wil doen, maar op het moment dat je een nieuwe activiteit wil aanroepen op basis van die call heb je een probleem aangezien je dit niet binnen deze call kan doen.  
Dit probleem kwam ik specifiek tegen bij het vinden van gebruikers die matchen. Hierbij moet ik in FireBase de gebruikers informatie ophalen en die vergelijken met de ingelogde gebruiker zijn gegegevens. Na het matchen wil ik dus een nieuwe activiteit starten met de gematchede gebruikers, maar dit kan niet via het returnen aangezien dat synchroon gebeurt en de call dus niet snel genoeg uitgevoerd wordt. De oplossing hiervoor was het aanmaken van een thread die geroepen wordt na het vinden van de resultaten van FireBase. Deze thread start de nieuwe activiteit wat wel mogelijk is door de dynamische context en ik zodoende dus de asynchrone resultaten toch kan gebruiken voor het aanroepen van een nieuwe activiteit.  
Ditzelfde probleem kwam ik later tegen doordat ik een asynchrone call in een asynchrone call had. Doordat beide calls asynchroon waren liepen ze apart van elkaar en kwamen de resultaten door elkaar heen terug, wat de resultaten negatief beïnvloeden en incorrect maakten. Een thread was hier echter niet de oplossing omdat ik de resultaten direct nodig had binnen de call en dus niet achteraf. De oplossing hiervoor was de calls samentrekken in één call en middels diepe nesting het tot één asynchrone call te maken. Het nadeel hiervan is dus dat de nesting heel diep zit, maar ik heb vooralsnog geen andere elegantere oplossing hiervoor kunnen vinden.

#### Weergeven van ListItems en de data die door wordt gegeven

In de app maak ik gebruik van een aantal listitems die userId's moeten doorgeven als data naar de volgende activiteit. Via de normale adapters kan dit wel, maar dan geeft hij ook de userId weer in de lijst. Dit is totaal niet gebruiksvriendelijk aangezien je als gebruiker nooit de id's van iedereen kan onthouden en het gewoon fijner is om te kijken naar een voor- en achternaam. Als oplossing hiervoor heb ik ervoor gekozen om een verborgen textview mee te geven per listitem die het userId bevat. De standaard listitems staan dit echter niet toe aangezien je dan maar één textview kan vullen en je dus een keuze moet maken tussen de naam en de userId. Om dit verhelpen heb ik dus een aparte adapter geschreven die de functionaliteiten van een standaard adapter heeft, maar wel meerdere textviews toestaat. Zodoende kon ik dus de zichtbare textviews vullen met gebruiksvriendelijke informatie van de user, maar via een verborgen scherm toch de userId's doorgeven, zodat de opeenvolgende schermen wel de functionaliteit konden behouden, zonder constant door de FireBase heen te zoeken en de userId's te zoeken bij de gegevens.

#### Schema's zoeken

Voor de schema's had ik bedacht om ook een zoek functie te implementeren, waarmee je dus schema's kon zoeken die voldoen aan jou voorwaarden. Echter kwam ik er tijdens het programmeren achter dat FireBase geen SQL Like achtige functionaliteit bevat en je dus niet via substrings op titel kon zoeken. Verder zijn er ook nog geen plugins voor FireBase die soortgelijke functionaliteit realiseren, waardoor het maken van een zoekmachine een onmogelijke taak werd. Zodoende moest ik dus een soort zoekfunctie verzinnen die wel past binnen de mogelijkheden van FireBase. Als oplossing hiervoor heb ik keywords toegevoegd per schema die vooropgesteld zijn, waarvan er 1 verplicht is en tot 3 optioneeel. Door deze keywords te gebruiken kan ik specifiek zoeken binnen FireBase naar de aanwezigheid van deze keywords en matchende schema's vinden en weergeven.

#### Veranderingen tov het design document

##### Classes

Bij de classes heb ik bij de activiteiten het creëeren van het profiel verdeeld over meerdere activiteiten. Dit heb ik gedaan ivm de ruimte en het feit dat het profiel steeds meer informatie ging bevatten, waardoor de ruimte te beperkt werd. Hierbij is de signOut activity wel verwijderd aangezien dit simpelweg in een methode past die aangeroepen kan wordne.  
Verder zijn het editten van de profielen en schema's ook geëxtraheerd van de basis functies, zodat de overzichtelijkheid van de functionaliteiten vergroot wordt. Je kan nu namelijk goed in één opslag zien wat voor schermen er zijn en wat er gebeurt.  
Als laatste heb ik een aantal classes en adapters toegevoegd. De custom adapters, ChatListAdapter en UserRequestAdapter, zijn er om de list items een custom uiterlijk te geven, die vervolgens gevuld worden met de bijbehorende classes zoals ListItem, ChatMessage en UserRequestItem. Van te voren had ik hier niet aan gedacht en tijdens het programmeren kwam ik achter de beperkte mogelijkheden van de standaard adapters. Verder zijn er uiteindelijk ook nog een User en Scheme class toegevoegd. Via deze classes is het zeer eenvoudig om FireBase data binnen te halen, wat de code kwaliteit verhoogd. 

##### Database

Naast de classes zijn er ook uitbreidingen te zien bij de database tov het design document. Er is ervoor gekozen om de chats een aparte branch te geven, zodat de berichten en chats apart opgeslagen worden. Dit was nodig vanuit de standaard aangeleverde FireBaseAdapter die de berichten real-time binnenhaald. Verder is te zien dat een User binnen de FireBase een aantal extra kinderen heeft gekregen die meer informatie geven. Dit komt doordat gaandeweg het proces specifiekere parameters betere zoekresultaten zouden geven en de informatie toch wel essentieel leek om een goed beeld te kunnen scheppen van wat iemand de gebruiker is. Als laatste zijn de schema's ook iets uitgebreid. Zo zijn er sleutelwoorden toegevoegd, die de zoek functionaliteit mogelijk maken. (Hierboven genoemd) Daarnaast worden er ook users bijgehouden, zodat je weet wie een schema al beoordeeld heeft en je dus niet een schema oneindig vaak nieuwe beoordelingen kan geven en zodoende onterecht de beoordeling omhoog of omlaag kan werken.

### Verdedigen van keuzes

#### Keuzen voor zoekmachine

Voor de zoekmachine is er nu gekozen om via keywords de schema's te filteren op de inhoud, waarbij je dus afhankelijk bent van de mogelijke keywords. Een andere keuze hier had kunnen zijn om een SQL database te implementeren voor de schema's, zodat de SQL LIKE queries een optie werden. Dit was echter veel extra werk geweest, aangezien SQL en FireBase dan samen zouden moeten functioneren en data over dragen wat veel extra druk op de app legt. Hierdoor zou de prestatie van de app achteruit kunnen gaan. Verder was de beschikbare tijd relatief kort, indien er meer tijd was geweest had het implementeren van een SQL database wel een optie geweest, om de zoekfunctie geadvanceerder te maken.

#### Creatie en aanpassen van schema's en profielen in een aparte activiteit

Er is ervoor gekozen om het aanpassen en creëeren in aparte activiteiten te doen, terwijl dit mogelijk in dezelfde activiteiten had gekund door de overlap tussen beide. Ik heb er echter voor gekozen om dit niet te doen, om de overzichtelijkheid van de structuur van de activiteiten te waarborgen en elke grote functionaliteit z'n eigen activiteit te geven. Verder zijn er wel veel overeenkomsten, maar zijn er substantiele verschillen die het implementeren lastig zouden maken. Dit zou meer tijd kosten dan het recreëeren van gedeeltes en het aanpassen op bepaalde punten. In het geval dat er meer tijd zou zijn geweest, had dit mogelijk wel een nettere oplossing kunnen zijn.

#### Vormgeving schema's

Voor de schema's is gekozen voor een titel, keywords en een beschrijving. In plaats hiervan had ik ook een API zoals Wqer kunnen gebruiken om oefeningen te laten kiezen en de hoeveelheid en eventueel daar nog een beschrijving bij. Het probleem hiervan is dat dit voor hardlopen niet echt mogelijk is. Daarnaast is de flexibiliteit van de vastgelegde oefeningen laag en als een oefening ontbreekt je hem dus niet kan toevoegen. Verder is het lastig om een schema op deze manier dynamisch aangezien iedereen anders traint kwa volume en mogelijkheden. Door alleen een beschrijving te gebruiken geef je de gebruiker veel vrijheid om een schema op te stellen dat precies weergeeft wat de auteur bedoeld.

#### Action bar verwijderd

Binnen deze app is ervoor gekozen om de action bar te verwijderen. Ik heb dit gedaan, zodat ik de mogelijke ruimte per scherm zou vergroten. Sommige schermen moeten aardig wat informatie weer geven en hoe meer ruimte je hiervoor creëert hoe beter het eruit kan zien en hoe meer je toe laat. Zodoende geef ik de gebruiker een betere ervaring en ziet de applicatie er over het algemeen overzichtelijker uit.

#### Bottom menu

Voor het menu heb ik gekozen voor een bottom menu met vier knoppen. Dit heb ik gebaseerd op de material guidelines van Android die aangeeft dat een bottom menu een goede keuzes is indien je tussen de 3 en 5 tabjes wil implementeren waar ik dus voor gekozen hebt.

#### Knop tekst/icoon

Voor het menu heb ik gekozen voor vector iconen die herkenbaar zijn voor de gebruiker. Zo zijn de functionaleiten in één oogopslag duidelijk en herkbaar voor de gebruiker. Met dezelfde redenen heb ik er ook voor gekozen om de log uit en edit profile knop een icoon te geven. Echter is er voor sommige knoppen gekozen voor tekst. Dit heb ik gedaan, om overlap te voorkomen. Zo zijn er meerdere knoppen voor zoeken en hier wilde ik graag onderscheid tussen maken omdat deze functies ook niets met elkaar te maken hebben. Verder werkt de functionaliteit van de Edit/Rate knop doormiddel van de tekst die erop staat, dus is het logisch om hier voor tekst te kiezen in plaats van iconen, omdat dit anders problemen zou opleveren mbt de functionaliteit.

#### Classes

De laatste dagen heb ik nog een User en Scheme class toegevoegd en geïmplementeerd om zo de gegevens op te halen. Dit werkt een stuk beter dan m'n eerst switch met veel branch points. Achteraf gezien had ik dit vanaf het begin al moeten doen, omdat dit duidelijker is, overzichtelijker en simpeler. Echter heb ik nog nooit gewerkt met object georienteerde dingen, waardoor ik hier snel voor zou kiezen. Als ik het project nog een keer zou doen zou ik dit vaker toepassen.

#### Zoeken van gebruikers

De app zoekt eerst door heel de gebruikers database en vindt alle matches, waarna hij deze lijst doorgeeft en de gebruikers één voor één aangeeft als mogelijke match. Het voordeel hiervan is dat je maar één keer door de gebruikers heeft hoeft te lopen en dan alle matches vindt. Dit is efficient bij kleine databases aangezien je snel door de gebruikers heen loopt. Mijn database is zo klein, dat dit dus naar mijn inzien een betere aanpak is. In het geval dat de database groter zou worden, zou er een limiet moeten worden gesteld, omdat het zoeken dan te lang kan duren.
