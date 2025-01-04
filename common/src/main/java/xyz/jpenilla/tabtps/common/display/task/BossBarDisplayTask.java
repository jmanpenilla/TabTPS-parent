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
package xyz.jpenilla.tabtps.common.display.task;

import net.kyori.adventure.bossbar.BossBar;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.jpenilla.tabtps.common.TabTPS;
import xyz.jpenilla.tabtps.common.User;
import xyz.jpenilla.tabtps.common.config.DisplayConfig;
import xyz.jpenilla.tabtps.common.config.Theme;
import xyz.jpenilla.tabtps.common.display.Display;
import xyz.jpenilla.tabtps.common.module.ModuleRenderer;
import xyz.jpenilla.tabtps.common.util.TPSUtil;

public final class BossBarDisplayTask implements Display {
  private final TabTPS tabTPS;
  private final User<?> user;
  private final DisplayConfig.BossBarSettings settings;
  private final ModuleRenderer renderer;
  private final BossBar bar;

  public BossBarDisplayTask(
    final @NonNull TabTPS tabTPS,
    final @NonNull User<?> user,
    final DisplayConfig.@NonNull BossBarSettings settings
  ) {
    this.tabTPS = tabTPS;
    this.user = user;
    this.settings = settings;
    final Theme theme = tabTPS.configManager().theme(settings.theme());
    this.renderer = ModuleRenderer.builder()
      .modules(tabTPS, theme, user, settings.modules())
      .separator(settings.separator())
      .moduleRenderFunction(ModuleRenderer.standardRenderFunction(theme))
      .build();
    this.bar = BossBar.bossBar(
      this.renderer.render(),
      this.progress(),
      this.color(),
      this.overlay()
    );
    user.showBossBar(this.bar);
  }

  private float progress() {
    switch (this.settings.fillMode()) {
      case MSPT:
        return this.msptProgress();
      case TPS:
        return this.tpsProgress();
      case REVERSE_MSPT:
        return 1.0F - this.msptProgress();
      case REVERSE_TPS:
        return 1.0F - this.tpsProgress();
      default:
        throw new IllegalStateException("Unknown or invalid fill mode: " + this.settings.fillMode());
    }
  }

  private float msptProgress() {
    return ensureInRange(this.tabTPS.platform().tickTimeService().averageMspt() / this.tabTPS.platform().tickTimeService().targetMspt());
  }

  private float tpsProgress() {
    return ensureInRange(this.tabTPS.platform().tickTimeService().recentTps()[0] / this.tabTPS.platform().tickTimeService().targetTickRate());
  }

  private static float ensureInRange(final double value) {
    return (float) Math.max(0.00D, Math.min(1.00D, value));
  }

  private BossBar.@NonNull Color color() {
    switch (this.settings.fillMode()) {
      case MSPT:
      case REVERSE_MSPT:
        final double mspt = this.tabTPS.platform().tickTimeService().averageMspt();
        final float targetMspt = this.tabTPS.platform().tickTimeService().targetMspt();
        if (mspt < TPSUtil.scaleMspt(25, targetMspt)) {
          return this.settings.colors().goodPerformance();
        } else if (mspt < TPSUtil.scaleMspt(40, targetMspt)) {
          return this.settings.colors().mediumPerformance();
        } else {
          return this.settings.colors().lowPerformance();
        }
      case REVERSE_TPS:
      case TPS:
        final double tps = this.tabTPS.platform().tickTimeService().recentTps()[0];
        final float targetTps = this.tabTPS.platform().tickTimeService().targetTickRate();
        if (tps > TPSUtil.scaleTps(18.50f, targetTps)) {
          return this.settings.colors().goodPerformance();
        } else if (tps > TPSUtil.scaleTps(15.00f, targetTps)) {
          return this.settings.colors().mediumPerformance();
        } else {
          return this.settings.colors().lowPerformance();
        }
      default:
        throw new IllegalStateException("Unknown or invalid fill mode: " + this.settings.fillMode());
    }
  }

  private BossBar.@NonNull Overlay overlay() {
    return this.settings.overlay();
  }

  private void updateBar() {
    this.bar.progress(this.progress());
    this.bar.color(this.color());
    this.bar.name(this.renderer.render());
  }

  @Override
  public void disable() {
    this.user.hideBossBar(this.bar);
  }

  @Override
  public void run() {
    if (!this.user.online()) {
      this.user.bossBar().stopDisplay();
      return;
    }
    this.updateBar();
  }
}
