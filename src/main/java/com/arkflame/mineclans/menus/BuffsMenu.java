package com.arkflame.mineclans.menus;

import java.util.Collection;

import com.arkflame.mineclans.buff.Buff;
import com.arkflame.mineclans.menus.items.BuffItem;
import com.arkflame.mineclans.modernlib.menus.Menu;
import com.arkflame.mineclans.utils.Materials;

public class BuffsMenu extends Menu {

    public BuffsMenu(Collection<Buff> buffs) {
        super("Buffs", 5);

        // Set buffs in the menu
        for (Buff buff : buffs) {
            setItem(buff.getSlot(), new BuffItem(buff));
        }

        // Set background
        setBackground(Materials.get("STAINED_GLASS_PANE", "GRAY_STAINED_GLASS_PANE"), (short) 7, " ");
    }
    
}
