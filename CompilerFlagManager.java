import java.util.*;

/**
 * Manages custom compilation flags and architecture-specific settings for different compilers.
 */
public class CompilerFlagManager {
    private final Map<String, Set<String>> defaultFlags;
    private final Map<String, Set<String>> customFlags;
    private final Map<String, Map<String, String>> archSettings;
    
    public CompilerFlagManager() {
        this.defaultFlags = new HashMap<>();
        this.customFlags = new HashMap<>();
        this.archSettings = new HashMap<>();
        initializeDefaultFlags();
    }
    
    private void initializeDefaultFlags() {
        // Initialize default optimization flags
        defaultFlags.put("gcc", new HashSet<>(Arrays.asList("-O2", "-Wall", "-Wextra")));
        defaultFlags.put("clang", new HashSet<>(Arrays.asList("-O2", "-Wall", "-Wextra")));
        defaultFlags.put("msvc", new HashSet<>(Arrays.asList("/O2", "/W4")));
        
        // Initialize architecture settings
        Map<String, String> x86Settings = new HashMap<>();
        x86Settings.put("arch", "-m32");
        x86Settings.put("sse", "-msse4.2");
        
        Map<String, String> x64Settings = new HashMap<>();
        x64Settings.put("arch", "-m64");
        x64Settings.put("avx", "-mavx2");
        
        archSettings.put("x86", x86Settings);
        archSettings.put("x64", x64Settings);
    }
    
    public void addCustomFlag(String compiler, String flag) {
        customFlags.computeIfAbsent(compiler, k -> new HashSet<>()).add(flag);
    }
    
    public void removeCustomFlag(String compiler, String flag) {
        Set<String> flags = customFlags.get(compiler);
        if (flags != null) {
            flags.remove(flag);
        }
    }
    
    public Set<String> getAllFlags(String compiler, String architecture) {
        Set<String> allFlags = new HashSet<>();
        allFlags.addAll(defaultFlags.getOrDefault(compiler, new HashSet<>()));
        allFlags.addAll(customFlags.getOrDefault(compiler, new HashSet<>()));
        
        Map<String, String> archSpecific = archSettings.get(architecture);
        if (archSpecific != null) {
            allFlags.addAll(archSpecific.values());
        }
        
        return allFlags;
    }
}
