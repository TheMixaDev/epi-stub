import java.util.*;

/**
 * Manages the integration and configuration of popular external libraries.
 */
public class ExternalLibraryManager {
    private final Map<String, LibraryConfig> availableLibraries;
    private final Set<String> activeLibraries;
    
    public ExternalLibraryManager() {
        this.availableLibraries = new HashMap<>();
        this.activeLibraries = new HashSet<>();
        initializePopularLibraries();
    }
    
    private void initializePopularLibraries() {
        // Add Boost library
        availableLibraries.put("boost", new LibraryConfig(
            "boost",
            "/usr/local/include/boost",
            "/usr/local/lib",
            Arrays.asList("-lboost_system", "-lboost_filesystem")
        ));
        
        // Add OpenCV library
        availableLibraries.put("opencv", new LibraryConfig(
            "opencv",
            "/usr/local/include/opencv4",
            "/usr/local/lib",
            Arrays.asList("-lopencv_core", "-lopencv_imgproc")
        ));
        
        // Add Qt library
        availableLibraries.put("qt", new LibraryConfig(
            "qt",
            "/usr/include/qt",
            "/usr/lib",
            Arrays.asList("-lQt5Core", "-lQt5Widgets")
        ));
    }
    
    public boolean enableLibrary(String libraryName) {
        if (availableLibraries.containsKey(libraryName)) {
            activeLibraries.add(libraryName);
            return true;
        }
        return false;
    }
    
    public void disableLibrary(String libraryName) {
        activeLibraries.remove(libraryName);
    }
    
    public List<String> getLibraryFlags(String libraryName) {
        LibraryConfig config = availableLibraries.get(libraryName);
        return config != null ? config.getLinkerFlags() : Collections.emptyList();
    }
    
    public Set<String> getActiveLibraries() {
        return new HashSet<>(activeLibraries);
    }
    
    public Set<String> getAvailableLibraries() {
        return availableLibraries.keySet();
    }
}
