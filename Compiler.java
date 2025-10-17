public interface Compiler {
    String getName();
    String getVersion();
    CompilationResult compile(String sourceCode, CompilationOptions options);
    List<String> getSupportedLanguages();
    boolean supports(String language, String version);
}

@Data
@Builder
public class CompilationResult {
    private boolean success;
    private String output;
    private String errorMessage;
    private byte[] compiledCode;
    private long compilationTime;
}

@Data
public class CompilationOptions {
    private String language;
    private String targetVersion;
    private List<String> compilerFlags;
    private int timeoutSeconds;
}
