motovatr
========
MotovatrSense contains modified code from Lab 2 that will upload activity data directly to our Firebase data store.

MotovatrLock contains the Android code for our lock screen widget.

MotovatrAndroidClient1 contains the client code to make HTTP requests to our servlet and is packaged into a jar for apps that want to connect to our platform (i.e. MotovatrLock)

The code running on our servlet is contained in folder MotovatrPlatfrm. This platform code communicates with Firebase to retrieve requested data. Currently the servlet is launched locally, and so you will not be able to make calls to it unless I have it running and you are connected to the same local network. 

Until our servlet code is live on a public server, it will be easiest to demo the Lock screen in person.

The other folders (i.e. Motobetr, MotovatrTree, piechart) are other instances that use our platform and are works in progress. We are focusing on MotovatrLock as our end-to-end app submission for Lab 4.
