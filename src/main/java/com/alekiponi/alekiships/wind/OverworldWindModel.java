package com.alekiponi.alekiships.wind;

import net.minecraft.util.LinearCongruentialGenerator;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

import java.util.Random;

public class OverworldWindModel implements WindModel {

    private final Level level;

    public OverworldWindModel(final Level level) {
        this.level = level;
    }

    @Override
    public Wind getWind(final double x, final double y, final double z) {
        // get the wind based on the total world time of the level and the location
        long time = level.getGameTime();

        // TODO redo wind model to be more interesting :) and less stolen
        // based on TFC wind for now
        long days = time / 24000L;
        Random random = this.seededRandom(days, 129341623413L);
        boolean isDay = level.getDayTime() % 24000L < 12000L;
        int windScale = 5000;
        boolean oddBand = z < 0 ? z % (windScale * 2) < windScale : z % (windScale * 2) > windScale;
        float intensity = random.nextFloat() * 0.3F + 0.3F + 0.4F * level.getRainLevel(0.0F);
        float angle;
        if (isDay && oddBand) {
            angle = 0.7853982F;
        } else if (isDay) {
            angle = 5.4977875F;
        } else if (oddBand) {
            angle = 3.926991F;
        } else {
            angle = 2.3561945F;
        }

        angle += random.nextFloat() * 0.2F - 0.1F;

        return Wind.of(intensity, (float) Math.toDegrees(angle));

    }

    // based on TFC seeding (again, for now)
    protected Random seededRandom(long day, long salt) {
        long seed = LinearCongruentialGenerator.next(129341623413L, day);
        seed = LinearCongruentialGenerator.next(seed, salt);
        return new Random(seed);
    }
}