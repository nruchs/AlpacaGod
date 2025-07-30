package com.alpacagod;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.NPC;
import net.runelite.client.util.Text;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.api.events.GameTick;

@Slf4j
@PluginDescriptor(
        name = "Pet The Alpaca",
        description = "Allows you to pet adorable alpacas.",
        tags = {"alpaca", "fun", "meme", "pet"}
)
public class AlpacaPlugin extends Plugin
{
    @Inject
    private Client client;

    @Inject
    private AlpacaConfig config;

    @Inject
    private ConfigManager configManager;

    @Inject
    private ChatMessageManager chatMessageManager;

    private static final String[] COMMON_MESSAGES = {
            "You pet the alpaca. It hums happily.",
            "The alpaca seems to enjoy the petting.",
            "You gently stroke the alpaca's fur. So fluffy!",
            "The alpaca blinks slowly. A sign of trust.",
            "The alpaca stares into your soul.",
            "A soft hum comes from the alpaca. It likes you.",
            "The alpaca pretends you don't exist.",
            "You are judged silently by the alpaca.",
            "You feel a warm alpaca aura surround you.",
            "A sparkle appears in the alpaca’s eye.",
            "You have unlocked the alpaca’s trust.",
            "The alpaca moonwalks away dramatically.",
            "The alpaca attempts to lick your elbow.",
            "The alpaca breaks the fourth wall."
    };

    private static final String[] RARE_MESSAGES = {
            "The alpaca points to the sky. You see nothing.",
            "The alpaca enters stealth mode. You can still see it.",
            "The alpaca reveals an ancient map. It's blank."
    };

    private static final String[] INSTIGATING_MESSAGES = {
            "Look, an alpaca! Maybe I should pet it...",
            "That alpaca looks friendly. Does it like cuddles?",
            "This alpaca needs a good pet. Don't you think?"
    };

    private static final int MESSAGE_COOLDOWN_TICKS = 50;
    private static final int DETECTION_RANGE_TILES = 10;
    private static final int OVERHEAD_TEXT_CYCLE = 75;
    private int petCount = 0;
    private int lastMessageTick = -MESSAGE_COOLDOWN_TICKS;

    @Override
    protected void startUp() throws Exception
    {
        petCount = config.petCount();
        log.info("Pet The Alpaca started! Loaded petCount = {}", petCount);
    }

    @Override
    protected void shutDown() throws Exception
    {
        savePetCount();
        log.info("Pet The Alpaca stopped! Saved petCount = {}", petCount);
    }

    private void savePetCount()
    {
        configManager.setConfiguration("alpacagod", "petCount", petCount);
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event)
    {
        String target = Text.removeTags(event.getTarget());

        if (target.equalsIgnoreCase("Alpaca") || target.equalsIgnoreCase("Alpaca cria"))
        {
            boolean petOptionExists = false;
            for (MenuEntry entry : client.getMenuEntries())
            {
                if (entry.getOption().equals("Pet") && (Text.removeTags(entry.getTarget()).equalsIgnoreCase("Alpaca") || Text.removeTags(entry.getTarget()).equalsIgnoreCase("Alpaca cria")))
                {
                    petOptionExists = true;
                    break;
                }
            }

            if (!petOptionExists)
            {
                client.createMenuEntry(-1)
                        .setOption("Pet")
                        .setTarget(event.getTarget())
                        .setType(MenuAction.RUNELITE)
                        .onClick(e -> petAlpaca());
            }
        }
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {
        if (client.getLocalPlayer() == null)
        {
            return;
        }

        if (client.getTickCount() - lastMessageTick < MESSAGE_COOLDOWN_TICKS)
        {
            return;
        }

        for (NPC npc : client.getNpcs())
        {
            if (npc.getName() != null && (npc.getName().equalsIgnoreCase("Alpaca") || npc.getName().equalsIgnoreCase("Alpaca cria")))
            {
                if (client.getLocalPlayer().getWorldLocation().distanceTo(npc.getWorldLocation()) <= DETECTION_RANGE_TILES)
                {
                    String message = INSTIGATING_MESSAGES[(int) (Math.random() * INSTIGATING_MESSAGES.length)];

                    client.getLocalPlayer().setOverheadText(message);
                    client.getLocalPlayer().setOverheadCycle(OVERHEAD_TEXT_CYCLE);

                    lastMessageTick = client.getTickCount();
                    return;
                }
            }
        }
    }

    private String getRandomMessage()
    {
        double chance = Math.random();

        if (chance < 0.8)
        {
            int index = (int) (Math.random() * COMMON_MESSAGES.length);
            return COMMON_MESSAGES[index];
        }
        else
        {
            int index = (int) (Math.random() * RARE_MESSAGES.length);
            return RARE_MESSAGES[index];
        }
    }

    private void petAlpaca()
    {
        petCount++;

        if (client.getLocalPlayer() != null) {
            client.getLocalPlayer().setAnimation(827);
            client.getLocalPlayer().setActionFrame(0);
        }

        String message;

        switch (petCount)
        {
            case 1:
                message = "You get a face full of alpaca spit.";
                break;
            case 3:
                message = "The alpaca allows it. Barely.";
                break;
            case 5:
                message = "The alpaca hums happily.";
                break;
            case 100:
                message = "You feel an ancient bond with the alpaca.";
                break;
            default:
                message = String.format("%s (Total pets: %d)", getRandomMessage(), petCount);
                break;
        }

        chatMessageManager.queue(QueuedMessage.builder()
                .type(ChatMessageType.GAMEMESSAGE)
                .runeLiteFormattedMessage(message)
                .build());

    }

    @Provides
    AlpacaConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(AlpacaConfig.class);
    }
}
