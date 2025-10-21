import java.util.*;

/**
 * Controls and manages versions of external libraries.
 */
public class LibraryVersionController {
    private final Map<String, Map<String, LibraryVersion>> libraryVersions;
    private final Map<String, String> activeVersions;
    
    public LibraryVersionController() {
        this.libraryVersions = new HashMap<>();
        this.activeVersions = new HashMap<>();
        initializeLibraryVersions();
    }
    
    private void initializeLibraryVersions() {
        // Initialize Boost versions
        Map<String, LibraryVersion> boostVersions = new HashMap<>();
        boostVersions.put("1.81.0", new LibraryVersion("1.81.0", "/usr/local/boost/1.81.0"));
        boostVersions.put("1.82.0", new LibraryVersion("1.82.0", "/usr/local/boost/1.82.0"));
        libraryVersions.put("boost", boostVersions);
        
        // Initialize OpenCV versions
        Map<String, LibraryVersion> opencvVersions = new HashMap<>();
        opencvVersions.put("4.7.0", new LibraryVersion("4.7.0", "/usr/local/opencv/4.7.0"));
        opencvVersions.put("4.8.0", new LibraryVersion("4.8.0", "/usr/local/opencv/4.8.0"));
        libraryVersions.put("opencv", opencvVersions);
        
        // Set default versions
        setLibraryVersion("boost", "1.82.0");
        setLibraryVersion("opencv", "4.8.0");
    }
    
    public boolean setLibraryVersion(String library, String version) {
        Map<String, LibraryVersion> versions = libraryVersions.get(library);
        if (versions != null && versions.containsKey(version)) {
            activeVersions.put(library, version);
            return true;
        }
        return false;
    }
    
    public String getActiveVersion(String library) {
        return activeVersions.get(library);
    }
    
    public Set<String> getAvailableVersions(String library) {
        Map<String, LibraryVersion> versions = libraryVersions.get(library);
        return versions != null ? versions.keySet() : Collections.emptySet();
    }
    
    public String getLibraryPath(String library) {
        String version = activeVersions.get(library);
        if (version != null) {
            Map<String, LibraryVersion> versions = libraryVersions.get(library);
            if (versions != null) {
                LibraryVersion libVersion = versions.get(version);
                return libVersion != null ? libVersion.getPath() : null;
            }
        }
        return null;
    }
}
