public enum OptimizationLevel {
    O0("-O0", "No optimization - fastest compilation, best for debugging"),
    O1("-O1", "Basic optimization - moderate speed improvement"),
    O2("-O2", "Extensive optimization - recommended for production"),
    O3("-O3", "Aggressive optimization - maximum performance");
    
    private final String flag;
    private final String description;
    
    OptimizationLevel(String flag, String description) {
        this.flag = flag;
        this.description = description;
    }
    
    public String getFlag() {
        return flag;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static OptimizationLevel fromString(String level) {
        return switch (level.toUpperCase()) {
            case "0", "O0" -> O0;
            case "1", "O1" -> O1;
            case "2", "O2" -> O2;
            case "3", "O3" -> O3;
            default -> O2; // default
        };
    }
}
