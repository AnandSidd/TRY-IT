# TRY-IT Mobile Application
<img src="https://github.com/AnandShivansh/TRY-IT/blob/master/app/src/main/ic_launcher-playstore.png" height=150 width=150>


## Description
TRY-IT is a virtual dressing room application for >= Android 6.0.

It allows users to see how an outfit will look on them by using image processing and a body pose-estimation model.

<br/><br/>
## Screenshots
<br>  <img src="https://github.com/AnandShivansh/TRY-IT/blob/master/Screenshots/MainPage.jpeg" height=300 width=150>
 <img src="https://github.com/AnandShivansh/TRY-IT/blob/master/Screenshots/AddOutFit_from_gallery.jpeg" height=300 width=150>
<img src="https://github.com/AnandShivansh/TRY-IT/blob/master/Screenshots/Share_from_online_store.jpeg" height=300 width=150>
<img src="https://github.com/AnandShivansh/TRY-IT/blob/master/Screenshots/Select_outfit.jpeg" height=300 width=150>
<img src="https://github.com/AnandShivansh/TRY-IT/blob/master/Screenshots/Camera_Preview.jpeg" height=300 width=150></br>

<br/><br/>
## How does it work ?
### Add Outfit
There are various ways to add outfit in TRY-IT : <br/>
<ul>
<li>Add oufit from gallery from the app itself.</li>
<li>Share oufit from gallery when the app is not running, from the share pane</li>
<li>Share oufit from online shopping website when the app is not running, from the share pane. This makes a server request and then gets back image url, which is used to download the image on frontend.</li>
</ul><br/>
### Preprocess and Save the OutFit
<ul>
<li>The selected outfit is processed according to a sensitivity rate given by the user.</li>
<li>If the user is not satisfied by the preprocessing, he can use the eraser to retune it according to himself.</li>
<li>User selects a category to store the outfit.</li>
<li>The outfit stored in database.</li>
</ul>

Image processing methods to extract outfit are given below:
<ul>
 <li>Add alpha channel to image</li>
 <li>Boolean Masking (Binary Threshold)</li>
 <li>Noise Removal (Gaussian Blur)</li>
 <li>Generate mask to make background transparent</li>
 <li>Apply generated mask</li>
 <li>Find largest contour to remove unnecessary area</li>
 <li>Crop largest contour</li>
 <li>Eraser paints a tranparent layer where it is stroked</li>
 </ul>

### Fit Outfit on Camera Preview
TRY-IT uses a tensorflow-lite model to estimate certain points on user's body during camera preview.<br/> 
By using these estimated points, the outfit is placed on screen by calculating its size and position.<br/><br/>


The model estimates 14 points on user's body;<br/> 
_Top, Neck, Left Shoulder, Left Elbow, Left Wrist, Right Shoulder, Right Elbow, Right Wrist, Left Hip, Left Knee, Left Ankle, Right Hip, Right Knee, Right Ankle._<br/><br/>


There are 4 outfit categories;<br/>
_"Top", "Long Wears", "Trousers", "Shorts and Skirts"_
<br/><br/>

According to its category, the outfit size and position are calculated by using;<br/>
* Top --> Left Shoulder, Right Shoulder, Left Hip
* Long Wears --> Left Shoulder, Right Shoulder, Left Knee
* Trousers --> Left Hip, Right Hip, Left Ankle
* Shorts and Skirts --> Left Hip, Right Hip, Left Knee


<br/><br/>
### Backend
* Used Flask for API services.
* Used selenium for scraping the image link from url send in API requests.
* Used OKHTTP for API requests.
* Currently, the server is running on localhost.

## Software and Tools
* Android Studio 3.2.1
* TensorFlow-Lite
* OpenCV 4.1
* SQLite
* Flask
* Python
* Selenium


### Note
* The application has onetime onboarding screen which explains whole application. It runs only once on installation.
<br/><br/>

## Future Development
  ### Functional
  * Users will be allowed to create outfit combinations
  * Users will be allowed to take screenshot during preview with a button

  ### System
  * Semantic segmentation to extract outfit in a more efficient way
  * 3D modeling for a more realistic result
