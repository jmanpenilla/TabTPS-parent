/*
 * This file is part of TabTPS, licensed under the MIT License.
 *
 * Copyright (c) 2020-2024 Jason Penilla
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package xyz.jpenilla.tabtps.spigot.service;

import java.lang.reflect.Method;
import org.bukkit.Bukkit;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import xyz.jpenilla.tabtps.common.service.TickTimeService;
import xyz.jpenilla.tabtps.common.util.FloatSupplier;

public final class PaperTickTimeService implements TickTimeService {
  private final FloatSupplier targetTickRateGetter;

  public PaperTickTimeService() {
    this.targetTickRateGetter = makeTickRateGetter();
  }

  @Override
  public double averageMspt() {
    return Bukkit.getAverageTickTime();
  }

  @Override
  public double @NonNull [] recentTps() {
    return Bukkit.getTPS();
  }

  @Override
  public float targetTickRate() {
    return this.targetTickRateGetter.get();
  }

  private static @NotNull FloatSupplier makeTickRateGetter() {
    try {
      final Method getServerTickManager = Bukkit.class.getDeclaredMethod("getServerTickManager");
      final Object serverTickManager = getServerTickManager.invoke(null);
      final Method getTickRate = serverTickManager.getClass().getMethod("getTickRate");
      getTickRate.setAccessible(true);
      return () -> {
        try {
          return (float) getTickRate.invoke(serverTickManager);
        } catch (final ReflectiveOperationException e) {
          throw new RuntimeException(e);
        }
      };
    } catch (final ReflectiveOperationException e) {
      return () -> 20;
    }
  }
}
