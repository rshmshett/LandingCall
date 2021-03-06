#!/usr/bin/env python3

###################################################
# Live Non Negative Matrix Decomposition
# for Elderly Fall Detection
#
#  We perfom a low pass filter on the F domain by using the 
#  Butterworth filter coefficients and then we compute the 
#  energy of the low frequency signature harmonics to detect
#  whether a fall occurred.
#
#      Original code cloned from 
#  https://github.com/ricklupton/livefft
#
#  Modified to make it do what we want
#
###################################################

from __future__ import division
from pyqtgraph.Qt import QtGui, QtCore
import numpy as np
from scipy import signal
from scipy.signal import filtfilt
from numpy import nonzero, diff
import pyqtgraph as pg
from recorder import SoundCardDataSource
from sklearn.decomposition import NMF
from python_speech_features import mfcc
import pickle
import socket
import struct
import fcntl
import RPi.GPIO as GPIO

from sklearn.externals import joblib
#Global variable declarations:
FILTER_CUTOFF= 0.125 # 0.125 Nyquist
FS = 44000          # sampling rate
ORDER = 5           #order of the low pass filter

GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(False)
GPIO.setup(21, GPIO.OUT)
GPIO.setup(20, GPIO.OUT)

global oldH
oldH= 0.3

def get_ip_address(ifname):
    print("Finding IP")
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    return socket.inet_ntoa(fcntl.ioctl(
        s.fileno(),
        0x8915,  # SIOCGIFADDR
        struct.pack(bytes('256s','utf-8'), bytes(ifname[:15],'utf-8'))
    )[20:24])


#Initializing socket
#sock= socket.socket(socket.AF_INET, socket.SOCK_STREAM)
#localhost=sock.gethostname()
#localhost= sock.gethostbyname(hostname)
#sock.bind(("192.168.50.142", 8081))



modelSVM= open('landingCall.pickle', 'rb')
modelSVM= joblib.load(modelSVM)


# Based on function from numpy 1.8
def rfftfreq(n, d=1.0):
    if not isinstance(n, int):
        raise ValueError("n should be an integer")
    val = 1.0/(n*d)
    N = n//2 + 1
    results = np.arange(0, N, dtype=int)
    return results * val


def fft_buffer(x):
    window = np.hanning(x.shape[0])
   # print(mfcc(window))
#    mfccDetails= np.array([np.mean(mfcc(window)), np.std(mfcc(window))])

    # Calculate FFT
    fx = np.fft.rfft(window * x)

    # Convert to normalised PSD
    Pxx = abs(fx)**2 / (np.abs(window)**2).sum()

    # Scale for one-sided (excluding DC and Nyquist frequencies)
    Pxx[1:-1] *= 2

    return np.sqrt(Pxx)

class LiveFFTWindow(pg.GraphicsWindow):
    def __init__(self, recorder):
        super(LiveFFTWindow, self).__init__()
        self.recorder = recorder
        self.paused = False
        self.downsample = True
        self.nextRow()
        # Data ranges
        self.resetRanges()
        self.c = socket.socket() 
        # Timer to update plots
        self.c.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        self.c.bind((get_ip_address('wlan0'),8081))
        self.c.listen(5)
        self.s, self.addr = self.c.accept()     
        self.timer = QtCore.QTimer()
        self.timer.timeout.connect(self.update)
        interval_ms = 1000 * (self.recorder.chunk_size / self.recorder.fs)
        print("Updating graphs every %.1f ms" % interval_ms)
        self.timer.start(interval_ms)

    def resetRanges(self):
        self.timeValues = self.recorder.timeValues
        self.freqValues = rfftfreq(len(self.timeValues),
                                   1./self.recorder.fs)
    def find_peaks(self, Pxx):
    ###################################################
    # this function was modified to provide the 
    # Butterworth filter with a cutoff of 0.125 Nyquist
    ##################################################
        # filter parameters
        global oldH
        mfccMean= np.mean(mfcc(Pxx), axis=0)
        mfccStd= np.std(mfcc(Pxx), axis=0)
        b, a= signal.butter(ORDER, 0.5, btype= 'low') #Butterworth filter
        Pxx_smooth = filtfilt(b, a, abs(Pxx))
        peakedness = abs(Pxx) / Pxx_smooth
        modelNMF= NMF(n_components=1, init='random', random_state=0)
        res= Pxx_smooth.reshape(-1, 1)
        
        fvec= np.concatenate((mfccMean, mfccStd)).reshape(-1, 1)

        res[res<0]= 0 #substitute negative values by 0 
        W= modelNMF.fit_transform(res)
        H= modelNMF.components_
        
        SVMpred= modelSVM.predict(fvec.T)
        fallSignal= False 
        isTrue= True
        if (H>0.1 or (H-oldH)>0.15) and np.max(mfcc(Pxx)) < 22:
            fallSignal=True
            GPIO.output(20,GPIO.HIGH)
            GPIO.output(20, GPIO.HIGH)
            GPIO.output(20, GPIO.HIGH)
            GPIO.output(20, GPIO.LOW)
            GPIO.output(20, GPIO.HIGH)
            GPIO.output(20, GPIO.LOW)
            GPIO.output(21, GPIO.LOW)
        else:
            GPIO.output(20, GPIO.LOW)
            GPIO.output(21, GPIO.HIGH)
            while isTrue: 
              #  print ('Got connection from', self.addr)
                self.s.send(bytes('1', 'utf-8'))
                isTrue=False
        print(fallSignal) 
        # find peaky regions which are separated by more than 10 samples
        peaky_regions = nonzero(peakedness > 1)[0]
        edge_indices = nonzero(diff(peaky_regions) > 10)[0]  # RH edges of peaks
        edges = [0] + [(peaky_regions[i] + 5) for i in edge_indices]
        if len(edges) < 2:
            edges += [len(Pxx) - 1]
        
        peaks = []
        for i in range(len(edges) - 1):
            j, k = edges[i], edges[i+1]
            peaks.append(j + np.argmax(peakedness[j:k]))
        return peaks, W, H 
   
    def plotPeaks(self, Pxx):
        # find peaks bigger than a certain threshold
        peaks1, W, H = self.find_peaks(Pxx)
        peaks = [p for p in peaks1 if Pxx[p] > 0.05]

#        for p in peaks:
#            if old:
#                t = old.pop()

    def update(self):
        data = self.recorder.get_buffer()
        weighting = np.exp(self.timeValues / self.timeValues[-1])
        Pxx = fft_buffer(weighting * data[:, 0])

        if self.downsample:
            downsample_args = dict(autoDownsample=False,
                                   downsampleMethod='subsample',
                                   downsample=10)
        else:
            downsample_args = dict(autoDownsample=True)

        
        self.plotPeaks(Pxx)

# Setup recorder
recorder = SoundCardDataSource(num_chunks=3,
                               sampling_rate=FS,
                               chunk_size=4*1024)
win = LiveFFTWindow(recorder)

## Start Qt event loop unless running in interactive mode or using pyside.
if __name__ == '__main__':
    import sys
    if (sys.flags.interactive != 1) or not hasattr(QtCore, 'PYQT_VERSION'):
        QtGui.QApplication.instance().exec_()
