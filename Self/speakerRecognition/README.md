# Prerequisites
  - Microsoft Speaker Recognition API
  - FFmpeg
  - Tomcat

# Steps
  - Change Authentication.java
  - Change config.properties and logback.xml
  - Build and deploy to tomcat

# Demo
  - Start up Tomcat and visit http://localhost:8080/speakerRecognition/
![images/1.png](images/1.png)
  - Keep pressing the button and say something
![images/2.png](images/2.png)
  - Wait for a while after recording
![images/3.png](images/3.png)
  - The response could be seen as below
![images/4.png](images/4.png)