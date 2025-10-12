import java.util.concurrent.*;

public class ResourceLimitedExecutor {
    
    private final ExecutorService executor;
    private final long timeoutSeconds;
    
    public ResourceLimitedExecutor(int maxThreads, long timeoutSeconds) {
        this.executor = Executors.newFixedThreadPool(maxThreads);
        this.timeoutSeconds = timeoutSeconds;
    }
    
    public <T> T executeWithTimeout(Callable<T> task) 
            throws TimeoutException, ExecutionException {
        Future<T> future = executor.submit(task);
        try {
            // Ограничение времени выполнения
            return future.get(timeoutSeconds, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);  // прерываем задачу
            throw new TimeoutException(
                "Task exceeded timeout of " + timeoutSeconds + " seconds");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ExecutionException("Task was interrupted", e);
        }
    }
    
    public void shutdown() {
        executor.shutdownNow();
    }
}

// Использование
public class Example {
    public static void main(String[] args) {
        ResourceLimitedExecutor executor = 
            new ResourceLimitedExecutor(10, 5);  // макс 10 потоков, 5 сек таймаут
        
        try {
            String result = executor.executeWithTimeout(() -> {
                // Симуляция долгой операции
                Thread.sleep(3000);
                return "Success";
            });
            System.out.println(result);
        } catch (TimeoutException e) {
            System.err.println("Operation timed out!");
        } catch (ExecutionException e) {
            System.err.println("Execution failed: " + e.getCause());
        }
        
        executor.shutdown();
    }
}
