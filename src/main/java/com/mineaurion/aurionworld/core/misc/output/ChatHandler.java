package com.mineaurion.aurionworld.core.misc.output;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.util.FakePlayer;

import org.apache.commons.lang3.StringEscapeUtils;

public final class ChatHandler
{

    public static final char COLOR_FORMAT_CHARACTER = '\u00a7';

    public static EnumChatFormatting chatErrorColor, chatWarningColor, chatConfirmationColor, chatNotificationColor;

    /* ------------------------------------------------------------ */

    public static void sendMessage(ICommandSender recipient, String message)
    {
        sendMessage(recipient, new ChatComponentText(message));
    }

    public static void sendMessage(ICommandSender recipient, IChatComponent message)
    {
        if (recipient instanceof FakePlayer && ((EntityPlayerMP) recipient).playerNetServerHandler == null)
            Log.info(String.format("Fakeplayer %s: %s", recipient.getCommandSenderName(), message.getUnformattedText()));
        else
            recipient.addChatMessage(message);
    }

    public static void sendMessage(ICommandSender recipient, String message, EnumChatFormatting color)
    {
        message = formatColors(message);
        if (recipient instanceof EntityPlayer)
        {
            ChatComponentText component = new ChatComponentText(message);
            component.getChatStyle().setColor(color);
            sendMessage(recipient, component);
        }
        else
            sendMessage(recipient, stripFormatting(message));
    }

    public static void broadcast(String message)
    {
        broadcast(new ChatComponentText(message));;
    }

    public static void broadcast(IChatComponent message)
    {
        MinecraftServer.getServer().getConfigurationManager().sendChatMsg(message);
    }

    /* ------------------------------------------------------------ */

    public static IChatComponent confirmation(String message)
    {
        return setChatColor(new ChatComponentText(formatColors(message)), chatConfirmationColor);
    }

    public static IChatComponent notification(String message)
    {
        return setChatColor(new ChatComponentText(formatColors(message)), chatNotificationColor);
    }

    public static IChatComponent warning(String message)
    {
        return setChatColor(new ChatComponentText(formatColors(message)), chatWarningColor);
    }

    public static IChatComponent error(String message)
    {
        return setChatColor(new ChatComponentText(formatColors(message)), chatErrorColor);
    }

    public static IChatComponent setChatColor(IChatComponent message, EnumChatFormatting color)
    {
        message.getChatStyle().setColor(color);
        return message;
    }

    /* ------------------------------------------------------------ */

    public static void chatError(ICommandSender sender, String msg)
    {
        sendMessage(sender, msg, chatErrorColor);
    }

    public static void chatConfirmation(ICommandSender sender, String msg)
    {
        sendMessage(sender, msg, chatConfirmationColor);
    }

    public static void chatWarning(ICommandSender sender, String msg)
    {
        sendMessage(sender, msg, chatWarningColor);
    }

    public static void chatNotification(ICommandSender sender, String msg)
    {
        sendMessage(sender, msg, chatNotificationColor);
    }

    /* ------------------------------------------------------------ */

    public static String formatColors(String message)
    {
        // TODO: Improve this to replace codes less aggressively
        char[] b = message.toCharArray();
        for (int i = 0; i < b.length - 1; i++)
        {
            if (b[i] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1)
            {
                b[i] = COLOR_FORMAT_CHARACTER;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b);
    }

    public static final Pattern FORMAT_CODE_PATTERN;

    static
    {
        String codes = "";
        for (EnumChatFormatting code : EnumChatFormatting.values())
            codes += code.getFormattingCode();
        FORMAT_CODE_PATTERN = Pattern.compile(COLOR_FORMAT_CHARACTER + "(<" + codes + ">)");
    }

    public static String stripFormatting(String message)
    {
        return FORMAT_CODE_PATTERN.matcher(message).replaceAll("");
    }

    public static void applyFormatting(ChatStyle chatStyle, Collection<EnumChatFormatting> formattings)
    {
        for (EnumChatFormatting format : formattings)
            applyFormatting(chatStyle, format);
    }

    public static void applyFormatting(ChatStyle chatStyle, EnumChatFormatting formatting)
    {
        switch (formatting)
        {
            case BOLD:
                chatStyle.setBold(true);
                break;
            case ITALIC:
                chatStyle.setItalic(true);
                break;
            case OBFUSCATED:
                chatStyle.setObfuscated(true);
                break;
            case STRIKETHROUGH:
                chatStyle.setStrikethrough(true);
                break;
            case UNDERLINE:
                chatStyle.setUnderlined(true);
                break;
            case RESET:
                break;
            default:
                chatStyle.setColor(formatting);
                break;
        }
    }

    public static Collection<EnumChatFormatting> enumChatFormattings(String textFormats)
    {
        List<EnumChatFormatting> result = new ArrayList<EnumChatFormatting>();
        for (int i = 0; i < textFormats.length(); i++)
        {
            char formatChar = textFormats.charAt(i);
            for (EnumChatFormatting format : EnumChatFormatting.values())
                if (format.getFormattingCode() == formatChar)
                {
                    result.add(format);
                    break;
                }
        }
        return result;
    }

    /* ------------------------------------------------------------ */

    public static String getUnformattedMessage(IChatComponent message)
    {
        StringBuilder sb = new StringBuilder();
        for (Object msg : message)
            sb.append(((IChatComponent) msg).getUnformattedTextForChat());
        return sb.toString();
    }

    public static String getFormattedMessage(IChatComponent message)
    {
        StringBuilder sb = new StringBuilder();
        for (Object msg : message)
            sb.append(((IChatComponent) msg).getFormattedText());
        return sb.toString();
    }

    public static String formatHtml(IChatComponent message)
    {
        // TODO: HTML formatting function
        StringBuilder sb = new StringBuilder();
        for (Object msgObj : message)
        {
            IChatComponent msg = (IChatComponent) msgObj;
            ChatStyle style = msg.getChatStyle();
            if (!isStyleEmpty(style))
            {
                sb.append("<span class=\"");
                EnumChatFormatting color = style.getColor();
                if (color != null)
                {
                    sb.append(" mcf");
                    sb.append(color.getFormattingCode());
                }
                if (style.getBold())
                {
                    sb.append(" mcf");
                    sb.append(EnumChatFormatting.BOLD.getFormattingCode());
                }
                if (style.getItalic())
                {
                    sb.append(" mcf");
                    sb.append(EnumChatFormatting.ITALIC.getFormattingCode());
                }
                if (style.getUnderlined())
                {
                    sb.append(" mcf");
                    sb.append(EnumChatFormatting.UNDERLINE.getFormattingCode());
                }
                if (style.getObfuscated())
                {
                    sb.append(" mcf");
                    sb.append(EnumChatFormatting.OBFUSCATED.getFormattingCode());
                }
                if (style.getStrikethrough())
                {
                    sb.append(" mcf");
                    sb.append(EnumChatFormatting.STRIKETHROUGH.getFormattingCode());
                }
                sb.append("\">");
                sb.append(formatHtml(msg.getUnformattedTextForChat()));
                sb.append("</span>");
            }
            else
            {
                sb.append(formatHtml(msg.getUnformattedTextForChat()));
            }
        }
        return sb.toString();
    }

    public static String formatHtml(String message)
    {
        StringBuilder sb = new StringBuilder();
        int pos = 0;
        int tagCount = 0;
        Matcher matcher = FORMAT_CODE_PATTERN.matcher(message);
        while (matcher.find())
        {
            sb.append(StringEscapeUtils.escapeHtml4(message.substring(pos, matcher.start())));
            pos = matcher.end();
            char formatChar = matcher.group(1).charAt(0);
            for (EnumChatFormatting format : EnumChatFormatting.values())
            {
                if (format.getFormattingCode() == formatChar)
                {
                    sb.append("<span class=\"mcf");
                    sb.append(formatChar);
                    sb.append("\">");
                    tagCount++;
                    break;
                }
            }
        }
        sb.append(StringEscapeUtils.escapeHtml4(message.substring(pos, message.length())));
        // for (; pos < message.length(); pos++)
        // sb.append(message.charAt(pos));
        for (int i = 0; i < tagCount; i++)
            sb.append("</span>");
        return sb.toString();
    }

    public static boolean isStyleEmpty(ChatStyle style)
    {
        return !style.getBold() && !style.getItalic() && !style.getObfuscated() && !style.getStrikethrough() && !style.getUnderlined()
                && style.getColor() == null;
    }

    public static enum ChatFormat
    {

        PLAINTEXT, HTML, MINECRAFT, DETAIL;

        public Object format(IChatComponent message)
        {
            switch (this)
            {
                case HTML:
                    return ChatHandler.formatHtml(message);
                case MINECRAFT:
                    return ChatHandler.getFormattedMessage(message);
                case DETAIL:
                    return message;
                default:
                case PLAINTEXT:
                    return ChatHandler.stripFormatting(ChatHandler.getUnformattedMessage(message));
            }
        }

        public static ChatFormat fromString(String format)
        {
            try
            {
                return ChatFormat.valueOf(format.toUpperCase());
            }
            catch (IllegalArgumentException e)
            {
                return ChatFormat.PLAINTEXT;
            }
        }

    }

    /* ------------------------------------------------------------ */

    public static String formatTimeDurationReadable(long time, boolean showSeconds)
    {
        int weeks = (int) (TimeUnit.SECONDS.toDays(time) / 7);
        int days = (int) (TimeUnit.SECONDS.toDays(time) - 7 * weeks);
        long hours = TimeUnit.SECONDS.toHours(time) - (TimeUnit.SECONDS.toDays(time) * 24);
        long minutes = TimeUnit.SECONDS.toMinutes(time) - (TimeUnit.SECONDS.toHours(time) * 60);
        long seconds = TimeUnit.SECONDS.toSeconds(time) - (TimeUnit.SECONDS.toMinutes(time) * 60);

        StringBuilder sb = new StringBuilder();
        if (weeks != 0)
            sb.append(String.format("%d weeks ", weeks));
        if (days != 0)
        {
            if (sb.length() > 0)
                sb.append(", ");
            sb.append(String.format("%d days ", days));
        }
        if (hours != 0)
        {
            if (sb.length() > 0)
                sb.append(", ");
            sb.append(String.format("%d hours ", hours));
        }
        if (minutes != 0 || !showSeconds)
        {
            if (sb.length() > 0)
                if (!showSeconds)
                    sb.append("and ");
                else
                    sb.append(", ");
            sb.append(String.format("%d minutes ", minutes));
        }
        if (showSeconds)
        {
            if (sb.length() > 0)
                sb.append("and ");
            sb.append(String.format("%d seconds ", seconds));
        }

        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    public static String formatTimeDurationReadableMilli(long time, boolean showSeconds)
    {
        return formatTimeDurationReadable(time / 1000, showSeconds);
    }

    /* ------------------------------------------------------------ */

    public static void setConfirmationColor(String color)
    {
        chatConfirmationColor = EnumChatFormatting.getValueByName(color);
        if (chatConfirmationColor == null)
            chatConfirmationColor = EnumChatFormatting.GREEN;
    }

    public static void setErrorColor(String color)
    {
        chatErrorColor = EnumChatFormatting.getValueByName(color);
        if (chatErrorColor == null)
            chatErrorColor = EnumChatFormatting.RED;
    }

    public static void setNotificationColor(String color)
    {
        chatNotificationColor = EnumChatFormatting.getValueByName(color);
        if (chatNotificationColor == null)
            chatNotificationColor = EnumChatFormatting.AQUA;
    }

    public static void setWarningColor(String color)
    {
        chatWarningColor = EnumChatFormatting.getValueByName(color);
        if (chatWarningColor == null)
            chatWarningColor = EnumChatFormatting.YELLOW;
    }
}