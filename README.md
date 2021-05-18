# Skycast  
Mobile application to check weather data based on the device location. Main window shows the current weather data and the weekly view shows weather data for the next 7 days, measurement system can be switched between Metric and Imperial in the settings menu. The weekly view also utilizes OpenWeather icons to describe the current weather.

## Name  
Aleksi Pynnönen  

## Topic  
A mobile application that shows the current and future weather for the next seven days using OpenWeather API. The location is either based on the users GPS location or a given city name.  

## Target  
Android/Kotlin  

## Google Play link  
https://play.google.com/store/apps/details?id=fi.organization.skycast  
Not up at the moment as the app hasn't been approved yet but it should be up soon™

## Release 1 features  
Every feature described above has been implemented, a few bugs and some code cleanup left before final release.

## Release 2 features
* Swipe refresh to update weather data along toasts for successful and unsuccessful refreshes.
* Custom app icon.
* Replaced the old location implementation, now the app isn't reliant on other apps asking the phone for location updates and actively asks for the location data instead of just listening.
* Large scale codebase cleanup.
* More documentation for the codebase.
* Reopening the app now puts the user in the same fragment where they left off.

## Build instructions
If you plan on building the app yourself include your own openWeather API key in the local.properties file.
Example local.properties file:
```
sdk.dir=D\:\\androidSDK
API_KEY=YOUR_API_KEY_HERE
```
