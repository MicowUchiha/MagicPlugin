package com.elmakers.mine.bukkit.api.block;

import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import com.elmakers.mine.bukkit.api.entity.EntityData;
import com.elmakers.mine.bukkit.api.magic.Mage;
import org.bukkit.util.Vector;

public interface MaterialBrush extends MaterialAndData {
    void prepare();
    boolean isReady();
    Collection<EntityData> getEntities();
    Collection<Entity> getTargetEntities();
    boolean hasEntities();
    boolean update(final Mage mage, final Location location);
    void update(String activeMaterial);
    void activate(final Location location, final String material);
    void setTarget(Location target);
    void setTarget(Location target, Location center);
    Vector getSize();
    BrushMode getMode();
    boolean isEraseModifierActive();
    boolean isErase();
}
