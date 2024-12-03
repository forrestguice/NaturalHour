/**
    Copyright (C) 2024 Forrest Guice
    This file is part of SuntimesWidget.

    SuntimesWidget is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    SuntimesWidget is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with SuntimesWidget.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.forrestguice.suntimes.naturalhour.data;

import android.util.Log;

import com.forrestguice.suntimes.annotation.NonNull;
import com.forrestguice.suntimes.annotation.Nullable;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ExecutorUtils
{
    public static boolean runTask(String tag, @NonNull final Callable<Boolean> r, long timeoutAfter)
    {
        Boolean result = getResult(tag, r, timeoutAfter);
        return (result != null && result);
    }

    @Nullable
    public static <T> T getResult(String tag, @NonNull final Callable<T> callable, long timeoutAfter)
    {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        final Future<T> task = executor.submit(callable);
        try {
            return task.get(timeoutAfter, TimeUnit.MILLISECONDS);

        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            Log.e(tag, "getResult: failed! " + e);
            return null;

        } finally {
            task.cancel(true);
            executor.shutdownNow();
        }
    }
}
