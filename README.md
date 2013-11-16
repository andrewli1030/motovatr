motovatr
========
MotovatrSense contains modified code from Lab 2 that will upload activity data directly to our Firebase data store.

MotovatrLock contains the Android code for our lock screen widget.

MotovatrAndroidClient1 contains the client code to make HTTP requests to our servlet and is packaged into a jar for apps that want to connect to our platform. 

The code running on our servlet is contained in folder MotovatrPlatfrm. This platform code communicates with Firebase to retrieve requested data. Currently the servlet is launched locally, and so you will not be able to make calls to it unless I have it running and you are connected to the same local network. It may be easiest to do a demo sometime.
