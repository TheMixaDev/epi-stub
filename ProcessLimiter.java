import java.util.concurrent.*;

public class ProcessLimiter {
    
    // Ограничение на максимум 10 одновременных процессов
    private static final int MAX_PROCESSES = 10;
    private static final int CORE_POOL_SIZE = 5;
    
    private final ExecutorService executor;
    
    public ProcessLimiter() {
        // Создаём пул с фиксированным ограничением
        this.executor = new ThreadPoolExecutor(
            CORE_POOL_SIZE,           // минимальное количество потоков
            MAX_PROCESSES,            // максимальное количество потоков
            60L,                      // время жизни простаивающих потоков
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100),  // очередь задач (макс 100)
            new ThreadPoolExecutor.CallerRunsPolicy()  // политика отклонения
        );
    }
    
    public void executeTask(Runnable task) {
        executor.submit(task);
    }
    
    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    // Пример использования
    public static void main(String[] args) {
        ProcessLimiter limiter = new ProcessLimiter();
        
        // Отправляем 50 задач, но выполняться будут только 10 одновременно
        for (int i = 0; i < 50; i++) {
            final int taskId = i;
            limiter.executeTask(() -> {
                System.out.println("Executing task " + taskId + 
                    " on thread " + Thread.currentThread().getName());
                try {
                    Thread.sleep(2000); // симуляция работы
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        limiter.shutdown();
    }
}
