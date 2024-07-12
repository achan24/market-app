import React, { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { useAuth, AuthContext } from './AuthContext';

const ImageAnalysis = () => {
  const [image, setImage] = useState(null);
  const [analysisResult, setAnalysisResult] = useState(null);
  const [error, setError] = useState('');
  const { user, token } = useAuth()

  const handleImageUpload = (event) => {
    const file = event.target.files[0];
    setImage(file);
  };

  const analyzeImage = async () => {
    if (!image) {
      setError('Please upload an image first.');
      return;
    }

    setError('');
    setAnalysisResult(null);

    try {
      const formData = new FormData();
      formData.append('image', image);

      const response = await fetch('http://localhost:8000/vision/analyse', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`
        },
        body: formData,
      });

      if (!response.ok) {
        throw new Error('Failed to analyze image');
      }

      const data = await response.json();
      setAnalysisResult(data);

    } catch (error) {
      setError('An error occurred during image analysis. Please try again.');
      console.error('Error:', error);
    }
  };

  return (
    <Card className="w-full max-w-md mx-auto">
      <CardHeader>
        <CardTitle>Image Analysis</CardTitle>
      </CardHeader>
      <CardContent>
        <Input type="file" onChange={handleImageUpload} accept="image/*" className="mb-4" />
        <Button onClick={analyzeImage} className="w-full mb-4">Analyze Image</Button>
        
        {error && (
          <Alert variant="destructive" className="mb-4">
            <AlertDescription>{error}</AlertDescription>
          </Alert>
        )}
        
        {analysisResult && (
          <>
            {analysisResult.labels && analysisResult.labels.length > 0 && (
              <div className="mb-4">
                <h3 className="font-bold">Labels:</h3>
                <ul className="list-disc pl-5">
                  {analysisResult.labels.map((label, index) => (
                    <li key={index}>{label.description} (Score: {label.score.toFixed(2)})</li>
                  ))}
                </ul>
              </div>
            )}
            
            {analysisResult.logos && analysisResult.logos.length > 0 && (
              <div className="mb-4">
                <h3 className="font-bold">Logos:</h3>
                <ul className="list-disc pl-5">
                  {analysisResult.logos.map((logo, index) => (
                    <li key={index}>{logo}</li>
                  ))}
                </ul>
              </div>
            )}
            
            {analysisResult.detectedText && analysisResult.detectedText.length > 0 && (
              <div className="mb-4">
                <h3 className="font-bold">Detected Text:</h3>
                <p>{analysisResult.detectedText.join(', ')}</p>
              </div>
            )}
            
            {analysisResult.safeSearch && (
              <div className="mb-4">
                <h3 className="font-bold">Safe Search:</h3>
                <ul className="list-disc pl-5">
                  <li>Adult: {analysisResult.safeSearch.adult}</li>
                  <li>Violence: {analysisResult.safeSearch.violence}</li>
                  <li>Racy: {analysisResult.safeSearch.racy}</li>
                </ul>
              </div>
            )}
          </>
        )}
      </CardContent>
    </Card>
  );
};

export default ImageAnalysis;