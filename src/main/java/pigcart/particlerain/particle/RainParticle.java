package pigcart.particlerain.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.*;
import org.joml.Math;
import pigcart.particlerain.ParticleRainClient;

import java.awt.*;

public class RainParticle extends WeatherParticle {

    protected RainParticle(ClientLevel level, double x, double y, double z, SpriteSet provider) {
        super(level, x, y, z, ParticleRainClient.config.rain.gravity, provider);

        if (ParticleRainClient.config.rain.biomeTint) {
            final Color waterColor = new Color(BiomeColors.getAverageWaterColor(level, this.pos));
            final Color fogColor = new Color(this.level.getBiome(this.pos).value().getFogColor());
            this.rCol = (Mth.lerp(ParticleRainClient.config.rain.mix / 100F, waterColor.getRed(), fogColor.getRed()) / 255F);
            this.gCol = (Mth.lerp(ParticleRainClient.config.rain.mix / 100F, waterColor.getGreen(), fogColor.getGreen()) / 255F);
            this.bCol = (Mth.lerp(ParticleRainClient.config.rain.mix / 100F, waterColor.getBlue(), fogColor.getBlue()) / 255F);
        }

        this.quadSize = ParticleRainClient.config.rain.size;
        this.gravity = ParticleRainClient.config.rain.gravity;
        this.setSprite(Minecraft.getInstance().getModelManager().getAtlas(ParticleRainClient.BLOCKS_LOCATION).getSprite(ParticleRainClient.RAIN_SPRITE));
        //System.out.println(Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_PARTICLES).getSprite(ParticleRainClient.RAIN_SPRITE));
        //TODO: figure out how to get the sprite on the particle atlas to fix mipmapping

        if (level.isThundering()) {
            this.xd = gravity * ParticleRainClient.config.rain.stormWindStrength;
            this.zd = gravity * ParticleRainClient.config.rain.stormWindStrength;
        } else {
            this.xd = gravity * ParticleRainClient.config.rain.windStrength;
            this.zd = gravity * ParticleRainClient.config.rain.windStrength;
        }
        //TODO: multiply wind strength by Y level - angled rain looks weird when it falls into caves

        this.lifetime = ParticleRainClient.config.particleRadius * 5;
        Vec3 vec3 = Minecraft.getInstance().cameraEntity.position();
        this.roll = (float) (Math.atan2(x - vec3.x, z - vec3.z) + Mth.HALF_PI);
    }

    @Override
    public void tick() {
        super.tick();
         if (this.age < 10) this.alpha = Math.clamp(0, ParticleRainClient.config.rain.opacity / 100F, this.alpha);
        if (this.onGround) {
            if (ParticleRainClient.config.doSplashParticles && Minecraft.getInstance().cameraEntity.position().distanceTo(this.pos.getCenter()) < ParticleRainClient.config.particleRadius - (ParticleRainClient.config.particleRadius / 2.0)) {
                for (int i = 0; i < ParticleRainClient.config.rain.splashDensity; i++) {
                    Vec3 spawnPos = Vec3.atLowerCornerWithOffset(this.pos, (random.nextFloat() * 3) - 1, 0, (random.nextFloat() * 3) - 1);
                    double d = random.nextDouble();
                    double e = random.nextDouble();
                    BlockPos blockPos = BlockPos.containing(spawnPos);
                    BlockState blockState = level.getBlockState(blockPos);
                    FluidState fluidState = level.getFluidState(blockPos);
                    VoxelShape voxelShape = blockState.getCollisionShape(level, blockPos);
                    double g = voxelShape.max(Direction.Axis.Y, d, e);
                    double h = fluidState.getHeight(level, blockPos);
                    double m = java.lang.Math.max(g, h);
                    Vec3 raycastStart = new Vec3(this.x, this.y, this.z);
                    Vec3 raycastEnd = new Vec3(spawnPos.x, this.y, spawnPos.z);
                    BlockHitResult hit = level.clip(new ClipContext(raycastStart, raycastEnd, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, CollisionContext.empty()));
                    if (m != 0 && hit.getLocation().distanceTo(new Vec3(spawnPos.x, spawnPos.y + 1, spawnPos.z)) < 0.01) {
                        if (this.isHotBlock()) {
                            Minecraft.getInstance().particleEngine.createParticle(ParticleTypes.SMOKE, spawnPos.x, spawnPos.y + m, spawnPos.z, 0, 0, 0);
                        } else {
                            Minecraft.getInstance().particleEngine.createParticle(ParticleTypes.RAIN, spawnPos.x, spawnPos.y + m, spawnPos.z, 0, 0, 0);
                        }
                    }
                }
            }
            this.remove();
        } else if (!this.level.getFluidState(this.pos).isEmpty()) {
            this.remove();
        } else if (this.removeIfObstructed()) {
            Minecraft.getInstance().particleEngine.createParticle(ParticleTypes.RAIN, this.x, this.y, this.z, 0, 0, 0);
            this.remove();
        }
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float tickPercentage) {
        Vector3f camPos = camera.getPosition().toVector3f();
        float x = (float) (Mth.lerp(tickPercentage, this.xo, this.x) - camPos.x);
        float y = (float) (Mth.lerp(tickPercentage, this.yo, this.y) - camPos.y);
        float z = (float) (Mth.lerp(tickPercentage, this.zo, this.z) - camPos.z);

        // angle particle along axis of velocity
        Vector3f delta = new Vector3f((float) this.xd, (float) this.yd, (float) this.zd);
        final float angle = Math.acos(delta.normalize().y);
        Vector3f axis = new Vector3f(-delta.z(), 0, delta.x()).normalize();
        Quaternionf quaternion = new Quaternionf(new AxisAngle4f(-angle, axis));

        // rotate particle to face camera
        //quaternion.mul(Axis.YN.rotation(Math.atan2(x, z) + Mth.HALF_PI));
        //FIXME: idk how to translate this to work with the angled axis, using as-is results in weird rotation
        // for now the rotation is calculated once when the particle spawns, which looks good enough
        quaternion.mul(Axis.YN.rotation(this.roll));
        quaternion = this.flipItTurnwaysIfBackfaced(quaternion, new Vector3f(x, y, z));
        this.renderRotatedQuad(vertexConsumer, quaternion, x, y, z, tickPercentage);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.TERRAIN_SHEET;
    }

    @Environment(EnvType.CLIENT)
    public static class DefaultFactory implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet provider;

        public DefaultFactory(SpriteSet provider) {
            this.provider = provider;
        }

        @Override
        public Particle createParticle(SimpleParticleType parameters, ClientLevel level, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new RainParticle(level, x, y, z, this.provider);
        }
    }
}