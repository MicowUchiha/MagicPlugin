package com.elmakers.mine.bukkit.magic;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.configuration.ConfigurationSection;

import com.elmakers.mine.bukkit.api.magic.MageController;

public class MageClassTemplate extends BaseMagicProperties implements com.elmakers.mine.bukkit.api.magic.MageClassTemplate {
    private MageClassTemplate parent;
    private final @Nonnull String key;
    private boolean isLocked = false;
    private String name;
    private String description;

    public MageClassTemplate(@Nonnull MageController controller, @Nonnull String key, @Nonnull ConfigurationSection configuration) {
        super(controller);
        checkNotNull(key, "key");
        checkNotNull(configuration, "configuration");

        this.key = key;
        this.load(configuration);

        isLocked = getProperty("locked", false);

        // Clear properties we don't want to pass along
        clearProperty("locked");
        clearProperty("parent");
        clearProperty("path_start");
        clearProperty("hidden");
        clearProperty("enabled");
        clearProperty("inherit");

        name = controller.getMessages().get("classes." + key + ".name", "");
        description = controller.getMessages().get("classes." + key + ".description", "");

        name = configuration.getString("name", name);
        description = configuration.getString("description", description);
    }

    public @Nonnull String getKey() {
        return key;
    }

    public @Nullable MageClassTemplate getParent() {
        return parent;
    }

    public void setParent(@Nullable  MageClassTemplate parent) {
        this.parent = parent;
    }

    public boolean hasParent() {
        return parent != null;
    }

    public boolean isLocked() {
        if (isLocked) return true;
        if (parent != null) return parent.isLocked();
        return false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
