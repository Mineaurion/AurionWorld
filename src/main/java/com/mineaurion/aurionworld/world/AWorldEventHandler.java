package com.mineaurion.aurionworld.world;

import com.mineaurion.aurionworld.AurionWorld;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.minecraftforge.common.network.ForgeMessage.DimensionRegisterMessage;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkHandshakeEstablished;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;

/**
 *
 * @author gnif
 */
public class AWorldEventHandler extends ChannelInboundHandlerAdapter {

    private AWorldManager manager;

    public AWorldEventHandler(AWorldManager aw) {
        super();

        this.manager = aw;
        NetworkRegistry.INSTANCE.getChannel("FORGE", Side.SERVER).pipeline().addFirst("MultiworldEventHandler", this);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof NetworkHandshakeEstablished) {
            NetworkHandshakeEstablished event = (NetworkHandshakeEstablished) evt;

            // REPLY does not work, see https://github.com/MinecraftForge/FML/issues/360
            FMLEmbeddedChannel channel = NetworkRegistry.INSTANCE.getChannel("FORGE", Side.SERVER);
            channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DISPATCHER);
            channel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(event.dispatcher);

            for (AWorld world : manager.getWorlds())
                channel.writeOutbound(new DimensionRegisterMessage(world.dimensionId, world.providerId));
        }

        ctx.fireUserEventTriggered(evt);
    }
}
