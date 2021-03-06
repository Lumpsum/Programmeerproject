# Dag 1
Gewerkt aan het opzetten van de Readme en het uitdenken van het concept en de MVC, met daarbij mogelijke uitbreidingen indien
de tijd dit toelaat.

# Dag 2
Project aangemaakt en de volledige Git opgezet. Verder gewerkt aan het Design Document en dus nagedacht over de classes en 
hoe de database eruit moet zien. Verder ook een opzet gemaakt voor het inloggen en aanmelden en FireBase geïmplementeerd.

# Dag 3
Design Document volledig gemaakt en veel basis classes aangemaakt, zodat er een duidelijk overzicht was. Verder het inloggen
en aanmelden voorlopig afgemaakt met daarbij het creëeren van een profiel en daarbij via de Google API de locatie in te laden.
Hiermee werkt het standaard log in en aanmeld systeem, waarbij fouten nog niet worden afgevangen in bijvoorbeeld API requests.

# Dag 4
Gewerkt aan het vinden van een andere user, waarbij er gekeken wordt naar de criteria op locatie na. Lang een probleem gehad met
de asycnhrone FireBase calls en het verwerken van de resultaten, op het moment dat ze gevonden zijn. Uiteindelijk middels een
Runnable de oplossing weten te vinden, waardoor je nu een andere user kan vinden. Verder ook het bottom menu standaard gemaakt 
waardoor je gemakkelijk de tabs onderin kan inladen bij elke activity, wat veel tijd kan schelen in de toekomst.

# Dag 5

Probleem gehad met een asynchrone call binnen nog een asynchrone call, uiteindelijk opgelost door één van de calls te verwerken in de andere zodat er maar één call is en je geen problemen krijgt met het resultaat verwerken. De voorwaarde voor het zoeken worden nu goed behandeld. Verder opzet gemaakt voor de chats en users kunnen elkaar nu toevoegen en chats beginnen. Chat zelf nog niet geïmplementeerd.

# Dag 6

Werkende realtime app geïmplementeerd tussen twee users. De userIds nog niet geconvert naar de namen die daar bij horen. Verder het profiel aanmaken uitgebreid met leeftijd, geslacht en een beschrijving zodat er een uitgebreider profiel geschetst wordt. Daarnaast geïmplementeerd dat er meer users gevonden worden met de search again knop, door een lijst te creëeren tijdens de eerste search en die lijst af te lopen. Als de lijst leeg is, ga je weer terug naar het zoek scherm. Nog geen restricties gezet op de lengte van de lijst of het aantal users dat na gelopen wordt.

# Dag 7

Geimplementeerd dat over waar een user id staat nu de naam van de user zichtbaar wordt. Hierbij ervoor gekozen om bij de listview een hidden text field mee te geven met daarin het Id zodat deze gepassed worden naar de volgende activiteit en ik dus niet de hele structuur hoef om te gooien. Dit gemaakt met custom list adapters zodat de list item meerdere elementen kan bevatten. Al het hiervoor genoemde ook toegepast op de chat zodat deze duidelijk is en gepersonaliseert voor elke user.

# Dag 8

Parameters toegevoegd aan de zoekfunctie, waarbij je nu dus kan zoeken op gelijk geslacht en een radius kan aangeven voor de leeftijd van de user die hij zoekt. Edit Profile knop toegevoegd en uitgewerkt zodat je het profiel kan aanpassen. Hierbij eerst gekeken naar een dailog, maar uiteindelijk gekozen voor een nieuwe activity, aangezien er veel waardes aangepast kunnen worden. Ervoor gekozen om edit text te gebruiken te vullen met de huidige informatie en na het editten dus alles opnieuw in firebase te zetten.

# Dag 9

Begonnen met de opzet van het creëeren en zoeken van schemes. Activiteiten aangemaakt hiervoor en het basis scherm van de schemes toegevoegd. Verder het aanmaken van schemes gemaakt en het zoeken geïmplementeerd. Het probleem hierbij was dat Firebase geen SQL LIKE queries toestaat, waardoor een echte zoek functie niet mogelijk is. Als oplossing hiervoor ervoor gekozen om keywords toe te voegen en op basis van het kiezen van keywords de schemes terug kan vinden. Zoekfunctie werkt op deze manier en je kan redelijk specifiek filteren op het soort scheme wat je wilt vinden.

# Dag 10

Het editten van schemes toegevoegd waarbij ik voor dezelfde flow als het edit profile gekozen heb, dus via een nieuwe activity opnieuw de data in de firebase zetten. Verder bij elke scheme toegevoegd dat je kan raten indien het scheme niet van jou is. Dit gebeurt via een star rating, waarbij in firebase wordt bijgehouden wat de rating is, en bij elke nieuwe rating het opnieuw berekend wordt.

# Dag 11

De lijsten van de user gevuld met zijn schemes en die middels een longclick te verwijderen zijn, verder de top 5 beste schemes van running en fitness weergegeven. Lastige is dat orderByChild ascending geordend is, maar dit opgelost door elk nieuwe element aan het begin van de array toe te voegen. Verder begonnen met het opschonen van de code waarbij er comments worden toegeveogd, methodes nader benoemd en nieuwe methodes toegevoegd, zodat het een mooier geheel wordt.

# Dag 12

Alle files volledig gecomment naar de conventies die gebruikelijk zijn voor Java. Verder ook nog methodes toegevoegd om duplicaten te verwijderen en aan de hand van Better Code m'n code kwaliteit verhoogd. Lastige hiervan is dat m'n cases te lang zijn, maar een alternatief niet echt handig of mogelijk is. Verder zijn de onCreate methodes te lang omdat je de xml elementen assigned, maar dit lijkt mij geen probleem als je dit goed ordent.

# Dag 13

De layout van elke file aangepast, waarbij ik ervoor gekozen heb om een afbeeldingen op de achtergrond te doen met daarop doorzichtige blokken met de content. Verder veel buttons omgezet naar ImageButtons zodat ik gebruik kan maken van vector afbeeldingen om de app er mooier uit te laten zien. Daarnaast via Adobe CC een kleurenschema uitgekozen, zodat de kleuren goed bij elkaar matchen.

# Dag 14

Begonnen met het opschonen van de code, door het extraheren van methodes uit klasses. Aan de hand van Better Code alles beoordeeld en aan de hand daarvan geprobeerd een zo hoog mogelijk cijfer ervan te maken. Verder ervoor gekozen om twee objecten te implementeren namelijk User en Scheme. Via deze objecten verminder ik de branch points en is de code overzichtelijker. Daarnaast zorgt het ervoor dat ik met weinig regels toch data kan ophalen van de user.

# Dag 15

De laatste hand gelegd aan het opschonen van de code aan de hand van Better Code. Heel veel .onCreate's verkort door er methodes van te maken, waarbij ik bijvoorbeeld het assignen van de elementen in een methode plaats om zo de rest te verkorten. Verder ook methodes verkort door er meerdere methodes van te maken en een aantal switches vervangen door andere structuren om zo het aantal branch points verder terug te brengen. 
