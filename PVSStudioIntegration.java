import java.util.*;
import java.nio.file.Path;

/**
 * Integrates PVS-Studio static analyzer for GCC and Clang on x86 and x64 platforms.
 */
public class PVSStudioIntegration {
    private final Map<String, String> compilerPaths;
    private final Set<String> supportedPlatforms;
    private final String pvsStudioPath;
    
    public PVSStudioIntegration(String pvsStudioPath) {
        this.pvsStudioPath = pvsStudioPath;
        this.compilerPaths = new HashMap<>();
        this.supportedPlatforms = new HashSet<>(Arrays.asList("x86", "x64"));
        initializeCompilers();
    }
    
    private void initializeCompilers() {
        compilerPaths.put("gcc", "/usr/bin/gcc");
        compilerPaths.put("clang", "/usr/bin/clang");
    }
    
    public List<String> generateAnalysisCommand(String compiler, String platform, Path sourceFile) {
        if (!isSupported(compiler, platform)) {
            throw new IllegalArgumentException("Unsupported compiler or platform");
        }
        
        List<String> command = new ArrayList<>();
        command.add(pvsStudioPath);
        command.add("--source-file");
        command.add(sourceFile.toString());
        command.add("--compiler");
        command.add(compilerPaths.get(compiler));
        command.add("--platform");
        command.add(platform);
        
        return command;
    }
    
    public boolean isSupported(String compiler, String platform) {
        return compilerPaths.containsKey(compiler) && supportedPlatforms.contains(platform);
    }
    
    public void setCompilerPath(String compiler, String path) {
        if (compilerPaths.containsKey(compiler)) {
            compilerPaths.put(compiler, path);
        }
    }
    
    public Set<String> getSupportedCompilers() {
        return new HashSet<>(compilerPaths.keySet());
    }
    
    public Set<String> getSupportedPlatforms() {
        return new HashSet<>(supportedPlatforms);
    }
    
    public String getCompilerPath(String compiler) {
        return compilerPaths.get(compiler);
    }
}
