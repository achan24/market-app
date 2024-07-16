package ie.revalue.authenticatedbackend.controllers;



import ie.revalue.authenticatedbackend.service.VisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/vision")
@CrossOrigin("*")
public class VisionController {


    @Autowired
    private VisionService visionService;

    @PostMapping("/analyse")
    public ResponseEntity<?> analyzeImage(@RequestParam("image") MultipartFile file) {
        try {
            Map<String, Object> analysisResult = visionService.analyzeImage(file);
            return ResponseEntity.ok(analysisResult);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error processing image: " + e.getMessage());
        }
    }
}

