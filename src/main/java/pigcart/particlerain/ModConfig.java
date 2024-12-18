package pigcart.particlerain;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "particlerain")
public class ModConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip @ConfigEntry.Gui.PrefixText
    public int maxParticleAmount = 1500;
    @ConfigEntry.Gui.Tooltip
    public int particleDensity = 80;
    @ConfigEntry.Gui.Tooltip
    public int particleStormDensity = 200;
    @ConfigEntry.Gui.Tooltip
    public int particleRadius = 30;

    @ConfigEntry.Gui.PrefixText
    public boolean doRainParticles = true;
    public boolean doSplashParticles = true;
    @Comment("❌ NOT IMPLEMENTED YET")
    public boolean doRippleParticles = false;
    @Comment("❌ NOT IMPLEMENTED YET")
    public boolean doStreakParticles = false;
    public boolean doSnowParticles = true;
    public boolean doSandParticles = true;
    public boolean doShrubParticles = true;
    public boolean doFogParticles = false;
    @Comment("❌ NOT IMPLEMENTED YET")
    public boolean doGroundFogParticles = false;

    @ConfigEntry.Gui.CollapsibleObject
    public RainOptions rain = new RainOptions();
    public static class RainOptions {
        @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
        public int density = 100;
        public float gravity = 1.0F;
        public float windStrength = 0.3F;
        public float stormWindStrength = 0.5F;
        public boolean biomeTint = true;
        @ConfigEntry.BoundedDiscrete(min = 0, max = 100) @ConfigEntry.Gui.Tooltip
        public int mix = 70;
        @ConfigEntry.BoundedDiscrete(min = 1, max = 100) @ConfigEntry.Gui.Tooltip
        public int opacity = 100;
        public int splashDensity = 5;
        public float size = 2F;
    }
    @ConfigEntry.Gui.CollapsibleObject
    public SnowOptions snow = new SnowOptions();
    public static class SnowOptions {
        @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
        public int density = 40;
        public float gravity = 0.1F;
        public float rotationAmount = 0.03F;
        @Comment("❌ NOT IMPLEMENTED YET")
        public float stormRotationAmount = 0.05F;
        public float windStrength = 0.1F;
        public float stormWindStrength = 0.5F;
        public float size = 2F;
    }
    @ConfigEntry.Gui.CollapsibleObject
    public SandOptions sand = new SandOptions();
    public static class SandOptions {
        @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
        public int density = 80;
        public float gravity = 0.2F;
        public float windStrength = 0.3F;
        public float moteSize = 0.1F;
        public float size = 2F;
        @Comment("❌ NOT IMPLEMENTED YET")
        public boolean spawnOnGround = false;
        public String matchIds = "sand";
    }
    @ConfigEntry.Gui.CollapsibleObject
    public ShrubOptions shrub = new ShrubOptions();
    public static class ShrubOptions {
        @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
        public int density = 5;
        public float gravity = 0.2F;
        public float rotationAmount = 0.2F;
        public float bounciness = 0.1F;
    }
    @ConfigEntry.Gui.CollapsibleObject
    public FogOptions fog = new FogOptions();
    public static class FogOptions {
        @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
        public int density = 20;
        public float gravity = 0.2F;
        public float size = 0.5F;
    }
    @ConfigEntry.Gui.CollapsibleObject @Comment("❌ NOT IMPLEMENTED YET")
    public GroundFogOptions groundFog = new GroundFogOptions();
    public static class GroundFogOptions {
        @ConfigEntry.BoundedDiscrete(min = 1, max = 100) @Comment("❌ NOT IMPLEMENTED YET")
        public int density = 20;
        @Comment("❌ NOT IMPLEMENTED YET")
        public int spawnHeight = 64;
        @Comment("❌ NOT IMPLEMENTED YET")
        public int spawnTime = 0;
        @Comment("❌ NOT IMPLEMENTED YET")
        public float size = 2.5F;
    }
    @ConfigEntry.Gui.PrefixText
    public boolean renderVanillaWeather = false;
    public boolean tickVanillaWeather = false;
    public boolean alwaysRaining = false;
    @Comment("❌ NOT IMPLEMENTED YET")
    public boolean yLevelWindAdjustment = false;
    @ConfigEntry.Gui.Tooltip
    public boolean syncRegistry = true;
}