package com.elmakers.mine.bukkit.action.builtin;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Villager;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import com.elmakers.mine.bukkit.action.BaseSpellAction;
import com.elmakers.mine.bukkit.api.action.CastContext;
import com.elmakers.mine.bukkit.api.effect.EffectPlayer;
import com.elmakers.mine.bukkit.api.entity.EntityData;
import com.elmakers.mine.bukkit.api.magic.MageController;
import com.elmakers.mine.bukkit.api.spell.Spell;
import com.elmakers.mine.bukkit.api.spell.SpellResult;
import com.elmakers.mine.bukkit.spell.BaseSpell;
import com.elmakers.mine.bukkit.utility.CompatibilityUtils;
import com.elmakers.mine.bukkit.utility.ConfigurationUtils;
import com.elmakers.mine.bukkit.utility.RandomUtils;
import com.elmakers.mine.bukkit.utility.WeightedPair;

public class SpawnEntityAction extends BaseSpellAction
{
    private Deque<WeightedPair<String>> entityTypeProbability;

    private CreatureSpawnEvent.SpawnReason spawnReason = CreatureSpawnEvent.SpawnReason.EGG;

    private boolean loot = false;
    private boolean setTarget = false;
    private boolean force = false;
    private boolean repeatRandomize = true;

    private Vector direction = null;
    private double speed;
    private double dyOffset;

    private EntityData entityData;

    @Override
    public void prepare(CastContext context, ConfigurationSection parameters) {
        loot = parameters.getBoolean("loot", false);
        force = parameters.getBoolean("force", false);
        setTarget = parameters.getBoolean("set_target", false);
        repeatRandomize = parameters.getBoolean("repeat_random", true);
        speed = parameters.getDouble("speed", 0);
        direction = ConfigurationUtils.getVector(parameters, "direction");
        dyOffset = parameters.getDouble("dy_offset", 0);

        if (parameters.contains("type"))
        {
            String mobType = parameters.getString("type");
            entityData = context.getController().getMob(mobType);
            if (entityData == null) {
                entityData = new com.elmakers.mine.bukkit.entity.EntityData(context.getController(), parameters);
            }
        }

        if (parameters.contains("reason"))
        {
            String reasonText = parameters.getString("reason").toUpperCase();
            try {
                spawnReason = CreatureSpawnEvent.SpawnReason.valueOf(reasonText);
            } catch (Exception ex) {
                spawnReason = CreatureSpawnEvent.SpawnReason.EGG;
            }
        }
    }

    @Override
    public SpellResult perform(CastContext context) {
        Block targetBlock = context.getTargetBlock();

        targetBlock = targetBlock.getRelative(BlockFace.UP);

        Location spawnLocation = targetBlock.getLocation();
        Location sourceLocation = context.getLocation();
        spawnLocation.setPitch(sourceLocation.getPitch());
        spawnLocation.setYaw(sourceLocation.getYaw());

        MageController controller = context.getController();
        if (entityTypeProbability != null && !entityTypeProbability.isEmpty())
        {
            if (repeatRandomize || entityData == null)
            {
                String randomType = RandomUtils.weightedRandom(entityTypeProbability);
                try {
                    entityData = controller.getMob(randomType);
                    if (entityData == null) {
                        entityData = new com.elmakers.mine.bukkit.entity.EntityData(EntityType.valueOf(randomType.toUpperCase()));
                    }
                } catch (Throwable ex) {
                    entityData = null;
                }
            }
        }
        if (entityData == null)
        {
            return SpellResult.FAIL;
        }

        if (force) {
            controller.setForceSpawn(true);
        }
        Entity spawnedEntity = null;
        try {
            spawnedEntity = entityData.spawn(context.getController(), spawnLocation, spawnReason);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Special check to assign ownership
        if (spawnedEntity instanceof AreaEffectCloud) {
            ((AreaEffectCloud)spawnedEntity).setSource(context.getLivingEntity());
        } else if (spawnedEntity instanceof Projectile) {
            ((Projectile)spawnedEntity).setShooter(context.getLivingEntity());
        }

        if (force) {
            controller.setForceSpawn(false);
        }

        if (spawnedEntity == null) {
            return SpellResult.FAIL;
        }

        if (!loot)
        {
            spawnedEntity.setMetadata("nodrops", new FixedMetadataValue(controller.getPlugin(), true));
        }
        if (speed > 0)
        {
            Vector motion = direction;
            if (motion == null)
            {
                motion = context.getDirection();
            }
            else
            {
                motion = motion.clone();
            }

            if (dyOffset != 0) {
                motion.setY(motion.getY() + dyOffset);
            }
            motion.normalize();
            motion.multiply(speed);
            CompatibilityUtils.setEntityMotion(spawnedEntity, motion);
        }

        Collection<EffectPlayer> projectileEffects = context.getEffects("spawned");
        for (EffectPlayer effectPlayer : projectileEffects) {
            effectPlayer.start(spawnedEntity.getLocation(), spawnedEntity, null, null);
        }
        context.registerForUndo(spawnedEntity);

        if (setTarget)
        {
            context.setTargetEntity(spawnedEntity);
        }
        return SpellResult.CAST;

    }

    @Override
    public void initialize(Spell spell, ConfigurationSection parameters)
    {
        super.initialize(spell, parameters);

        if (parameters.contains("entity_types"))
        {
            entityTypeProbability = new ArrayDeque<>();
            RandomUtils.populateStringProbabilityMap(entityTypeProbability, ConfigurationUtils.getConfigurationSection(parameters, "entity_types"), 0, 0, 0);
        }
    }

    @Override
    public boolean requiresTarget() {
        return true;
    }

    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    public void getParameterNames(Spell spell, Collection<String> parameters) {
        super.getParameterNames(spell, parameters);
        parameters.add("loot");
        parameters.add("baby");
        parameters.add("name");
        parameters.add("type");
        parameters.add("speed");
        parameters.add("reason");
        parameters.add("villager_profession");
        parameters.add("ocelot_type");
        parameters.add("rabbit_type");
        parameters.add("horse_style");
        parameters.add("horse_color");
        parameters.add("color");
        parameters.add("repeat_random");
    }

    @Override
    public void getParameterOptions(Spell spell, String parameterKey, Collection<String> examples) {
        if (parameterKey.equals("type")) {
            for (EntityType type : EntityType.values()) {
                examples.add(type.name().toLowerCase());
            }
        } else if (parameterKey.equals("reason")) {
            for (CreatureSpawnEvent.SpawnReason type : CreatureSpawnEvent.SpawnReason.values()) {
                examples.add(type.name().toLowerCase());
            }
        } else if (parameterKey.equals("ocelot_type")) {
            for (Ocelot.Type type : Ocelot.Type.values()) {
                examples.add(type.name().toLowerCase());
            }
        } else if (parameterKey.equals("villager_profession")) {
            for (Villager.Profession profession : Villager.Profession.values()) {
                examples.add(profession.name().toLowerCase());
            }
        } else if (parameterKey.equals("rabbit_type")) {
            for (Rabbit.Type type : Rabbit.Type.values()) {
                examples.add(type.name().toLowerCase());
            }
        } else if (parameterKey.equals("horse_style")) {
            for (Horse.Style type : Horse.Style.values()) {
                examples.add(type.name().toLowerCase());
            }
        } else if (parameterKey.equals("horse_color")) {
            for (Horse.Color type : Horse.Color.values()) {
                examples.add(type.name().toLowerCase());
            }
        } else if (parameterKey.equals("color")) {
            for (DyeColor type : DyeColor.values()) {
                examples.add(type.name().toLowerCase());
            }
        } else if (parameterKey.equals("loot") || parameterKey.equals("baby") || parameterKey.equals("repeat_random")) {
            examples.addAll(Arrays.asList((BaseSpell.EXAMPLE_BOOLEANS)));
        } else if (parameterKey.equals("name")) {
            examples.add("Philbert");
        } else if (parameterKey.equals("speed")) {
            examples.addAll(Arrays.asList((BaseSpell.EXAMPLE_SIZES)));
        } else {
            super.getParameterOptions(spell, parameterKey, examples);
        }
    }
}
