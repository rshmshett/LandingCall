mf = MFCC;
for i=1:30
mf{i}=[mf{i}, zeros(1,1876-length(mf{i}))];
end
    
layers = [ ...
    sequenceInputLayer(1820)
    convolution2dLayer([5 5],10)
    reluLayer
    fullyConnectedLayer(10)
    softmaxLayer
    classificationLayer]