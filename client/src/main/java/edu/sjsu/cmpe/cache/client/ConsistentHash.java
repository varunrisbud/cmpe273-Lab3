package edu.sjsu.cmpe.cache.client;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ConsistentHash<DistributedCacheService> {

    
    private final int numberOfReplicas;
    private final SortedMap<String, edu.sjsu.cmpe.cache.client.DistributedCacheService> circle
            = new TreeMap<String, edu.sjsu.cmpe.cache.client.DistributedCacheService>();

    public ConsistentHash(Collection<edu.sjsu.cmpe.cache.client.DistributedCacheService> nodes, int num) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        this.numberOfReplicas=num;
        for (edu.sjsu.cmpe.cache.client.DistributedCacheService node : nodes) {
            add(node);
         
        }
    }

    public void add(edu.sjsu.cmpe.cache.client.DistributedCacheService node) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        for (Integer i = 0; i < numberOfReplicas; i++) {
            
            circle.put(hashString(node.cacheServerUrl +i.toString()),node);
        }
    }

    public void remove(edu.sjsu.cmpe.cache.client.DistributedCacheService node) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        for (Integer i = 0; i < numberOfReplicas; i++) {
            circle.remove(hashString(node.cacheServerUrl + i.toString()));
        }
    }

    public edu.sjsu.cmpe.cache.client.DistributedCacheService get(Object key) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        if (circle.isEmpty()) {
            return null;
        }
        
        String hash = hashString(key.toString());
        if (!circle.containsKey(hash)) {
            SortedMap<String, edu.sjsu.cmpe.cache.client.DistributedCacheService> tailMap
                    = circle.tailMap(hash);
            hash = tailMap.isEmpty()
                    ? circle.firstKey() : tailMap.firstKey();
        }
        return circle.get(hash);
    }

    public static String hashString(String message) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] hashedBytes = digest.digest(message.getBytes("UTF-8"));

        return convertByteArrayToHexString(hashedBytes);

    }

    private static String convertByteArrayToHexString(byte[] arrayBytes) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < arrayBytes.length; i++) {
            stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16)
                    .substring(1));
        }
        return stringBuffer.toString();
    }
}