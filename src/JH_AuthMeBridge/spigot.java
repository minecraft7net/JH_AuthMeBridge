package JH_AuthMeBridge;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import fr.xephi.authme.events.LoginEvent;

public class spigot extends JavaPlugin implements Listener, PluginMessageListener {

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this,  this);
		Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		Bukkit.getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
	}
	
	@EventHandler
	public void onLogin(LoginEvent event){		
        try {
        	ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            out.writeUTF("JH_AuthMeBridge");
            out.writeUTF(event.getPlayer().getName());
            Bukkit.getServer().sendPluginMessage(this, "BungeeCord", b.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }       
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event){
		if(isLogged(event.getPlayer())){
			logados.remove(event.getPlayer().getName());
		}
	}
	
	@Override
	public void onPluginMessageReceived(String channel, Player p, byte[] message) {		
        try {
        	DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
            String subchannel = in.readUTF();
            if(subchannel.equals("JH_AuthMeBridge")){
                String input = in.readUTF();    
                if(isLogged(input))return;
                Player player = Bukkit.getPlayer(input);
                if(player != null && player.isOnline()){               	
                	new BukkitRunnable()
            	    {
            			public void run()
            			{
							// For older versions of Authme
            				// fr.xephi.authme.api.API.forceLogin(player);
            				fr.xephi.authme.api.v3.AuthMeApi.getInstance().forceLogin(player);
            				logados.add(player.getName());
            			}
            	    }.runTaskLater(this, 20L);               	
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	List<String> logados = new ArrayList<>();
    
    public Boolean isLogged(Player player){
    	return logados.contains(player.getName());
    }
    
    public Boolean isLogged(String player){
    	return logados.contains(player);
    }
}
