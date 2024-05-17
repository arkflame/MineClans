package com.arkflame.mineclans.tasks;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import com.arkflame.mineclans.MineClans;
import com.arkflame.mineclans.models.Faction;
import com.arkflame.mineclans.modernlib.tasks.ModernTask;

public class InventorySaveTask extends ModernTask {
    private Collection<Faction> toSave = ConcurrentHashMap.newKeySet();

    public InventorySaveTask() {
        super(MineClans.getInstance(), 20L, true);
    }

    public void save(Faction faction) {
        toSave.add(faction);
    }

    @Override
    public void run() {
        Iterator<Faction> iterator = toSave.iterator();

        while (iterator.hasNext()) {
            Faction faction = iterator.next();
            iterator.remove();
            MineClans.getInstance().getMySQLProvider().getChestDAO().saveFactionChest(faction.getId(),
                    faction.getInventory());
        }
    }
}
