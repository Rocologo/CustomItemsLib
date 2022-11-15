package one.lindegaard.CustomItemsLib;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import org.bukkit.Bukkit;

public class HttpTools {

	public interface httpCallback {
		void onSuccess();

		void onError();
	}

	public static void isHomePageReachable(URL url, httpCallback callback) {
		Bukkit.getConsoleSender().sendMessage("[CORE] isHomePageReachable");
		
		new Runnable() {
			
			@Override
			public void run() {
				try {
					// open a connection to that source
					HttpURLConnection urlConnect = (HttpURLConnection) url.openConnection();

					// trying to retrieve data from the source. If there
					// is no connection, this line will fail
					urlConnect.setConnectTimeout(5000);
					urlConnect.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
					urlConnect.addRequestProperty("User-Agent", "Mozilla");
					urlConnect.addRequestProperty("Referer", "google.com");

					boolean redirect = false;

					// normally, 3xx is redirect
					int status = urlConnect.getResponseCode();
					if (status != HttpURLConnection.HTTP_OK) {
						if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM
								|| status == HttpURLConnection.HTTP_SEE_OTHER)
							redirect = true;
					}
					Bukkit.getConsoleSender().sendMessage("[CORE] status="+status);

					if (redirect) {

						// get redirect url from "location" header field
						String newUrl = urlConnect.getHeaderField("Location");

						// open the new connnection again
						urlConnect = (HttpURLConnection) new URL(newUrl).openConnection();

						status = urlConnect.getResponseCode();
						Bukkit.getConsoleSender().sendMessage("[CORE] status2="+status);
					}
					
					if (status == HttpURLConnection.HTTP_OK)
						callback.onSuccess();
					else
						callback.onError();

				} catch (UnknownHostException e) {
					callback.onError();
				} catch (IOException e) {
					callback.onError();
				}
			}
		};
	}

}
