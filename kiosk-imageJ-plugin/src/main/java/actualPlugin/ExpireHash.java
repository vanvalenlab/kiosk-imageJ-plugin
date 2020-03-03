package actualPlugin;

// ExpireHash is used to send
// an API request to set an
// expiration time.
public class ExpireHash {
	String hash;
	int expireIn;
	
	public ExpireHash(String h, int e) {
		hash = h;
		expireIn = e;
	}
}
