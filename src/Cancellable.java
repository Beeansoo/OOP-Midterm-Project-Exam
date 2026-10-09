import java.time.LocalDateTime;

public interface Cancellable {
    void cancel(LocalDateTime now);
}