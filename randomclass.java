/* this class is just an example of the server searcher from play.craftshark.net */
public class listener {

// message event listener

	@EventListener
    public void onMessage(MessageEvent event) {
    	String[] data = null;
    	if(event.getChannel().equalsIgnoreCase("hubAPI")) {
    		System.out.println("got something on channcel hubAPI");
    		try { 
    			data = event.getMessageAsString().split(",,"); 
    		} catch (UnsupportedEncodingException e) { 
    			e.printStackTrace();
    			return; 
    		}
    		
    		String username = data[0];
    		String status = data[1];
    		
    		try {
				System.out.println("This was in the message " + event.getMessageAsString());
			} catch (UnsupportedEncodingException e) {
			}
    		
    		String string = username + "," + status;
    		
    		if(servers.contains(username + "," + "open")){
    			servers.remove(username + "," + "open");
    		}
    		
    		if(servers.contains(username + "," + "restarting")){
    			servers.remove(username + "," + "restarting");
    		}
    		
    		if(servers.contains(username + "," + "ingame")){
    			servers.remove(username + "," + "ingame");
    		}
    		
    		if(servers.contains(username + "," + "full")){
    			servers.remove(username + "," + "full");
    		}
    		
    		servers.add(string);
    	}
    }

}


public class fromminigame {
// you should use this method in your minigame plugin.
	public void sendinfo(String status) {
			String serverUsername = connect.getSettings().getUsername();
			int online = Bukkit.getOnlinePlayers().length;
			int max = Bukkit.getMaxPlayers();
			
			MessageRequest request;
			try {
				request = new MessageRequest(Collections.<String> emptyList(), 
						"hubAPI", serverUsername + ",," + status + ",," + online + ",," + max);
				try {
					connect.request(request);
				} catch (RequestException e) {
					e.printStackTrace();
				} 
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			System.out.println("sendinfo of status == " + status);
	}
}

public class playerlistener {
/* you would use this class in your hub side, same plugin as the event listener. */

  public List<String> moveAPI = new ArrayList<String>();
  
	@EventHandler
	public void ServerLookUp( PlayerMoveEvent e ) {
		Player p = e.getPlayer();
		
		// skywars
		if(e.getPlayer().getLocation().subtract(0,1,0).getBlock().getType().equals(Material.DIAMOND_BLOCK) && !moveAPI.contains(p.getName())) {
			// if steps on Diamond Block and not in moveAPI list.
			skywarsSearch(p);
		} 
	}
	
	public void skywarsSearch(final Player p) {
		p.setVelocity(new Vector(0,7,0));
		p.sendMessage(prefix + ChatColor.YELLOW + " Searching for open servers...");
		moveAPI.add(p.getName());
		removefromList(p.getName());
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				for ( String string : m.servers ) {
					
					String[] s = string.split(",");
					
					String username = s[0];
					String status = s[1];
					//String status = "open";
					
					System.out.println(username + " and its status is " + status);
					
					if(username.contains("SW-00")) {
						System.out.println("listed all in arraylist");
						if("open" == status) {
							System.out.println("found a server");
							
							p.sendMessage(prefix + ChatColor.GREEN + " Found server " + ChatColor.GOLD + username + ChatColor.GREEN + "!");
						    try { 
						    	m.connect.request(new RedirectRequest(username, p.getName()));
						      } catch (RequestException ev) { 
						    	  p.sendMessage(prefix + ChatColor.GREEN + " Seems to be a error, Please try again!");
						    	  p.teleport(minigameSpawn());
						      }
						    return;
							
						}
					}
				}
				p.sendMessage(prefix + ChatColor.YELLOW + " Sorry there is no open " + ChatColor.RED + "SkyWars" + ChatColor.YELLOW + " servers!");
				p.sendMessage(prefix + ChatColor.YELLOW + " Please try again!");
				p.teleport(minigameSpawn());
				
			}
		}.runTaskLater(m, 40);
	}
	
	public void removefromList(final String player) {
		new BukkitRunnable() {
			
			@Override
			public void run() {
				moveAPI.remove(player);
			}
		}.runTaskLater(m, 60);
	}

}
