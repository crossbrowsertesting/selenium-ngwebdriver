package CBT.NGWebDriverCBT;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class CBTHelper {
	private String sessionId,username,authkey;
	private String apiUrl = "crossbrowsertesting.com/api/v3/selenium";
	public CBTHelper(String username, String authkey, boolean useSecureTunnel) {
		// for java URL's must be character encoded. If you use 
		// your email, let's replace that character
		this.username = username;
		this.authkey = authkey;
		if (useSecureTunnel) {
			
		}
		
		if (this.username.contains("@")) {
			username = username.replace("@", "%40");
		} 
	}
	
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	public void setScore(String score) {
		String url = "https://" + apiUrl + "/" + this.sessionId;
		String payload = "{\"action\": \"set_score\", \"score\": \"" + score + "\"}";
		makeRequest("PUT", url,payload);
	}
	
	public void takeSnapshot() {
		if (this.sessionId != null) {
			String url = "https://" + apiUrl + "/" + this.sessionId + "/snapshots";
			String payload = "{\"selenium_test_id\": \"" + this.sessionId + "\"}";
			makeRequest("POST",url,payload);
		}
	}
	
	public void setDescription(String desc) {
		String url = "https://" + apiUrl + "/" + this.sessionId;
		String payload = "{\"action\": \"set_description\", \"description\": \"" + desc + "\"}";
		makeRequest("PUT", url,payload);
	}
	
	private void makeRequest(String requestMethod, String apiUrl, String payload) {
		URL url;
		String auth = "";

        if (username != null && authkey != null) {
            auth = "Basic " + Base64.encodeBase64String((username+":" + authkey).getBytes());
        }
        try {
            url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(requestMethod);
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", auth);
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
            osw.write(payload);
            osw.flush();
            osw.close();
            conn.getResponseCode();
            
        } catch (Exception e) {
        	System.out.println(e.getMessage());
        }
	}
	
	private void secureTunnel() throws IOException, InterruptedException {
		if (isTunnelActive()) {
			System.out.println("Tunnel active, carrying on..");
		} else {
			System.out.println("Tunnel not active, starting tunnel");
			Process p = Runtime.getRuntime().exec(System.getProperty("user.dir") + "/SecureTunnel/cbt-tunnels --username " + username + " --authkey " + authkey + " --verbose > log.text");
			while (!isTunnelActive()) {
				Thread.sleep(10000);
			}
			
			System.out.println("Tunnel started successfully");
		}
	}
	
	private boolean isTunnelActive() {
		String JSON_RESPONSE;
		boolean tunnelActive = false;
		try {
			JSON_RESPONSE = getTunnels();
			JSONObject jo = new JSONObject(JSON_RESPONSE);
			JSONArray ja = jo.getJSONArray("tunnels");
			JSONObject tunnel = ja.getJSONObject(0);
			tunnelActive = tunnel.get("active").toString().equals("true");
		}catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return tunnelActive;
	}
	
	private String getTunnels() throws IOException {
		String requestString = "https://crossbrowsertesting.com/api/v3/tunnels";
		URL url = new URL(requestString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		
		conn.setRequestMethod("GET");
		if (username != null && authkey != null) {
			String userpassEncoding = Base64.encodeBase64String((username+":"+authkey).getBytes());
			conn.setRequestProperty("Authorization", "Basic " + userpassEncoding);
		}
		if (conn.getResponseCode() != 200) {
			throw new IOException(conn.getResponseMessage());
		}
		// Buffer the result into a string
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
		rd.close();
		conn.disconnect();
		return sb.toString();
	}
}