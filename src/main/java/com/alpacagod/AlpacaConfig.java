package com.alpacagod;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("alpacagod")
public interface AlpacaConfig extends Config
{
    @ConfigSection(
            name = "Statistics & Control",
            description = "Settings for pet count and reset.",
            position = 0
    )
    String statsSection = "statsSection";

    @ConfigItem(
            keyName = "petCount",
            name = "Pet Count",
            description = "Total number of times an alpaca has been petted.",
            hidden = true
    )
    default int petCount()
    {
        return 0;
    }


    @ConfigItem(
            keyName = "currentPetCountDisplay",
            name = "Total Pets",
            description = "Displays the total number of times you've petted an alpaca.",
            position = 1,
            section = "statsSection"
    )
    default String currentPetCountDisplay()
    {
        return "0";
    }

    @ConfigItem(
            keyName = "resetPetCount",
            name = "Reset Pet Count",
            description = "Resets your total alpaca pet count to 0.",
            position = 2,
            section = "statsSection"
    )
    default boolean resetPetCount()
    {
        return false;
    }
}
