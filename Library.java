import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "libraries")
public class Library {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String version;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Language language;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LibraryStatus status;
    
    @Column(length = 1000)
    private String description;
    
    // Maven: groupId:artifactId:version
    // Python: package_name==version
    // C++: library-name
    @Column(nullable = false, unique = true)
    private String coordinate;
    
    @Column(name = "is_whitelisted")
    private boolean whitelisted;
    
    @Column(name = "is_standard")
    private boolean standard;  // стандартная библиотека языка
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "installed_at")
    private LocalDateTime installedAt;
    
    // Дополнительные параметры (JSON)
    @Column(columnDefinition = "TEXT")
    private String metadata;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters, setters, constructors
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public Language getLanguage() { return language; }
    public void setLanguage(Language language) { this.language = language; }
    
    public LibraryStatus getStatus() { return status; }
    public void setStatus(LibraryStatus status) { this.status = status; }
    
    public String getCoordinate() { return coordinate; }
    public void setCoordinate(String coordinate) { this.coordinate = coordinate; }
    
    public boolean isWhitelisted() { return whitelisted; }
    public void setWhitelisted(boolean whitelisted) { this.whitelisted = whitelisted; }
    
    public boolean isStandard() { return standard; }
    public void setStandard(boolean standard) { this.standard = standard; }
    
    public LocalDateTime getInstalledAt() { return installedAt; }
    public void setInstalledAt(LocalDateTime installedAt) { 
        this.installedAt = installedAt; 
    }
}

enum LibraryStatus {
    PENDING,      // ожидает установки
    INSTALLING,   // в процессе установки
    INSTALLED,    // установлена
    FAILED,       // ошибка установки
    DEPRECATED,   // устаревшая
    BLOCKED       // заблокирована (небезопасная)
}
