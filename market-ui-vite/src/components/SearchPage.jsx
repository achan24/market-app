import React, { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import CardListing from './CardListing';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import { Icon } from 'leaflet';
import MarkerClusterGroup from 'react-leaflet-cluster';
import 'leaflet/dist/leaflet.css';
import 'leaflet.markercluster/dist/MarkerCluster.css';
import 'leaflet.markercluster/dist/MarkerCluster.Default.css';
import categories from './Categories';

const useQuery = () => {
  return new URLSearchParams(useLocation().search);
};

const fetchGeocode = async (locationName) => {
  const response = await fetch(`http://localhost:8000/api/v1/location?locationName=${encodeURIComponent(locationName)}`);
  if (!response.ok) {
    throw new Error('Error fetching geocode');
  }
  return response.json();
};

const saveGeocode = async (location) => {
  const response = await fetch(`http://localhost:8000/api/v1/location`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(location),
  });
  if (!response.ok) {
    throw new Error('Error saving geocode');
  }
  return response.json();
};

const fetchGeocodeFromAPI = async (locationName) => {
  const apiKey = import.meta.env.VITE_LOCATIONIQ_API_KEY; // Use Vite's import.meta.env
  if (!apiKey) {
    console.error('LocationIQ API key is not set in the environment variables');
    return null;
  }
  console.log(`Fetching geocode for location: ${locationName}`);
  const response = await fetch(`https://us1.locationiq.com/v1/search.php?key=${apiKey}&q=${encodeURIComponent(locationName)}&format=json`);
  const data = await response.json();
  console.log('API response:', data);
  if (data && data.length > 0) {
    const geocode = {
      lat: data[0].lat,
      lon: data[0].lon
    };
    console.log('Geocode found:', geocode);
    return geocode;
  }
  console.log('No geocode found for location:', locationName);
  return null;
};

const SearchPage = () => {
  const [products, setProducts] = useState([]);
  const [markers, setMarkers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filters, setFilters] = useState({ category: '', location: '', price: '' });
  const [showMap, setShowMap] = useState(false);
  const [activeMarker, setActiveMarker] = useState(null);
  const query = useQuery().get('query') || '';
  const categoryParam = useQuery().get('category') || '';

  useEffect(() => {
    const delay = setTimeout(() => {
      setFilters((prevFilters) => ({ ...prevFilters, category: categoryParam }));
    }, 500); // 0.5 second delay

    return () => clearTimeout(delay);
  }, [categoryParam]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFilters((prevFilters) => ({ ...prevFilters, [name]: value }));
  };

  const fetchData = async () => {
    try {
      let queryStr = `query=${query}`;
      if (filters.category) queryStr += `&category=${filters.category}`;
      if (filters.location) queryStr += `&location=${filters.location}`;
      if (filters.price) queryStr += `&price=${filters.price}`;

      const response = await fetch(`http://localhost:8000/api/v1/listings?${queryStr}`);
      if (!response.ok) {
        throw new Error('Error fetching listings');
      }
      const data = await response.json();

      // Fetch geocode data for each listing with delay
      const listingsWithGeocode = await Promise.all(data.map(async (listing, index) => {
        let geocode;
        try {
          geocode = await fetchGeocode(listing.location);
        } catch (error) {
          console.log('Geocode not found, fetching from API');
          geocode = await fetchGeocodeFromAPI(listing.location);
          if (geocode) {
            await saveGeocode({ name: listing.location, latitude: geocode.lat, longitude: geocode.lon });
          }
        }
        return { ...listing, geocode };
      }));

      console.log('Listings with geocode:', listingsWithGeocode);

      // Create markers array
      const markersData = listingsWithGeocode
        .filter(listing => listing.geocode)
        .map(listing => ({
          geocode: [listing.geocode.latitude, listing.geocode.longitude],
          listing // Include the entire listing data for the popup
        }));

      console.log('Markers Data:', markersData);

      setProducts(listingsWithGeocode);
      setMarkers(markersData);
    } catch (err) {
      console.log('Error fetching listings', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    const getProducts = async () => {
      await fetchData();
    };

    getProducts();
  }, [filters, query]);

  const customIcon = new Icon({
    iconUrl: "https://i.postimg.cc/zG3Rbmmn/map-pointer.png",
    iconSize: [38, 38]
  });

  const handleMarkerMouseOver = (markerId) => {
    setActiveMarker(markerId);
  };

  const handleMarkerMouseOut = () => {
    setTimeout(() => {
      setActiveMarker(null);
    }, 1000); // Delay before hiding the popup
  };

  const CustomMarker = ({ position, listing }) => {
    const markerId = listing.id; // Use a unique identifier for the marker

    return (
      <Marker
        position={position}
        icon={customIcon}
        eventHandlers={{
          mouseover: () => handleMarkerMouseOver(markerId),
          mouseout: handleMarkerMouseOut
        }}
      >
        {activeMarker === markerId && (
          <Popup>
            <CardListing product={listing} />
          </Popup>
        )}
      </Marker>
    );
  };

  return (
    <div className="font-sans text-center container mx-auto flex mt-8 max-w-screen-xl">
      <div className="w-1/4 p-4 bg-gray-100">
        <h3 className="text-xl font-bold mb-4">Filters</h3>
        <div className="mb-4">
          <label className="block text-sm font-medium mb-1">Category</label>
          <select name="category" value={filters.category} onChange={handleChange} className="w-full p-2 border border-gray-300 rounded">
            <option value="">Select a category</option>
            {Object.keys(categories).map((group, index) => (
              <optgroup key={index} label={group}>
                {categories[group].map((option, idx) => (
                  <option key={idx} value={option}>{option}</option>
                ))}
              </optgroup>
            ))}
          </select>
        </div>
        <div className="mb-4">
          <label className="block text-sm font-medium mb-1">Location</label>
          <input name="location" value={filters.location} onChange={handleChange} type="text" className="w-full p-2 border border-gray-300 rounded" placeholder="Enter location" />
        </div>
        <div className="mb-4">
          <label className="block text-sm font-medium mb-1">Price</label>
          <select name="price" value={filters.price} onChange={handleChange} className="w-full p-2 border border-gray-300 rounded">
            <option value="">All Prices</option>
            <option value="1-50">€1 to €50</option>
            <option value="50-100">€50 to €100</option>
            <option value="100-250">€100 to €250</option>
            <option value="250-500">€250 to €500</option>
            <option value="500+">€500 and above</option>
          </select>
        </div>
        <button
          className="w-full p-2 bg-blue-500 text-white rounded mt-4"
          onClick={() => setShowMap(!showMap)}
        >
          {showMap ? 'Show Results' : 'Show Map'}
        </button>
      </div>
      <div className="w-3/4 pl-4">
        {loading ? (
          <div>Loading...</div>
        ) : showMap ? (
          <MapContainer className="h-screen" center={[53.3498, -6.2603]} zoom={12}>
            <TileLayer
              attribution='openstreetmap contributors'
              url="https://tile.openstreetmap.org/{z}/{x}/{y}.png"
            />
            <MarkerClusterGroup chunkedLoading>
              {markers.map((marker, key) => (
                <CustomMarker key={key} position={marker.geocode} listing={marker.listing} />
              ))}
            </MarkerClusterGroup>
          </MapContainer>
        ) : (
          <div>
            <h2 className="text-2xl font-bold mb-4">Search Results</h2>
            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {products.map((product, index) => (
                <CardListing key={index} product={product} className="transform transition-transform duration-200 hover:scale-105 hover:shadow-lg hover:border-gray-400"/>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default SearchPage;
