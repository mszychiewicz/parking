User stories
1. As a driver, I want to start the parking meter, so I don’t have to pay the fine for the invalid
parking.

	Przykładowe zapytanie:

		POST http://localhost:8080/users/2/parking?locationId=3&registration=AB1234

2. As a parking operator, I want to check if the vehicle has started the parking meter.

	Przykładowe zapytanie:

		GET http://localhost:8080/locations/3/parking?locationId=3&registration=AB1234
	
Założyłem, że operatort parkingu sprawdza czy dany samochód ma włączony licznik tylko na danym parkingu.

3. As a driver, I want to stop the parking meter, so that I pay only for the actual parking time

	Przykładowe zapytanie:

		PATCH http://localhost:8080/users/2/parking?eventId=8

4. As a driver, I want to know how much I have to pay for parking.
	
	Przykładowe zapytanie:

		GET http://localhost:8080/users/1/billing

5. As a parking owner, I want to know how much money I earn any given day

	Przykładowe zapytanie: (dla strefy czasowej UTC+1:00)

		GET http://localhost:8080/users/1/earnings?date=2019-03-11T23:00:00.00000Z 


Założenia:

-użytkownik może być zarówno klientem, jak i właścicielem parkingu

-użytkownik nie jest na stałe powiazany z rejestracją pojazdu i może mieć aktywne naliczanie dla paru pojazdów na raz

-użytkownik sprawdzając koszt parkowania otrzymuje kwotę za wszystkie swoje parkowania

-użytkownik może sprawdzić koszt każdej pojedynczej akcji parkowania w swoich akcjach parkowania

-sprawdzając zarobki z danego dnia jako parametr podawany jest czas UTC rozpoczęcia dnia w strefie czasowej użytkownika

-użytkownik sprawdzając zarobki z danego dnia otrzymuje sumę ze wszystkich swoich parkingów

-naliczanie kosztów jest za każdą rozpoczętą godzinę

-operatort parkingu sprawdza czy dany samochód ma włączony licznik tylko na danym parkingu

