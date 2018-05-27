# udacity-android-advanced-nanodegree
As part of Google Udacity Developer Scholarship awarded to me, I have to complete Udacity's Advanced Nanodegree for Android. All my projects will be displayed in this repository

### Project 1: Sandwich App
This app was developed to parse API response(JSON) without any 3rd party libraries and display it using a recycler view. 
The recycler view click would open up new activities with all the data populated. The key areas in this project-
* Coordinate Layout
* Parallax Effect for app bar using Design Library
* Constraint layout inside Coordinator layout using nested scroll view
* ButterKnife

### Project 2: Popular Movies Stage 1 (WATCHIFY)
This app was developed to get content from API and display it using recycler view. The Key areas in this project in addition to coordinator layout, parallax effect, butterknife (All previous projects) are :
* First Run Screens (Fragments which run only on first run to educate the user => ViewPager and Fragment Adapter)
* Using Volley (Efficient use of Library) - The app always checks for cache before making a call and always uses cache in case there is data availavle
* Using Picasso to load movie images/posters/backdrops
* Use Of Parcelable to transfer data with Intent
* Card Views
TODO:
* Animation - Animate M and W by rotating 180 degree on the first run screens
* Pull down to refresh
* Pagination after first 10 movies
* Use View Model
* Option to change grid view to linear detailed view
