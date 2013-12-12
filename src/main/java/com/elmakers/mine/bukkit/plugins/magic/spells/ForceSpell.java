package com.elmakers.mine.bukkit.plugins.magic.spells;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import com.elmakers.mine.bukkit.plugins.magic.Spell;
import com.elmakers.mine.bukkit.plugins.magic.SpellResult;
import com.elmakers.mine.bukkit.plugins.magic.Target;
import com.elmakers.mine.bukkit.utilities.InventoryUtils;
import com.elmakers.mine.bukkit.utilities.borrowed.ConfigurationNode;

public class ForceSpell extends Spell
{
	int magnitude = 3;
	LivingEntity targetEntity = null;
	int effectColor = 0;
	
	@Override
	public SpellResult onCast(ConfigurationNode parameters) 
	{
		if (targetEntity != null)
		{
			if (targetEntity instanceof LivingEntity)
			{
				if (!targetEntity.isValid() || targetEntity.isDead())
				{
					releaseTarget();
				}
				if (targetEntity != null && player.getLocation().distanceSquared(targetEntity.getLocation()) > getMaxRangeSquared())
				{
					releaseTarget();
				}
			}
		}
		
		if (targetEntity == null) {
			targetEntity(LivingEntity.class);
			Target target = getTarget();

			if (target == null || !target.hasTarget() || !target.isEntity() || !(target.getEntity() instanceof LivingEntity))
			{
				targetEntity = null;
				return SpellResult.NO_TARGET;
			}
			
			targetEntity = (LivingEntity)target.getEntity();
			if (effectColor != 0) {
				InventoryUtils.addPotionEffect(targetEntity, effectColor);
			}
			return SpellResult.COST_FREE;
		}

		double multiplier = parameters.getDouble("size", 1);
		forceEntity(targetEntity, multiplier);
		return SpellResult.SUCCESS;
	}

	protected void forceEntity(Entity target, double multiplier)
	{
		magnitude = (int)((double)magnitude * multiplier);
		Vector forceVector = getAimVector();
		forceVector.normalize();
		forceVector.multiply(magnitude);
		target.setVelocity(forceVector);
	}
	
	protected void releaseTarget() {
		if (targetEntity != null && effectColor != 0) {
			InventoryUtils.addPotionEffect(targetEntity, 0);
		}
		targetEntity = null;
	}

	@Override
	public boolean onCancel()
	{
		if (targetEntity != null)
		{
            if 
            (
                    (targetEntity instanceof LivingEntity) 
            &&      !targetEntity.isDead() 
            &&      player.getLocation().distanceSquared(targetEntity.getLocation()) > getMaxRangeSquared()
            )
            {
                castMessage("Released target");
            }

            releaseTarget();
			return true;
		}
		
		return false;
	}

	@Override
	public void onLoad(ConfigurationNode properties)  
	{
		magnitude = properties.getInt("entity_force", magnitude);
		effectColor = Integer.parseInt(properties.getString("effect_color", "FF0000"), 16);
	}
}
