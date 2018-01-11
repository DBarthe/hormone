package hormone;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Java OptionalImpl type interface extracted to be used with virtual proxy.
 *
 * C'est pour que les relations OneToOne et OneToMany puissent être proxyfiées.
 */
public interface Optional<T> {
    T get();
    boolean isPresent();
    void ifPresent(Consumer<? super T> consumer);
    OptionalImpl<T> filter(Predicate<? super T> predicate);
    <U> OptionalImpl<U> map(Function<? super T, ? extends U> mapper);
    <U> OptionalImpl<U> flatMap(Function<? super T, OptionalImpl<U>> mapper);
    T orElseGet(Supplier<? extends T> other);
    T orElse(T other);
    <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X;
}
