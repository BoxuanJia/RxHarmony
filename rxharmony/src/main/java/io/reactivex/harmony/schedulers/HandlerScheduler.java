/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.reactivex.harmony.schedulers;

import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.plugins.RxJavaPlugins;
import ohos.eventhandler.EventHandler;
import ohos.eventhandler.InnerEvent;

import java.util.concurrent.TimeUnit;

public class HandlerScheduler extends Scheduler {
    private final EventHandler handler;

    HandlerScheduler(EventHandler handler) {
        this.handler = handler;
    }

    @Override
    public Disposable scheduleDirect(Runnable run, long delay, TimeUnit unit) {
        if (run == null) throw new NullPointerException("run == null");
        if (unit == null) throw new NullPointerException("unit == null");

        run = RxJavaPlugins.onSchedule(run);
        ScheduledRunnable scheduled = new ScheduledRunnable(handler, run);
        InnerEvent message = InnerEvent.get(scheduled);
        handler.sendEvent(message, unit.toMillis(delay));
        return scheduled;
    }

    @Override
    public Worker createWorker() {
        return new HandlerWorker(handler);
    }

    private static final class HandlerWorker extends Worker {
        private final EventHandler handler;

        private volatile boolean disposed;

        HandlerWorker(EventHandler handler) {
            this.handler = handler;
        }

        @Override
        public Disposable schedule(Runnable run, long delay, TimeUnit unit) {
            if (run == null) throw new NullPointerException("run == null");
            if (unit == null) throw new NullPointerException("unit == null");

            if (disposed) {
                return Disposables.disposed();
            }

            run = RxJavaPlugins.onSchedule(run);

            ScheduledRunnable scheduled = new ScheduledRunnable(handler, run);

            InnerEvent message = InnerEvent.get(scheduled);
            message.object = this; // Used as token for batch disposal of this worker's runnables.
            handler.sendEvent(message, unit.toMillis(delay));

            // Re-check disposed state for removing in case we were racing a call to dispose().
            if (disposed) {
                handler.removeTask(scheduled);
                return Disposables.disposed();
            }

            return scheduled;
        }

        @Override
        public void dispose() {
            disposed = true;
            handler.removeAllEvent();
        }

        @Override
        public boolean isDisposed() {
            return disposed;
        }
    }

    private static final class ScheduledRunnable implements Runnable, Disposable {
        private final EventHandler handler;
        private final Runnable delegate;

        private volatile boolean disposed; // Tracked solely for isDisposed().

        ScheduledRunnable(EventHandler handler, Runnable delegate) {
            this.handler = handler;
            this.delegate = delegate;
        }

        @Override
        public void run() {
            try {
                delegate.run();
            } catch (Throwable t) {
                RxJavaPlugins.onError(t);
            }
        }

        @Override
        public void dispose() {
            handler.removeTask(this);
            disposed = true;
        }

        @Override
        public boolean isDisposed() {
            return disposed;
        }
    }
}
