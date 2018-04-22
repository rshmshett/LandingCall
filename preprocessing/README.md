## Fall Detection Using Signal Processing 

<br> This code was inspired in livefft.py developed by https://github.com/ricklupton/livefft, which is a GUI-based python oscilloscope that takes in live data coming from a microphone and plots its frequencies and signals.  It also makes use of python_speech_features found in https://github.com/jameslyons/python_speech_features </br>

<br>This code uses a low-pass Butterworth filter, decomposes the frequency spectrum in its MFCC coefficients and applies Non-negative Matrix Decomposition on the softened frequency spectrum (V \approx W H), it uses then the H matrix value to threshold whether it is a fall (two points of contact with the ground) and whether it is not a fall (one or 0 points of contact with the ground) and spits out True when it detects a fall.</br> 

<br> After first test, I figured out it is very effective at classifying a fall at any place inside a small apartment as long as the microphone is kept in contact with the floor so it receives vibrations </br>

<br> **Current issues to be solved** </br>

<br> - It falsely classifies very high pitch sounds as falls </br>
<br> - We need to do more testing with actual falls </br>

<br> **How to run the code** </br>

<br> First, make sure you have the necessary requirements laid out in the previous readme. Second, make sure you have python_speech_features in the current working folder. </br>

<br> ./newProc.py </br>

The future version of the code will not contain the GUI. 
