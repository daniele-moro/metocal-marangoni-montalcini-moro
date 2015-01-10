package business.security.boundary;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestManager {
    
    private static String BASE_URL;
    
    public RequestManager(String url, String city) {
        BASE_URL = url + "" + city;
    }
    
    public RequestManager(String url, String lat, String lng) {
        BASE_URL = url + lat + "&lon=" + lng + "&cnt=16&APPID=543122f4518679639a607123e79c14ad";
    }

    public String getData() {
        
        HttpURLConnection con = null ;
        InputStream is = null;

        try {
            con = (HttpURLConnection) (new URL(BASE_URL)).openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();
            
            // Let's read the response
            StringBuffer buffer = new StringBuffer();
            is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while (  (line = br.readLine()) != null )
                buffer.append(line + "\r\n");
            is.close();
            con.disconnect();
            return buffer.toString();
        }
        catch(Throwable t) {
            t.printStackTrace();
        }
        finally {
            try { is.close(); } catch(Throwable t) {}
            try { con.disconnect(); } catch(Throwable t) {}
        }
        return null; 
    }
}
