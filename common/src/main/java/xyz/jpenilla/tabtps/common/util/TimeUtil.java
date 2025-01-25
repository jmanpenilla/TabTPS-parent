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
package xyz.jpenilla.tabtps.common.util;

import java.time.LocalDateTime;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.jpenilla.tabtps.common.config.Theme;

import static xyz.jpenilla.tabtps.common.util.Components.gradient;

public final class TimeUtil {
  private TimeUtil() {
  }

  public static @NonNull Component coloredTime(final LocalDateTime time, final Theme.@NonNull Colors colors) {
    final TextColor color1;
    final TextColor color2;
    final int hour = time.getHour();
    if (hour > 22 || hour < 7) {
      color1 = TextColor.color(255, 0, 0);
      color2 = TextColor.color(139, 0, 0);
    } else if (hour < 16) {
      color1 = TextColor.color(0, 255, 0);
      color2 = TextColor.color(144, 238, 144);
    } else {
      color1 = TextColor.color(255, 69, 0);
      color2 = TextColor.color(255, 140, 0);
    }
    return gradient(formatTime(time), color1, color2);
  }

  private static String formatTime(final LocalDateTime time) {
    return String.format("%02d", time.getHour()) + ":" + String.format("%02d", time.getMinute()) + ":" +
      String.format("%02d", time.getSecond());
  }
}

