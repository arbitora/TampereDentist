# TampereDentist
Mobiiliohjelmointi harjoitustyö.

15.5.2016

This program lists all Dentists from Tampere.
By selecting a dentist, you will get more detailed information and the following options:
- View dentist's location on map (opens Maps application).
- Dial to the given phonenumber(opens Dialer with the phonenumber).
- Open the give website (opens Browser and directs to the given website).

Uses SQLite database to store the information on cache to avoid unnecessary process of getting the data via network and listing it on the ListView. Also allows the application to work in offline mode.
