package ie.revalue.authenticatedbackend.controllers;



import ie.revalue.authenticatedbackend.service.VisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/vision")
@CrossOrigin("*")
public class VisionController {


    @Autowired
    private VisionService visionService;

    @PostMapping("/analyse")
    public Map<String, Object> analyzeImage(@RequestParam("image") MultipartFile image) throws IOException {
        return visionService.analyzeImage(image);
    }
}

