package com.backtobedrock.LitePlaytimeRewards.helperClasses;

import com.backtobedrock.LitePlaytimeRewards.LitePlaytimeRewardsCommands;
import java.util.List;
import java.util.TreeMap;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RewardsGUI {

    private final Inventory GUI;
    private final TreeMap<String, RewardsGUIReward> GUIRewards = new TreeMap<>();

    public RewardsGUI(TreeMap<String, ?> rewards) {
        RewardsGUICustomHolder holder = (rewards.firstEntry().getValue() instanceof Reward
                ? new RewardsGUICustomHolder((int) (Math.ceil(((double) rewards.size()) / 9) * 9), "Your Rewards")
                : new RewardsGUICustomHolder((int) (Math.ceil(((double) rewards.size()) / 9) * 9), "Available Rewards"));
        this.GUI = holder.getInventory();
        this.initializeGUI(rewards);
    }

    private void initializeGUI(TreeMap<String, ?> rewards) {
        rewards.entrySet().stream().forEach(e -> {
            this.GUI.addItem(this.createGUIItem(e.getKey(), e.getValue()));
            this.GUIRewards.put(e.getKey(), new RewardsGUIReward());
        });
    }

    private ItemStack createGUIItem(String name, Object reward) {
        ItemStack item = new ItemStack(((ConfigReward) reward).getDisplayItem());
        ItemMeta im = item.getItemMeta();
        if (im != null) {
            im.setDisplayName(((ConfigReward) reward).getDisplayName());
            im.setLocalizedName(name);
            List<String> description = ((ConfigReward) reward).getDisplayDescription();
            if (description.size() > 0) {
                description.add(0, "§f----");
            }
            if (reward instanceof Reward) {
                description.add(0, String.format("§a(redeemed: §2%d§a, pending: §2%s§a)", ((Reward) reward).getAmountRedeemed(), ((Reward) reward).getAmountPending()));
                description.add("§f----");
                Long nextReward = ((Reward) reward).getTimeTillNextReward().get(0);
                description.add("§eNext reward in: §6" + (nextReward > -1L ? LitePlaytimeRewardsCommands.timeToString(nextReward) : "never"));
            } else {
                description.add(0, String.format("§a(amount: §2%d§a, broadcast: §2%s§a)", 1, false));
                description.add("§f----");
                description.add("§6§oRight click §e§oto toggle broadcasting");
                description.add("§6§oShift left click §e§oto increase amount");
                description.add("§6§oShift right click §e§oto decrease amount");
            }
            im.setLore(description);
            item.setItemMeta(im);
        }
        return item;
    }

    public void updateGUIItem(ItemStack item, RewardsGUIReward reward) {
        ItemStack newItem = new ItemStack(item);
        ItemMeta im = newItem.getItemMeta();
        if (im != null && im.hasLore()) {
            List<String> lore = im.getLore();
            lore.set(0, String.format("§a(amount: §2%d§a, broadcast: §2%s§a)", reward.getAmount(), reward.isBroadcast()));
            im.setLore(lore);
            newItem.setItemMeta(im);
        }
        this.GUI.setItem(this.GUI.first(item), newItem);
    }

    public Inventory getGUI() {
        return GUI;
    }

    public TreeMap<String, RewardsGUIReward> getGUIRewards() {
        return GUIRewards;
    }
}