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
package xyz.jpenilla.tabtps.fabric;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.modcommon.MinecraftServerAudiences;
import net.kyori.adventure.text.Component;
import net.minecraft.server.level.ServerPlayer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import xyz.jpenilla.tabtps.common.AbstractUser;

@DefaultQualifier(NonNull.class)
public final class FabricUser extends AbstractUser<ServerPlayer> {
  private final TabTPSFabric tabTPSFabric;

  private FabricUser(final TabTPSFabric tabTPS, final ServerPlayer player) {
    super(tabTPS.tabTPS(), player, player.getUUID());
    this.tabTPSFabric = tabTPS;
  }

  public static FabricUser from(final TabTPSFabric tabTPSFabric, final ServerPlayer player) {
    return new FabricUser(tabTPSFabric, player);
  }

  @Override
  public Component displayName() {
    return MinecraftServerAudiences.of(this.base().getServer()).asAdventure(this.base().getDisplayName());
  }

  @Override
  public boolean online() {
    return this.tabTPSFabric.server().getPlayerList().getPlayer(this.uuid()) == this.base();
  }

  @Override
  public int ping() {
    return this.base().connection.latency();
  }

  @Override
  public boolean hasPermission(final String permissionString) {
    return Permissions.check(this.base(), permissionString, this.base().getServer().getOperatorUserPermissionLevel());
  }

  @Override
  public Audience audience() {
    return this.base();
  }
}
