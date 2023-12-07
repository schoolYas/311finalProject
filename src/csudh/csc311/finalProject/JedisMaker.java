package src.csudh.csc311.finalProject;
import java.io.IOException;
import redis.clients.jedis.Jedis;

public class JedisMaker {

	/**
	 * Make a Jedis object and authenticate it.
	 *
	 * @return
	 * @throws IOException
	 */
	public static Jedis make() {
		Jedis jedis = new Jedis("redis-15253.c280.us-central1-2.gce.cloud.redislabs.com",15253); // Connects to the Server
		jedis.auth("l5nhsVghzfdX9AKKeCZDNeMpOj8uPgIu"); //Authorizes connection with password
		return jedis;
	}

	/**
	 * Creates a test set to check that database populates
	 * @param args
	 * @throws IOException
	 * 
	 */
	public static void main(String[] args) {

		Jedis jedis = make();

		// String
		jedis.set("mykey", "myvalue");
		String value = jedis.get("mykey");
	    System.out.println("Got value: " + value);

	    // Set
	    jedis.sadd("myset", "element1", "element2", "element3");
	    System.out.println("element2 is member: " + jedis.sismember("myset", "element2"));

	    // List
	    jedis.rpush("mylist", "element1", "element2", "element3");
	    System.out.println("element at index 1: " + jedis.lindex("mylist", 1));

	    // Hash
	    jedis.hset("myhash", "word1", Integer.toString(2));
	    jedis.hincrBy("myhash", "word2", 1);
	    System.out.println("frequency of word1: " + jedis.hget("myhash", "word1"));
	    System.out.println("frequency of word2: " + jedis.hget("myhash", "word2"));

	    jedis.close();
	}
}