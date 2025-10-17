@Service
@Slf4j
public class AssemblyGeneratorService {
    
    @Value("${assembly.output.dir:/tmp/assembly}")
    private String assemblyOutputDir;
    
    /**
     * Генерирует ассемблерный код с маппингом на исходный код
     */
    public AssemblyMapping generateAssembly(String sourceCode, 
                                           Language language,
                                           OptimizationLevel optimization) {
        try {
            // Создаём временную директорию
            Path tempDir = Files.createTempDirectory("asm-gen-");
            
            // Сохраняем исходный код
            Path sourceFile = saveSourceFile(tempDir, sourceCode, language);
            
            // Компилируем в ассемблер с отладочной информацией
            Path asmFile = compileToAssembly(sourceFile, language, optimization);
            
            // Парсим ассемблер и строим маппинг
            AssemblyMapping mapping = parseAssemblyWithMapping(
                sourceFile, asmFile, sourceCode);
            
            return mapping;
            
        } catch (Exception e) {
            log.error("Failed to generate assembly mapping", e);
            throw new AssemblyGenerationException(e);
        }
    }
    
    private Path compileToAssembly(Path sourceFile, 
                                   Language language,
                                   OptimizationLevel optimization) 
            throws IOException, InterruptedException {
        
        String outputFile = sourceFile.toString()
            .replaceFirst("\\.[^.]+$", ".s");
        
        List<String> command = buildCompileCommand(
            sourceFile, outputFile, language, optimization);
        
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(sourceFile.getParent().toFile());
        pb.redirectErrorStream(true);
        
        Process process = pb.start();
        String output = readOutput(process.getInputStream());
        
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new CompilationException("Compilation failed: " + output);
        }
        
        return Paths.get(outputFile);
    }
    
    private List<String> buildCompileCommand(Path sourceFile,
                                            String outputFile,
                                            Language language,
                                            OptimizationLevel optimization) {
        String filename = sourceFile.getFileName().toString();
        
        return switch (language) {
            case C, CPP -> List.of(
                language == Language.CPP ? "g++" : "gcc",
                "-S",                    // генерировать ассемблер
                "-g",                    // отладочная информация
                "-fverbose-asm",         // подробные комментарии
                "-fno-asynchronous-unwind-tables",  // чище вывод
                "-masm=intel",           // Intel синтаксис (опционально)
                optimization.getFlag(),
                filename,
                "-o", outputFile
            );
            
            case JAVA -> throw new UnsupportedOperationException(
                "Use javap for Java bytecode disassembly");
                
            default -> throw new UnsupportedOperationException(
                "Assembly generation not supported for " + language);
        };
    }
    
    private AssemblyMapping parseAssemblyWithMapping(Path sourceFile,
                                                     Path asmFile,
                                                     String sourceCode) 
            throws IOException {
        
        List<String> asmLines = Files.readAllLines(asmFile);
        String[] sourceLines = sourceCode.split("\n");
        
        // Парсим .file и .loc директивы
        Map<Integer, Integer> asmToSourceMap = new HashMap<>();
        Map<Integer, List<Integer>> sourceToAsmMap = new HashMap<>();
        
        Pattern filePattern = Pattern.compile("\\s*\\.file\\s+(\\d+)\\s+\"(.*)\"");
        Pattern locPattern = Pattern.compile("\\s*\\.loc\\s+(\\d+)\\s+(\\d+)");
        
        Map<String, Integer> fileNumbers = new HashMap<>();
        int currentSourceLine = -1;
        
        for (int asmLineNo = 0; asmLineNo < asmLines.size(); asmLineNo++) {
            String line = asmLines.get(asmLineNo);
            
            // Парсим .file директиву
            Matcher fileMatcher = filePattern.matcher(line);
            if (fileMatcher.matches()) {
                fileNumbers.put(fileMatcher.group(2), 
                    Integer.parseInt(fileMatcher.group(1)));
                continue;
            }
            
            // Парсим .loc директиву
            Matcher locMatcher = locPattern.matcher(line);
            if (locMatcher.matches()) {
                int fileNum = Integer.parseInt(locMatcher.group(1));
                int sourceLine = Integer.parseInt(locMatcher.group(2)) - 1;
                currentSourceLine = sourceLine;
                continue;
            }
            
            // Пропускаем пустые строки и директ
