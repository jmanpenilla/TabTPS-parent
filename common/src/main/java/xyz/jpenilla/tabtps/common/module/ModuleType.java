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
package xyz.jpenilla.tabtps.common.module;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import xyz.jpenilla.tabtps.common.TabTPS;
import xyz.jpenilla.tabtps.common.User;
import xyz.jpenilla.tabtps.common.config.Theme;

public final class ModuleType<T extends Module> {
  private static final Map<String, ModuleType<? extends Module>> TYPES_BY_NAME = new HashMap<>();
  private static final Map<Class<? extends Module>, ModuleType<? extends Module>> TYPES_BY_CLASS = new HashMap<>();

  public static final ModuleType<CPUModule> CPU = withoutPlayer(CPUModule.class, CPUModule::new, "cpu");
  public static final ModuleType<MemoryModule> MEMORY = withoutPlayer(MemoryModule.class, MemoryModule::new, "memory");
  public static final ModuleType<MSPTModule> MSPT = withoutPlayer(MSPTModule.class, MSPTModule::new, "mspt");
  public static final ModuleType<TPSModule> TPS = withoutPlayer(TPSModule.class, TPSModule::new, "tps");
  public static final ModuleType<PingModule> PING = withPlayer(PingModule.class, PingModule::new, "ping");
  public static final ModuleType<PlayerCountModule> PLAYER_COUNT = withoutPlayer(PlayerCountModule.class, PlayerCountModule::new, "players");
  public static final ModuleType<TimeModule> TIME = withoutPlayer(TimeModule.class, TimeModule::new, "time");

  public static Collection<ModuleType<?>> moduleTypes() {
    return Collections.unmodifiableCollection(TYPES_BY_NAME.values());
  }

  public static @NonNull ModuleType<? extends Module> fromName(final @NonNull String name) {
    final ModuleType<? extends Module> type = TYPES_BY_NAME.get(name);
    if (type == null) {
      throw new IllegalArgumentException("Unknown or invalid module type: " + name);
    }
    return type;
  }

  @SuppressWarnings("unchecked")
  public static <T extends Module> @NonNull ModuleType<T> fromClass(final @NonNull Class<T> moduleClass) {
    final ModuleType<T> type = (ModuleType<T>) TYPES_BY_CLASS.get(moduleClass);
    if (type == null) {
      throw new IllegalArgumentException("Unknown or invalid module class: " + moduleClass.getSimpleName());
    }
    return type;
  }

  private static <T extends Module> @NonNull ModuleType<T> withoutPlayer(
    final @NonNull Class<T> moduleClass,
    final @NonNull BiFunction<@NonNull TabTPS, @NonNull Theme, @NonNull T> moduleFactory,
    final @NonNull String name
  ) {
    return new ModuleType<>(
      moduleClass,
      (plugin, theme, player) -> moduleFactory.apply(plugin, theme),
      name,
      false
    );
  }

  private static <T extends Module> @NonNull ModuleType<T> withPlayer(
    final @NonNull Class<T> moduleClass,
    final @NonNull ModuleFactory<T> moduleFactory,
    final @NonNull String name
  ) {
    return new ModuleType<>(moduleClass, moduleFactory, name, true);
  }

  private final Class<T> moduleClass;
  private final ModuleFactory<T> moduleFactory;
  private final String name;
  private final boolean needsPlayer;

  private ModuleType(
    final @NonNull Class<T> moduleClass,
    final @NonNull ModuleFactory<T> moduleFactory,
    final @NonNull String name,
    final boolean needsPlayer
  ) {
    this.moduleClass = moduleClass;
    this.moduleFactory = moduleFactory;
    this.name = name;
    this.needsPlayer = needsPlayer;
    TYPES_BY_NAME.put(name, this);
    TYPES_BY_CLASS.put(moduleClass, this);
  }

  public @NonNull String name() {
    return this.name;
  }

  public boolean needsPlayer() {
    return this.needsPlayer;
  }

  public @NonNull Class<T> moduleClass() {
    return this.moduleClass;
  }

  public @NonNull T createModule(
    final @NonNull TabTPS tabTPS,
    final @NonNull Theme theme,
    final @Nullable User<?> user
  ) {
    if (this.needsPlayer && user == null) {
      throw new IllegalArgumentException(String.format("Module type '%s' requires a player", this.name));
    }
    return this.moduleFactory.create(tabTPS, theme, user);
  }

  @FunctionalInterface
  private interface ModuleFactory<T extends Module> {
    @NonNull T create(
      final @NonNull TabTPS tabTPS,
      final @NonNull Theme theme,
      final @Nullable User<?> user
    );
  }
}
