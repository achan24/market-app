package ie.revalue.authenticatedbackend.service;

import com.google.cloud.vision.v1.*;
import com.google.cloud.spring.vision.CloudVisionTemplate;
import com.google.protobuf.ByteString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VisionService {

    @Autowired
    private CloudVisionTemplate cloudVisionTemplate;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private ImageAnnotatorClient visionClient;

    private final Map<String, String> categoryKeywords;
    private final List<String> categoryHierarchy;

    public VisionService() {
        this.categoryKeywords = initializeCategoryKeywords();
        this.categoryHierarchy = initializeCategoryHierarchy();
    }

    private Map<String, String> initializeCategoryKeywords() {
        Map<String, String> keywords = new HashMap<>();
        keywords.put("car", "Cars");
        keywords.put("motorcycle", "Motorcycles");
        keywords.put("truck", "Trucks");
        keywords.put("boat", "Boats");
        keywords.put("computer", "Computers");
        keywords.put("phone", "Phones");
        keywords.put("tv", "TVs");
        keywords.put("television", "TVs");
        keywords.put("camera", "Cameras");
        keywords.put("furniture", "Furniture");
        keywords.put("decor", "Home Decor");
        keywords.put("garden", "Garden");
        keywords.put("appliance", "Appliances");
        keywords.put("clothing", "Clothing");
        keywords.put("jewelry", "Jewelry");
        keywords.put("cosmetic", "Cosmetics");
        return keywords;
    }

    private List<String> initializeCategoryHierarchy() {
        return Arrays.asList(
                "Cars", "Motorcycles", "Trucks", "Boats",
                "Computers", "Phones", "TVs", "Cameras",
                "Furniture", "Home Decor", "Garden", "Appliances",
                "Clothing", "Jewelry", "Cosmetics",
                "Motors", "Electronics & Media", "Home & Living", "Fashion & Beauty",
                "Other"
        );
    }

    public Map<String, Object> analyzeImage(MultipartFile file) throws IOException {
        byte[] imageBytes = file.getBytes();
        ByteString imgBytes = ByteString.copyFrom(imageBytes);
        Image img = Image.newBuilder().setContent(imgBytes).build();

        Feature labelDetection = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
        Feature logoDetection = Feature.newBuilder().setType(Feature.Type.LOGO_DETECTION).build();
        Feature textDetection = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
        Feature safeSearchDetection = Feature.newBuilder().setType(Feature.Type.SAFE_SEARCH_DETECTION).build();

        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .addFeatures(labelDetection)
                .addFeatures(logoDetection)
                .addFeatures(textDetection)
                .addFeatures(safeSearchDetection)
                .setImage(img)
                .build();

        BatchAnnotateImagesResponse response = visionClient.batchAnnotateImages(Collections.singletonList(request));
        AnnotateImageResponse res = response.getResponses(0);

        Map<String, Object> result = new HashMap<>();

        // Process labels
        List<Map<String, Object>> labels = res.getLabelAnnotationsList().stream()
                .map(label -> {
                    Map<String, Object> labelMap = new HashMap<>();
                    labelMap.put("description", label.getDescription());
                    labelMap.put("score", label.getScore());
                    return labelMap;
                })
                .collect(Collectors.toList());
        result.put("labels", labels);

        // Process logos
        List<String> logos = res.getLogoAnnotationsList().stream()
                .map(EntityAnnotation::getDescription)
                .collect(Collectors.toList());
        result.put("logos", logos);

        // Process text
        List<String> texts = res.getTextAnnotationsList().stream()
                .skip(1) // Skip the first one as it's the entire text
                .map(EntityAnnotation::getDescription)
                .collect(Collectors.toList());
        result.put("detectedText", texts);

        // Process safe search
        SafeSearchAnnotation safeSearch = res.getSafeSearchAnnotation();
        result.put("safeSearch", Map.of(
                "adult", safeSearch.getAdult(),
                "violence", safeSearch.getViolence(),
                "racy", safeSearch.getRacy()
        ));

        // Suggest category based on labels and keywords
        Map<String, String> labelCategory = suggestCategoryFromLabels(labels);
        Map<String, String> keywordCategory = suggestCategoryFromKeywords(texts);
        Map<String, String> suggestedCategory = chooseMostSpecificCategory(labelCategory, keywordCategory);
        result.put("suggestedCategory", suggestedCategory);

        return result;
    }

    private Map<String, String> suggestCategoryFromLabels(List<Map<String, Object>> labels) {
        for (Map<String, Object> label : labels) {
            String description = ((String) label.get("description")).toLowerCase();
            for (Map.Entry<String, String> entry : categoryKeywords.entrySet()) {
                if (description.contains(entry.getKey())) {
                    String category = entry.getValue();
                    String heading = getHeadingForCategory(category);
                    return Map.of("heading", heading, "category", category);
                }
            }
        }
        return Map.of("heading", "Other", "category", "Other");
    }

    private Map<String, String> suggestCategoryFromKeywords(List<String> texts) {
        for (String text : texts) {
            String lowerText = text.toLowerCase();
            for (Map.Entry<String, String> entry : categoryKeywords.entrySet()) {
                if (lowerText.contains(entry.getKey())) {
                    String category = entry.getValue();
                    String heading = getHeadingForCategory(category);
                    return Map.of("heading", heading, "category", category);
                }
            }
        }
        return Map.of("heading", "Other", "category", "Other");
    }

    private Map<String, String> chooseMostSpecificCategory(Map<String, String> category1, Map<String, String> category2) {
        String cat1 = category1.get("category");
        String cat2 = category2.get("category");

        if (cat1.equals("Other") && !cat2.equals("Other")) {
            return category2;
        } else if (!cat1.equals("Other") && cat2.equals("Other")) {
            return category1;
        } else {
            int index1 = categoryHierarchy.indexOf(cat1);
            int index2 = categoryHierarchy.indexOf(cat2);
            return index1 <= index2 ? category1 : category2;
        }
    }

    private String getHeadingForCategory(String category) {
        if (Arrays.asList("Cars", "Motorcycles", "Trucks", "Boats").contains(category)) {
            return "Motors";
        } else if (Arrays.asList("Computers", "Phones", "TVs", "Cameras").contains(category)) {
            return "Electronics & Media";
        } else if (Arrays.asList("Furniture", "Home Decor", "Garden", "Appliances").contains(category)) {
            return "Home & Living";
        } else if (Arrays.asList("Clothing", "Jewelry", "Cosmetics").contains(category)) {
            return "Fashion & Beauty";
        } else {
            return "Other";
        }
    }
}