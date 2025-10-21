import java.util.*;
import java.net.URL;
import java.net.HttpURLConnection;

/**
 * Service for integrating with C++ Insights tool for C++ code analysis.
 */
public class CppInsightsService {
    private final String insightsApiUrl;
    private final Map<String, String> standardOptions;
    private final Set<String> supportedStandards;
    
    public CppInsightsService(String apiUrl) {
        this.insightsApiUrl = apiUrl;
        this.standardOptions = new HashMap<>();
        this.supportedStandards = new HashSet<>();
        initializeOptions();
    }
    
    private void initializeOptions() {
        // Initialize supported C++ standards
        supportedStandards.addAll(Arrays.asList("c++11", "c++14", "c++17", "c++20"));
        
        // Initialize standard-specific options
        standardOptions.put("c++11", "-std=c++11");
        standardOptions.put("c++14", "-std=c++14");
        standardOptions.put("c++17", "-std=c++17");
        standardOptions.put("c++20", "-std=c++20");
    }
    
    public Map<String, String> prepareAnalysisRequest(String sourceCode, String standard) {
        if (!supportedStandards.contains(standard)) {
            throw new IllegalArgumentException("Unsupported C++ standard: " + standard);
        }
        
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("code", sourceCode);
        requestParams.put("options", standardOptions.get(standard));
        requestParams.put("insightsOptions", "");
        
        return requestParams;
    }
    
    public boolean isSupportedStandard(String standard) {
        return supportedStandards.contains(standard);
    }
    
    public Set<String> getSupportedStandards() {
        return new HashSet<>(supportedStandards);
    }
    
    public String getStandardOption(String standard) {
        return standardOptions.get(standard);
    }
    
    public String getApiUrl() {
        return insightsApiUrl;
    }
}
