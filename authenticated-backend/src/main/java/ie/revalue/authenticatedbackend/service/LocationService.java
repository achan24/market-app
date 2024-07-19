package ie.revalue.authenticatedbackend.service;

import ie.revalue.authenticatedbackend.models.Location;
import ie.revalue.authenticatedbackend.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Optional;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    @Value("${locationiq.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public Location getLocation(String locationName) {
        Optional<Location> cachedLocation = locationRepository.findById(locationName);
        if (cachedLocation.isPresent()) {
            return cachedLocation.get();
        }

        Location location = fetchGeocodeFromAPI(locationName);
        if (location != null) {
            locationRepository.save(location);
        }
        return location;
    }

    public Location saveGeocode(Location location) {
        return locationRepository.save(location);
    }

    private Location fetchGeocodeFromAPI(String locationName) {
        String url = String.format("https://us1.locationiq.com/v1/search.php?key=%s&q=%s&format=json", apiKey, locationName);
        try {
            LocationIQResponse[] response = restTemplate.getForObject(url, LocationIQResponse[].class);
            if (response != null && response.length > 0) {
                Location location = new Location();
                location.setName(locationName);
                location.setLatitude(Double.parseDouble(response[0].getLat()));
                location.setLongitude(Double.parseDouble(response[0].getLon()));
                return location;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Inner class to map the LocationIQ API response
    private static class LocationIQResponse {
        private String lat;
        private String lon;

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLon() {
            return lon;
        }

        public void setLon(String lon) {
            this.lon = lon;
        }
    }
}
