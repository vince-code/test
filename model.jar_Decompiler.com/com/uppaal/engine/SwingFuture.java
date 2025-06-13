package com.uppaal.engine;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.swing.SwingUtilities;

public class SwingFuture<T> implements Future<T>, CompletionStage<T> {
   private final CompletableFuture<T> base;
   private CancellableToken cancellableToken;

   public SwingFuture(CompletableFuture<T> base) {
      this.base = base;
   }

   public SwingFuture() {
      this.base = new CompletableFuture();
   }

   public SwingFuture<T> setCancelToken(CancellableToken cancellableToken) {
      if (cancellableToken == null) {
         throw new IllegalArgumentException("Does not accept null argument");
      } else {
         this.cancellableToken = cancellableToken;
         return this;
      }
   }

   public static <U> SwingFuture<U> completedFuture(U value) {
      return new SwingFuture(CompletableFuture.completedFuture(value));
   }

   public static <U> SwingFuture<U> failedFuture(Throwable exception) {
      return new SwingFuture(CompletableFuture.failedFuture(exception));
   }

   public static SwingFuture<Void> allOf(SwingFuture<?>... futures) {
      return new SwingFuture(CompletableFuture.allOf(getArray(futures)));
   }

   public static SwingFuture<Object> anyOf(SwingFuture<?>... futures) {
      return new SwingFuture(CompletableFuture.anyOf(getArray(futures)));
   }

   private static CompletableFuture[] getArray(SwingFuture[] futures) {
      return (CompletableFuture[])Arrays.stream(futures).map((f) -> {
         return f.base;
      }).toArray((x$0) -> {
         return new CompletableFuture[x$0];
      });
   }

   public SwingFuture<Void> swingRun(Runnable runnable) {
      SwingFuture<Void> future = new SwingFuture();
      this.thenRun(() -> {
         this.swingInvokeLater(() -> {
            runnable.run();
            future.complete((Object)null);
         });
      });
      return future;
   }

   public SwingFuture<Void> swingAccept(Consumer<T> consumer) {
      SwingFuture<Void> future = new SwingFuture();
      this.thenAccept((t) -> {
         this.swingInvokeLater(() -> {
            consumer.accept(t);
            future.complete((Object)null);
         });
      });
      return future;
   }

   public <U> SwingFuture<U> swingApply(Function<T, U> function) {
      SwingFuture<U> future = new SwingFuture();
      this.thenAccept((t) -> {
         this.swingInvokeLater(() -> {
            U u = function.apply(t);
            future.complete(u);
         });
      });
      return future;
   }

   public SwingFuture<T> swingExceptionally(Consumer<Throwable> consumer) {
      this.base.exceptionally((ex) -> {
         this.swingInvokeLater(() -> {
            consumer.accept(ex);
         });
         return null;
      });
      return this;
   }

   private void swingInvokeLater(Runnable runnable) {
      if (this.cancellableToken != null) {
         this.cancellableToken.invokeLater(runnable);
      } else {
         SwingUtilities.invokeLater(runnable);
      }

   }

   public boolean cancel(boolean mayInterruptIfRunning) {
      return this.base.cancel(mayInterruptIfRunning);
   }

   public boolean complete(T value) {
      return this.base.complete(value);
   }

   public boolean completeExceptionally(Throwable ex) {
      return this.base.completeExceptionally(ex);
   }

   public SwingFuture<T> copy() {
      return new SwingFuture(this.base.copy());
   }

   public T join() {
      return this.base.join();
   }

   private <U> SwingFuture<U> wrap(CompletableFuture<U> future) {
      SwingFuture<U> newFuture = new SwingFuture(future);
      newFuture.cancellableToken = this.cancellableToken;
      return newFuture;
   }

   public SwingFuture<Void> thenAccept(Consumer<? super T> action) {
      return this.wrap(this.base.thenAccept(action));
   }

   public SwingFuture<Void> acceptEither(CompletionStage<? extends T> other, Consumer<? super T> action) {
      return this.wrap(this.base.acceptEither(other, action));
   }

   public SwingFuture<Void> acceptEitherAsync(CompletionStage<? extends T> other, Consumer<? super T> action) {
      return this.wrap(this.base.acceptEitherAsync(other, action));
   }

   public SwingFuture<Void> acceptEitherAsync(CompletionStage<? extends T> other, Consumer<? super T> action, Executor executor) {
      return this.wrap(this.base.acceptEitherAsync(other, action, executor));
   }

   public SwingFuture<Void> thenAcceptAsync(Consumer<? super T> action) {
      return this.wrap(this.base.thenAcceptAsync(action));
   }

   public SwingFuture<Void> thenAcceptAsync(Consumer<? super T> action, Executor executor) {
      return this.wrap(this.base.thenAcceptAsync(action, executor));
   }

   public <U> SwingFuture<Void> thenAcceptBoth(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action) {
      return this.wrap(this.base.thenAcceptBoth(other, action));
   }

   public <U> SwingFuture<Void> thenAcceptBothAsync(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action) {
      return this.wrap(this.base.thenAcceptBothAsync(other, action));
   }

   public <U> SwingFuture<Void> thenAcceptBothAsync(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action, Executor executor) {
      return this.wrap(this.base.thenAcceptBothAsync(other, action, executor));
   }

   public <U> SwingFuture<U> applyToEither(CompletionStage<? extends T> other, Function<? super T, U> fn) {
      return this.wrap(this.base.applyToEither(other, fn));
   }

   public <U> SwingFuture<U> applyToEitherAsync(CompletionStage<? extends T> other, Function<? super T, U> fn) {
      return this.wrap(this.base.applyToEitherAsync(other, fn));
   }

   public <U> SwingFuture<U> applyToEitherAsync(CompletionStage<? extends T> other, Function<? super T, U> fn, Executor executor) {
      return this.wrap(this.base.applyToEitherAsync(other, fn, executor));
   }

   public <U> SwingFuture<U> thenApply(Function<? super T, ? extends U> fn) {
      return this.wrap(this.base.thenApply(fn));
   }

   public <U> SwingFuture<U> thenApplyAsync(Function<? super T, ? extends U> fn) {
      return this.wrap(this.base.thenApplyAsync(fn));
   }

   public <U> SwingFuture<U> thenApplyAsync(Function<? super T, ? extends U> fn, Executor executor) {
      return this.wrap(this.base.thenApplyAsync(fn, executor));
   }

   public SwingFuture<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action) {
      return this.wrap(this.base.whenCompleteAsync(action));
   }

   public SwingFuture<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action, Executor executor) {
      return this.wrap(this.base.whenCompleteAsync(action, executor));
   }

   public <U> SwingFuture<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn) {
      return this.wrap(this.base.thenComposeAsync(fn));
   }

   public <U> SwingFuture<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn, Executor executor) {
      return this.wrap(this.base.thenComposeAsync(fn, executor));
   }

   public <U, V> SwingFuture<V> thenCombineAsync(CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn) {
      return this.wrap(this.base.thenCombineAsync(other, fn));
   }

   public <U, V> SwingFuture<V> thenCombineAsync(CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn, Executor executor) {
      return this.wrap(this.base.thenCombineAsync(other, fn, executor));
   }

   public SwingFuture<Void> runAfterBoth(CompletionStage<?> other, Runnable action) {
      return this.wrap(this.base.runAfterBoth(other, action));
   }

   public SwingFuture<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action) {
      return this.wrap(this.base.runAfterBothAsync(other, action));
   }

   public SwingFuture<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action, Executor executor) {
      return this.wrap(this.base.runAfterBothAsync(other, action, executor));
   }

   public SwingFuture<Void> runAfterEither(CompletionStage<?> other, Runnable action) {
      return this.wrap(this.base.runAfterEither(other, action));
   }

   public SwingFuture<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action) {
      return this.wrap(this.base.runAfterEitherAsync(other, action));
   }

   public SwingFuture<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action, Executor executor) {
      return this.wrap(this.base.runAfterEitherAsync(other, action, executor));
   }

   public SwingFuture<Void> thenRun(Runnable action) {
      return this.wrap(this.base.thenRun(action));
   }

   public SwingFuture<Void> thenRunAsync(Runnable action) {
      return this.wrap(this.base.thenRunAsync(action));
   }

   public SwingFuture<Void> thenRunAsync(Runnable action, Executor executor) {
      return this.wrap(this.base.thenRunAsync(action, executor));
   }

   public SwingFuture<T> completeOnTimeout(T value, long timeout, TimeUnit unit) {
      return this.wrap(this.base.completeOnTimeout(value, timeout, unit));
   }

   public SwingFuture<T> exceptionally(Function<Throwable, ? extends T> fn) {
      return this.wrap(this.base.exceptionally(fn));
   }

   public <U, V> SwingFuture<V> thenCombine(CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn) {
      return this.wrap(this.base.thenCombine(other, fn));
   }

   public <U> SwingFuture<U> thenCompose(Function<? super T, ? extends CompletionStage<U>> fn) {
      return this.wrap(this.base.thenCompose(fn));
   }

   public SwingFuture<T> whenComplete(BiConsumer<? super T, ? super Throwable> action) {
      return this.wrap(this.base.whenComplete(action));
   }

   public <U> SwingFuture<U> handle(BiFunction<? super T, Throwable, ? extends U> fn) {
      return this.wrap(this.base.handle(fn));
   }

   public <U> SwingFuture<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn) {
      return this.wrap(this.base.handleAsync(fn));
   }

   public <U> SwingFuture<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn, Executor executor) {
      return this.wrap(this.base.handleAsync(fn, executor));
   }

   public CompletableFuture<T> toCompletableFuture() {
      return this.base.toCompletableFuture();
   }

   public SwingFuture<T> orTimeout(long timeout, TimeUnit unit) {
      return this.wrap(this.base.orTimeout(timeout, unit));
   }

   public boolean isCancelled() {
      return this.base.isCancelled();
   }

   public boolean isDone() {
      return this.base.isDone();
   }

   public T get() throws InterruptedException, ExecutionException {
      return this.base.get();
   }

   public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
      return this.base.get(timeout, unit);
   }
}
