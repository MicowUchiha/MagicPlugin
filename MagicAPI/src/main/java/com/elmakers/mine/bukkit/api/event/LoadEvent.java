package com.elmakers.mine.bukkit.api.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.elmakers.mine.bukkit.api.attributes.AttributeProvider;
import com.elmakers.mine.bukkit.api.entity.TeamProvider;
import com.elmakers.mine.bukkit.api.magic.MageController;
import com.elmakers.mine.bukkit.api.requirements.RequirementsProcessor;

/**
 * A custom event that fires whenever Magic loads or reloads configurations.
 */
public class LoadEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private MageController controller;
    private List<AttributeProvider> attributeProviders = new ArrayList<>();
    private List<TeamProvider> teamProviders = new ArrayList<>();
    private Map<String, RequirementsProcessor> requirementProcessors = new HashMap<>();

    public LoadEvent(MageController controller) {
        this.controller = controller;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public MageController getController() {
        return controller;
    }

    /**
     * Register an AttributeProvider, for adding custom attribute support to spells and mages.
     *
     * @param provider The provider to add.
     */
    public void registerAttributeProvider(AttributeProvider provider) {
        attributeProviders.add(provider);
    }

    /**
     * Register a TeamProvider, to be able to make decisions about who players and mobs can target.
     *
     * @param provider The provider to add.
     */
    public void registerTeamProvider(TeamProvider provider) {
        teamProviders.add(provider);
    }

    /**
     * Register a RequirementsProcessor for handling a specific type of requirement.
     *
     * <p>Requirement types are 1:1 with processors, each type may only have one processor associated with it.
     *
     * <p>Processors must be re-registered with each load.
     *
     * <p>Example requirement block, which might appear in a spell, Selector or other config:
     *
     * <code>
     * requirements:
     * - type: skillapi
     *   skill: enchanting
     * - type: avengers
     *   power: hulkout
     *   character: Hulk
     * </code>
     *
     * @param requirementType The type of requirements this processor handles
     * @param processor The processor to register
     */
    public void registerRequirementsProcessor(String requirementType, RequirementsProcessor processor) {
        if (requirementProcessors.containsKey(requirementType)) {
            controller.getLogger().warning("Tried to register RequiremensProcessor twice for same type: " + requirementType);
        }
        requirementProcessors.put(requirementType, processor);
    }

    public Map<String, RequirementsProcessor> getRequirementProcessors() {
        return requirementProcessors;
    }

    public Collection<AttributeProvider> getAttributeProviders() {
        return attributeProviders;
    }

    public Collection<TeamProvider> getTeamProviders() {
        return teamProviders;
    }
}
