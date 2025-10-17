@Component
@Slf4j
public class DockerSandbox implements CodeSandbox {
    
    @Value("${docker.api.version:1.43}")
    private String dockerApiVersion;
    
    @Value("${docker.network.enabled:false}")
    private boolean networkEnabled;
    
    @Value("${docker.image.default:alpine:latest}")
    private String defaultImage;
    
    private final DockerClient dockerClient;
    
    public DockerSandbox() {
        this.dockerClient = DockerClientBuilder
            .getInstance()
            .build();
    }
    
    public SandboxResult execute(ExecutionRequest request) {
        String containerId = null;
        try {
            // Создаём контейнер с ограничениями безопасности
            containerId = createSecureContainer(request);
            
            // Копируем исполняемый файл в контейнер
            copyFilesToContainer(containerId, request.getExecutable());
            
            // Запускаем контейнер
            dockerClient.startContainerCmd(containerId).exec();
            
            // Выполняем команду с таймаутом
            ExecCreateCmdResponse execCreateCmd = dockerClient
                .execCreateCmd(containerId)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withCmd(request.getCommand())
                .exec();
            
            // Захватываем вывод
            ResultCallback<Frame> resultCallback = new ResultCallback.Adapter<>() {
                private final StringBuilder stdout = new StringBuilder();
                private final StringBuilder stderr = new StringBuilder();
                
                @Override
                public void onNext(Frame frame) {
                    switch (frame.getStreamType()) {
                        case STDOUT -> stdout.append(new String(frame.getPayload()));
                        case STDERR -> stderr.append(new String(frame.getPayload()));
                    }
                }
            };
            
            dockerClient.execStartCmd(execCreateCmd.getId())
                .exec(resultCallback)
                .awaitCompletion(request.getTimeoutSeconds(), TimeUnit.SECONDS);
            
            // Проверяем код выхода
            InspectExecResponse execResponse = dockerClient
                .inspectExecCmd(execCreateCmd.getId())
                .exec();
            
            int exitCode = execResponse.getExitCodeLong() != null ? 
                execResponse.getExitCodeLong().intValue() : -1;
            
            return new SandboxResult(
                exitCode == 0,
                resultCallback.stdout.toString(),
                resultCallback.stderr.toString(),
                exitCode
            );
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return SandboxResult.timeout();
        } catch (Exception e) {
            log.error("Docker execution failed", e);
            return SandboxResult.error(e.getMessage());
        } finally {
            // Всегда удаляем контейнер
            cleanupContainer(containerId);
        }
    }
    
    private String createSecureContainer(ExecutionRequest request) {
        // Выбираем образ на основе языка
        String image = selectImage(request.getLanguage());
        
        // Создаём контейнер с максимальными ограничениями безопасности
        CreateContainerResponse container = dockerClient
            .createContainerCmd(image)
            .withName("sandbox-" + UUID.randomUUID().toString().substring(0, 8))
            
            // Ограничения ресурсов
            .withHostConfig(new HostConfig()
                // Память
                .withMemory(request.getMemoryLimitBytes())
                .withMemorySwap(request.getMemoryLimitBytes()) // без swap
                .withMemoryReservation(request.getMemoryLimitBytes() / 2)
                .withOomKillDisable(false) // разрешаем OOM killer
                
                // CPU
                .withCpuCount(request.getCpuCount())
                .withCpuQuota(request.getCpuQuota())
                .withCpuPeriod(100000L)
                
                // PIDs (процессы)
                .withPidsLimit(request.getMaxProcesses())
                
                // Файловая система (read-only root)
                .withReadonlyRootfs(true)
                .withTmpFs(Map.of(
                    "/tmp", "rw,noexec,nosuid,size=100m",
                    "/workspace", "rw,noexec,nosuid,size=100m"
                ))
                
                // Сетевая из
