num=30;
sframe=123;
eframe=138;
str1='H:\IOT\project\Home_01\Home_01\Videos\video (';
str2=').avi';
orig=sprintf('%s%d%s',str1,num,str2);
str3='H:\IOT\project\Home_01\Home_01\Videos\crop_video (';
dest = sprintf('%s%d%s',str3,num,str2);
str4='video(';
str5=').WAV';
audiodest=sprintf('%s%d%s',str4,num,str5);
videoFReader = vision.VideoFileReader(orig)
vidObj = VideoReader(orig);
[data,Fs] = audioread(orig);

videoFWriter = vision.VideoFileWriter(dest,'FrameRate',videoFReader.info.VideoFrameRate,'AudioInputPort',true);

nFrames   = ceil(vidObj.Duration * vidObj.FrameRate);


val = size(data,1)/nFrames;
count=0;

while ~isDone(videoFReader)
    count = count+1;
        videoFrame = step(videoFReader);
    if(count>=sframe && count<=eframe )
    k=count;
  
   step(videoFWriter,videoFrame,data(val*(k-1)+1:val*k,:)); % it is 2 channel that is why I have put (:)

    end
end
release(videoFWriter);
release(videoFReader);
[input_file, Fs] = audioread(dest);
audiowrite(audiodest, input_file, Fs);
