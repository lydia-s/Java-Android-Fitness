# Java-Android-Fitness
This is my fitness app. It calculates the distance a user travels and displays their current location on a map. I used async task 
to periodically update the location and a service to keep it running in the background. I make use of broadcast receivers for 
broadcasting the latitude and longitude. I also make use of intents and intent filters. 

The app should display a marker on a map and update the location of the marker every time the location changes. When a user clicks 
on the marker an info window should display showing a runner icon, details of the user's current location, their latitude and
longitude, the current distance they've travelled and the current number of calories they've burnt. 
They can also view the information below the map in a text view that displays the distance they've travelled.
They can then save the distance to a database by clicking save current and then display the current distance and number of
calories burnt in another text view. They can press the clear database button to clear the database.
