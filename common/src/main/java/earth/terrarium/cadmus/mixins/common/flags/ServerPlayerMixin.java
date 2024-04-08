package earth.terrarium.cadmus.mixins.common.flags;

import com.mojang.authlib.GameProfile;
import earth.terrarium.cadmus.common.flags.Flags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {

    @Shadow
    public abstract boolean hurt(DamageSource source, float amount);

    public ServerPlayerMixin(Level level, BlockPos pos, float yRot, GameProfile gameProfile) {
        super(level, pos, yRot, gameProfile);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (this.level().getGameTime() % 20 == 0) {
            float healRate = Flags.HEAL_RATE.get(this.level(), this.chunkPosition());
            if (healRate > 0) {
                this.heal(healRate);
            } else if (healRate < 0) {
                this.hurt(this.damageSources().generic(), -healRate);
            }

            float feedRate = Flags.FEED_RATE.get(this.level(), this.chunkPosition());
            if (feedRate > 0) {
                if (feedRate > this.random.nextFloat()) {
                    this.getFoodData().eat((int) Math.ceil(feedRate), feedRate);
                }
            }
        }
    }

    @Inject(method = "teleportTo(DDD)V", at = @At("HEAD"), cancellable = true)
    private void cadmus$teleportTo(double x, double y, double z, CallbackInfo ci) {
        if (!Flags.ALLOW_ENTRY.get(level(), new ChunkPos(BlockPos.containing(x, y, z)))) {
            String message = Flags.ENTRY_DENY_MESSAGE.get(level(), chunkPosition());
            if (!message.isBlank()) {
                displayClientMessage(Component.literal(message).withStyle(ChatFormatting.RED), false);
            }
            ci.cancel();
        }

        if (!Flags.ALLOW_EXIT.get(level(), chunkPosition())) {
            ci.cancel();
        }
    }

    @Inject(method = "restoreFrom", at = @At(value = "HEAD", target = "Lnet/minecraft/server/level/ServerPlayer;onUpdateAbilities()V"))
    private void cadmus$restoreFrom(ServerPlayer that, boolean keepEverything, CallbackInfo ci) {
        if (!keepEverything && Flags.KEEP_INVENTORY.get(that.serverLevel(), that.chunkPosition())) {
            this.getInventory().replaceWith(that.getInventory());
            this.experienceLevel = that.experienceLevel;
            this.totalExperience = that.totalExperience;
            this.experienceProgress = that.experienceProgress;
            this.setScore(that.getScore());
        }
    }
}
