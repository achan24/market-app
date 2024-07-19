package ie.revalue.authenticatedbackend.controllers;

import ie.revalue.authenticatedbackend.models.Location;
import ie.revalue.authenticatedbackend.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/location")
@CrossOrigin("*")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @GetMapping
    public ResponseEntity<Location> getLocation(@RequestParam String locationName) {
        Location location = locationService.getLocation(locationName);
        if (location != null) {
            return ResponseEntity.ok(location);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Location> saveGeocode(@RequestBody Location location) {
        Location savedLocation = locationService.saveGeocode(location);
        return ResponseEntity.ok(savedLocation);
    }
}