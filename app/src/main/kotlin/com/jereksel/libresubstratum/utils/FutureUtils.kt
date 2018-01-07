/*
 * Copyright (C) 2017 Andrzej Ressel (jereksel@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.jereksel.libresubstratum.utils

import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

object FutureUtils {

    class FinishedFuture<T>(
            val element: T
    ): ListenableFuture<T> {
        override fun isDone() = true

        override fun get() = element

        override fun get(timeout: Long, unit: TimeUnit?) = element

        override fun cancel(mayInterruptIfRunning: Boolean) = true

        override fun addListener(listener: Runnable, executor: Executor) =
                executor.execute { listener.run() }

        override fun isCancelled() = false

    }

    public fun <T> T.toFuture() = FinishedFuture(this)
}