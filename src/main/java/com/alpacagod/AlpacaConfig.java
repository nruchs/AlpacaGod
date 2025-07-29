package com.alpacagod;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("alpacagod")
public interface AlpacaConfig extends Config {
    @ConfigItem(
            keyName = "petCount",
            name = "Pet Count",
            description = "How many times you have petted the alpaca.",
            hidden = true
    )
    default int petCount() {
        return 0;
    }
}
