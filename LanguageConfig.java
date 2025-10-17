import java.util.*;

public class LanguageConfig {
    
    public enum Language {
        JAVA, PYTHON, CPP, JAVASCRIPT, C
    }
    
    private static final Map<Language, StandardLibraries> STANDARD_LIBS = 
        Map.of(
            Language.JAVA, new JavaLibraries(),
            Language.PYTHON, new PythonLibraries(),
            Language.CPP, new CppLibraries(),
            Language.JAVASCRIPT, new JsLibraries(),
            Language.C, new CLibraries()
        );
    
    public static StandardLibraries getLibraries(Language lang) {
        return STANDARD_LIBS.get(lang);
    }
    
    public static Language detectLanguage(String filename) {
        if (filename.endsWith(".java")) return Language.JAVA;
        if (filename.endsWith(".py")) return Language.PYTHON;
        if (filename.endsWith(".cpp") || filename.endsWith(".cc")) 
            return Language.CPP;
        if (filename.endsWith(".js")) return Language.JAVASCRIPT;
        if (filename.endsWith(".c")) return Language.C;
        throw new IllegalArgumentException("Unsupported file type: " + filename);
    }
}

// Базовый интерфейс для библиотек
interface StandardLibraries {
    List<String> getImports();
    String getCompilerFlags();
    String wrapCode(String userCode);
    Map<String, String> getEnvironment();
}
