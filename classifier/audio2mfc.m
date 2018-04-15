    close all;clc;clear;
% Define variables
    Tw = 25;                % analysis frame duration (ms)
    Ts = 10;                % analysis frame shift (ms)
    alpha = 0.97;           % preemphasis coefficient
    M = 20;                 % number of filterbank channels 
    C = 12;                 % number of cepstral coefficients
    L = 22;                 % cepstral sine lifter parameter
    LF = 300;               % lower frequency limit (Hz)
    HF = 3700; % upper frequency limit (Hz)
    MFCCs={};
    Y={};
    for i=1:20
    wav_file = sprintf('%s%d%s','H:\IOT\project\Home_01\Home_01\Videos\audiofiles\video(',i,').wav');  % input audio filename
    [ speech, fs ] = audioread( wav_file );

    % Feature extraction (feature vectors as columns)
    [ A, FBEs, frames ] = ...
                    mfcc( speech, fs, Tw, Ts, alpha, @hamming, [LF HF], M, C+1, L );
    MFCC{i}=reshape(A.',1,[]);
    Y{i}='1';
    end
    %add trailing zeros to MFCC
    
    mf = MFCC;
for i=1:20
mf{i}=[mf{i}, zeros(1,1820-length(mf{i}))];
end

SVMModel = fitcsvm(cell2mat(mf(:)),cell2mat(Y(:)),'KernelFunction','rbf','ClassNames',['1']);
    
    for i=21:30
    wav_file = sprintf('%s%d%s','H:\IOT\project\Home_01\Home_01\Videos\audiofiles\video(',i,').wav');  % input audio filename
    [ speech, fs ] = audioread( wav_file );

    % Feature extraction (feature vectors as columns)
    [ A, FBEs, frames ] = ...
                    mfcc( speech, fs, Tw, Ts, alpha, @hamming, [LF HF], M, C+1, L );
    MFCC{i}=reshape(A.',1,[]);
    Y{i}='1';
    end
    %add trailing zeros to MFCC
%      %wav_file = sprintf('%s%d%s','H:\IOT\project\Home_01\Home_01\Videos\audiofiles\video(',i,').wav');  % input audio filename
%      wav_file = 'video(30)_nofall.wav';
%     [ speech, fs ] = audioread( wav_file );
% 
%     % Feature extraction (feature vectors as columns)
%     [ A, FBEs, frames ] = ...
%                     mfcc( speech, fs, Tw, Ts, alpha, @hamming, [LF HF], M, C+1, L );
%     A=reshape(A.',1,[]);
%     %Y{i}='1';
   
    
    
for i=21:30
   mf{i}=[MFCC{i}, zeros(1,1820-length(MFCC{i}))];
%    test = [A, zeros(1,1820-length(A))];
% test{i-20}=[mf{i}, zeros(1,1820-length(mf{i}))];
[label,score]=predict(SVMModel,mf{i})   
end
