public enum Language {
    CPP("C++", ".cpp", "cpp", "g++", "GNU C++ Compiler"),
    C("C", ".c", "c", "gcc", "GNU C Compiler"),
    RUST("Rust", ".rs", "rust", "rustc", "Rust Compiler"),
    GO("Go", ".go", "go", "go", "Go Compiler"),
    PYTHON("Python", ".py", "python", "python3", "Python Interpreter"),
    JAVA("Java", ".java", "java", "javac", "Java Compiler"),
    CSHARP("C#", ".cs", "csharp", "dotnet", ".NET Compiler"),
    HASKELL("Haskell", ".hs", "haskell", "ghc", "Glasgow Haskell Compiler"),
    PASCAL("Pascal", ".pas", "pascal", "fpc", "Free Pascal Compiler");
    
    private final String displayName;
    private final String extension;
    private final String monacoId;
    private final String compiler;
    private final String compilerName;
    
    Language(String displayName, String extension, String monacoId, 
             String compiler, String compilerName) {
        this.displayName = displayName;
        this.extension = extension;
        this.monacoId = monacoId;
        this.compiler = compiler;
        this.compilerName = compilerName;
    }
    
    // Getters
    public String getDisplayName() { return displayName; }
    public String getExtension() { return extension; }
    public String getMonacoId() { return monacoId; }
    public String getCompiler() { return compiler; }
    public String getCompilerName() { return compilerName; }
}
