package ie.revalue.authenticatedbackend.service;

import com.google.cloud.vision.v1.*;
import com.google.cloud.spring.vision.CloudVisionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VisionService {

    @Autowired
    private CloudVisionTemplate cloudVisionTemplate;

    @Autowired
    private ResourceLoader resourceLoader;

    public Map<String, Object> analyzeImage(MultipartFile file) throws IOException {
        ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };

        AnnotateImageResponse response = cloudVisionTemplate.analyzeImage(
                resource,
                Feature.Type.LABEL_DETECTION,
                Feature.Type.LOGO_DETECTION,
                Feature.Type.TEXT_DETECTION,
                Feature.Type.SAFE_SEARCH_DETECTION
        );

        Map<String, Object> analysis = new HashMap<>();

        // Label Detection
        List<Map<String, Object>> labels = response.getLabelAnnotationsList().stream()
                .map(label -> {
                    Map<String, Object> labelMap = new HashMap<>();
                    labelMap.put("description", label.getDescription());
                    labelMap.put("score", label.getScore());
                    return labelMap;
                })
                .collect(Collectors.toList());
        analysis.put("labels", labels);

        // Logo Detection
        List<String> logos = response.getLogoAnnotationsList().stream()
                .map(EntityAnnotation::getDescription)
                .collect(Collectors.toList());
        analysis.put("logos", logos);

        // Text Detection
        List<String> textDetections = response.getTextAnnotationsList().stream()
                .skip(1) // Skip the first one as it's the entire text
                .map(EntityAnnotation::getDescription)
                .collect(Collectors.toList());
        analysis.put("detectedText", textDetections);

        // Safe Search
        SafeSearchAnnotation safeSearch = response.getSafeSearchAnnotation();
        Map<String, Object> safeSearchMap = new HashMap<>();
        safeSearchMap.put("adult", safeSearch.getAdult().toString());
        safeSearchMap.put("violence", safeSearch.getViolence().toString());
        safeSearchMap.put("racy", safeSearch.getRacy().toString());
        analysis.put("safeSearch", safeSearchMap);

        return analysis;
    }
}