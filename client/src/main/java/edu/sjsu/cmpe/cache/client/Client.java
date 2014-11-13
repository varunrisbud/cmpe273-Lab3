package edu.sjsu.cmpe.cache.client;


import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {

    public static void main(String[] args) throws Exception {
	String[] values = new String[] {"a","b","c","d","e","f","g","h", "i", "j"};
        System.out.println("Starting Cache Client...");
        DistributedCacheService cache1 = new DistributedCacheService(
                "http://localhost:3000");
        DistributedCacheService cache2 = new DistributedCacheService(
                "http://localhost:3001");
        DistributedCacheService cache3 = new DistributedCacheService(
                "http://localhost:3002");

	List<DistributedCacheService> nodeList = new ArrayList<DistributedCacheService>();
        nodeList.add(cache1);
        nodeList.add(cache2);
        nodeList.add(cache3);
	try {
            ConsistentHash ch = new ConsistentHash(nodeList, 3);

		for(int i=1; i<=10 ; i++)
		{
			    CacheServiceInterface cache = (CacheServiceInterface) ch.get(i);
				System.out.println("Inserting the key-value pair" + " (" + i + ") => " + values[i-1]);
        		cache.put(i, values[i-1]);
		}
		
		for(int i=1; i<=10 ; i++)
		{
        		CacheServiceInterface cache = (CacheServiceInterface) ch.get(i);
				System.out.println("Getting the key-value pair" + " (" + i + ") => " + values[i-1]);
				String value = cache.get(i);		
		}




	} catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ConsistentHash.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ConsistentHash.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
