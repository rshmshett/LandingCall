#!/usr/bin/env python

from python_speech_features import mfcc
from python_speech_features import delta
from python_speech_features import logfbank
from sklearn import svm
import scipy.io.wavfile as wav
import numpy as np
import matplotlib
from matplotlib.mlab import PCA
from sklearn import decomposition
song_list=[]
tl=[]
testx=[]
testy=[]
for i in range(1,31):
	if(i==18 or i==23 or i==19 or i == 21): #trimmed audio is too short
		(rate,sig) = wav.read("../Audiodataset/video("+str(i)+").wav")
	else:
		(rate,sig) = wav.read("H:/IOT/project/python test/videotrimmed("+str(i)+").wav")
	mfcc_feat = mfcc(sig,rate)
	mean = np.mean(mfcc_feat,axis=0)
	# and standard deviation of MFCC vectors 
	std = np.std(mfcc_feat,axis=0)
	# use [mean, std] as the feature vector
	fvec = np.concatenate((mean, std)).reshape(-1,1)#.tolist()
	mfcc_arr=np.array(fvec)
	print str(i)+"size:"+str(mfcc_arr.shape)
	d_mfcc_feat = delta(mfcc_feat, 2)
	fbank_feat = logfbank(sig,rate)
	if(i>26):
		testx.append(mfcc_arr)
		testy.append(1)
	else:
		song_list.append(mfcc_arr)
		tl.append(1)	
for i in range(1,6):
	(rate,sig) = wav.read("H:/IOT/project/python test/nofall"+str(i)+".wav")
	mfcc_feat = mfcc(sig,rate)
	mean = np.mean(mfcc_feat,axis=0)
	# and standard deviation of MFCC vectors 
	std = np.std(mfcc_feat,axis=0)
	# use [mean, std] as the feature vector
	fvec = np.concatenate((mean, std)).reshape(-1,1)#.tolist()
	mfcc_arr=np.array(fvec)
	print str(i)+"size:"+str(mfcc_arr.shape)
	d_mfcc_feat = delta(mfcc_feat, 2)
	fbank_feat = logfbank(sig,rate)
	if(i==5):
		testx.append(mfcc_arr)
		testy.append(0)
	else:
		song_list.append(mfcc_arr)
		tl.append(0)
		
clf= svm.LinearSVC()
a=np.array(song_list)
b=np.array(tl)
nsamples, nx, ny = a.shape
a = a.reshape((nsamples,nx*ny))
print len(a),len(b) 
clf.fit(a,b)
print "done fitting"
testx=np.array(testx)
nsamples, nx, ny = testx.shape
testx = testx.reshape((nsamples,nx*ny))

for i in range(1,5):
	print clf.predict(testx[i])	
#song_matrix = np.vstack(song_list)
#test = matplotlib.mlab.PCA(song_matrix.T)

#pca = decomposition.PCA(n_components=2)
#pca.fit(song_matrix.T)
#song_matrix = pca.transform(song_matrix.T)
#song_matrix_pca = pca.fit_transform(song_matrix.T)
#print pca.n_components_
#clf = svm.SVC()
#clf.fit(song_list, tl)
#print clf 


	
	

