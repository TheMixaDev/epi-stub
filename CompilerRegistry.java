import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Registry for managing multiple compiler implementations including GCC, Clang, MSVC,
 * and specialized compilers for ARM and RISC-V architectures.
 */
public class CompilerRegistry {
    private final Map<String, CompilerConfig> compilers;
    
    public CompilerRegistry() {
        this.compilers = new HashMap<>();
        initializeDefaultCompilers();
    }
    
    private void initializeDefaultCompilers() {
        // Register GCC compiler configurations
        registerCompiler("gcc-x86", new CompilerConfig("gcc", "/usr/bin/gcc", "x86"));
        registerCompiler("gcc-x64", new CompilerConfig("gcc", "/usr/bin/gcc", "x64"));
        
        // Register Clang compiler configurations
        registerCompiler("clang-x86", new CompilerConfig("clang", "/usr/bin/clang", "x86"));
        registerCompiler("clang-x64", new CompilerConfig("clang", "/usr/bin/clang", "x64"));
        
        // Register MSVC compiler configuration
        registerCompiler("msvc", new CompilerConfig("msvc", "cl.exe", "x64"));
        
        // Register ARM compiler configuration
        registerCompiler("arm-gcc", new CompilerConfig("arm-none-eabi-gcc", 
            "/usr/local/bin/arm-none-eabi-gcc", "arm"));
            
        // Register RISC-V compiler configuration
        registerCompiler("riscv-gcc", new CompilerConfig("riscv64-unknown-elf-gcc",
            "/usr/local/bin/riscv64-unknown-elf-gcc", "riscv"));
    }
    
    public void registerCompiler(String id, CompilerConfig config) {
        compilers.put(id, config);
    }
    
    public Optional<CompilerConfig> getCompiler(String id) {
        return Optional.ofNullable(compilers.get(id));
    }
    
    public boolean removeCompiler(String id) {
        return compilers.remove(id) != null;
    }
    
    public Map<String, CompilerConfig> getAllCompilers() {
        return new HashMap<>(compilers);
    }
}
