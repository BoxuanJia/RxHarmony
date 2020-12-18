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
import io.reactivex.harmony.plugins.RxHarmonyPlugins;
import ohos.eventhandler.EventHandler;
import ohos.eventhandler.EventRunner;

public class HarmonySchedulers {

    private static final class MainHolder {
        static final Scheduler DEFAULT
                = new HandlerScheduler(new EventHandler(EventRunner.getMainEventRunner()));
    }

    private static final Scheduler MAIN_THREAD =
            RxHarmonyPlugins.initMainThreadScheduler(() -> MainHolder.DEFAULT);

    /**
     * A {@link Scheduler} which executes actions on the Android main thread.
     * <p>
     * The returned scheduler will post asynchronous messages to the looper by default.
     *
     * @see #from(EventRunner)
     */
    public static Scheduler mainThread() {
        return RxHarmonyPlugins.onMainThreadScheduler(MAIN_THREAD);
    }

    /**
     * A {@link Scheduler} which executes actions on {@code looper}.
     * <p>
     * The returned scheduler will post asynchronous messages to the looper by default.
     */
    public static Scheduler from(EventRunner looper) {
        if (looper == null) throw new NullPointerException("looper == null");
        return new HandlerScheduler(new EventHandler(looper));
    }

    private HarmonySchedulers() {
        throw new AssertionError("No instances.");
    }
}
