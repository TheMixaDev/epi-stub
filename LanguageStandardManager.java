import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/**
 * Manages programming language standards and their compatibility with different compilers.
 * Currently supports C++14, C++17, and C++20 standards.
 */
public class LanguageStandardManager {
    private final Map<String, Set<String>> compilerStandardSupport;
    private final Map<String, String> standardFlags;
    
    public LanguageStandardManager() {
        this.compilerStandardSupport = new HashMap<>();
        this.standardFlags = new HashMap<>();
        initializeStandardSupport();
    }
    
    private void initializeStandardSupport() {
        // Initialize C++ standards flags
        standardFlags.put("C++14", "-std=c++14");
        standardFlags.put("C++17", "-std=c++17");
        standardFlags.put("C++20", "-std=c++20");
        
        // Set up GCC support
        Set<String> gccStandards = new HashSet<>();
        gccStandards.addAll(Set.of("C++14", "C++17", "C++20"));
        compilerStandardSupport.put("gcc", gccStandards);
        
        // Set up Clang support
        Set<String> clangStandards = new HashSet<>();
        clangStandards.addAll(Set.of("C++14", "C++17", "C++20"));
        compilerStandardSupport.put("clang", clangStandards);
        
        // Set up MSVC support
        Set<String> msvcStandards = new HashSet<>();
        msvcStandards.addAll(Set.of("C++14", "C++17", "C++20"));
        compilerStandardSupport.put("msvc", msvcStandards);
    }
    
    public boolean isStandardSupported(String compiler, String standard) {
        Set<String> supported = compilerStandardSupport.get(compiler);
        return supported != null && supported.contains(standard);
    }
    
    public String getStandardFlag(String standard) {
        return standardFlags.getOrDefault(standard, "");
    }
    
    public Set<String> getSupportedStandards(String compiler) {
        return new HashSet<>(compilerStandardSupport.getOrDefault(compiler, new HashSet<>()));
    }
}
