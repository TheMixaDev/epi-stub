@Data
@Configuration
@ConfigurationProperties(prefix = "compiler")
public class CompilerConfig {
    private int timeout;
    private int maxExecutions;
    private String tempDirectory;
    private List<CompilerDefinition> compilers;
    
    @Data
    public static class CompilerDefinition {
        private String name;
        private String version;
        private String path;
        private boolean enabled;
        private List<String> options;
    }
}
