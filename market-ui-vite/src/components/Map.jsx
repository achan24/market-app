import React, { useState } from 'react';
import { APIProvider, Map, AdvancedMarker, Pin, Infowindow } from '@vis.gl/react-google-maps';

export default function Map() {
  const position = { lat: 53.54, lng: 10 };
  const apiKey = process.env.REACT_APP_GOOGLE_MAPS_API_KEY; // Ensure this is set in your environment

  return (
    <APIProvider apiKey={apiKey}>
      <div style={{ height: '100vh', width: '100%' }}>
        <Map zoom={9} center={position}>
          {/* You can add more components like AdvancedMarker, Pin, and Infowindow here */}
        </Map>
      </div>
    </APIProvider>
  );
}