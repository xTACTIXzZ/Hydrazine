package com.github.hydrazine.util;

import java.net.InetSocketAddress;
import java.net.Proxy;

import com.github.hydrazine.Hydrazine;
import com.github.hydrazine.minecraft.Server;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.event.session.ConnectedEvent;
import com.github.steveice10.packetlib.event.session.DisconnectedEvent;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;

/**
 * 
 * @author xTACTIXzZ
 * 
 * This class helps connecting a client to a server. It respects the user-specified settings.
 *
 */
public class ConnectionHelper 
{
	
	public ConnectionHelper()
	{
		
	}
	
	/**
	 * Registers default listeners to a client
	 */
	public static void registerDefaultListeners(Client client)
	{
		client.getSession().addListener(new SessionAdapter() 
		{
            @Override
            public void packetReceived(PacketReceivedEvent event) 
            {
                if(event.getPacket() instanceof ServerJoinGamePacket) 
                {
                     System.out.println(Hydrazine.infoPrefix + ((MinecraftProtocol) client.getPacketProtocol()).getProfile().getName() + " joined the game!");
                }
            }
            
            @Override
            public void connected(ConnectedEvent event)
            {
                System.out.println(Hydrazine.infoPrefix + ((MinecraftProtocol) client.getPacketProtocol()).getProfile().getName() + " connected to the server!");
            }

            @Override
            public void disconnected(DisconnectedEvent event) 
            {
            	System.out.println(Hydrazine.infoPrefix + "Client disconnected: " + event.getReason());           	
            }
        });
	}
	
	/**
	 * Connects a client to a server
	 */
	public static Client connect(MinecraftProtocol protocol, Server server)
	{
		// Check if authenticated successfully
		if(protocol == null)
		{
			return null;
		}
		
		// Check if socks proxy should be used
		if(Hydrazine.settings.hasSetting("socksproxy"))
		{
			Proxy proxy = null;
			
			try
			{
				String[] parts = Hydrazine.settings.getSetting("socksproxy").split(":");
				proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(parts[0], Integer.parseInt(parts[1])));
			}
			catch(Exception e)
			{
				System.out.println(Hydrazine.errorPrefix + "Invalid value for switch -sp");
				
				return null;
			}
			
			Client client = new Client(server.getHost(), server.getPort(), protocol, new TcpSessionFactory(proxy));
			
			registerDefaultListeners(client);
						
			client.getSession().connect();
			
			return client;
		}
		else
		{
			Client client = new Client(server.getHost(), server.getPort(), protocol, new TcpSessionFactory());
			
			registerDefaultListeners(client);
						
			client.getSession().connect();
			
			return client;
		}
	}

}