package consol;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

public class myconsmoskva {
	static String senderLogin;

	static String[] partsout(String[] array, int index) {
		String[] result = new String[array.length - index];
		for (int i = index; i < (array.length); i++) {
			result[i - index] = array[i];

		}
		return result;
	}

	@SuppressWarnings({ "rawtypes" })
	public static void main(String[] args) {
		try {
			Client client = ClientBuilder.newClient();
			String[] parts;
			InputStreamReader isr = new InputStreamReader(System.in);
			BufferedReader br = new BufferedReader(isr);
			String s = null;
			// Timer timeToReceiveMsg = new Timer();
			System.out.printf("Enter String%n");
			boolean isClosed = false;
			// boolean isTimerStarted = false;
			while (!isClosed) {

				s = br.readLine();
				parts = s.split(" ");
				switch (parts[0]) {
				case "ping":
					String response = client.target("http://localhost:8080/chat/server/ping")
							.request(MediaType.TEXT_PLAIN_TYPE).get(String.class);
					if (response != null)
						System.out.println("Ping successfull");
					break;
				case "echo":
					response = client.target("http://localhost:8080/chat/server/echo")
							.request(MediaType.TEXT_PLAIN_TYPE)
							.post(Entity.text(String.join(" ", partsout(parts, 1))), String.class);
					System.out.println(response);
					break;
				case "login":
					UserInfo userInfo = new UserInfo();
					userInfo.setLogin(parts[1]);
					userInfo.setPassword(parts[2]);
					Entity userInfoEntity = Entity.entity(userInfo, MediaType.APPLICATION_JSON_TYPE);
					Response responseOnLogin = client.target("http://localhost:8080/chat/server/user")
							.request(MediaType.TEXT_PLAIN_TYPE).put(userInfoEntity);
					if (responseOnLogin.getStatus() == Status.CREATED.getStatusCode())
						System.out.println("New user registered");
					else
						System.out.println("Error code" + responseOnLogin.getStatus());
					client.register(HttpAuthenticationFeature.basic(userInfo.getLogin(), userInfo.getPassword()));
					senderLogin = userInfo.getLogin();
					/*
					 * if (userInfo != null && !isTimerStarted) isTimerStarted =
					 * true; timeToReceiveMsg.schedule(new MyTimerTask(), 0,
					 * 1000);
					 */
					break;
				case "list":
					try {
					WrappedList users = client.target("http://localhost:8080/chat/server/users")
							.request(MediaType.APPLICATION_JSON_TYPE).get(WrappedList.class);
					System.out.println("List of active users:" + users.items);
					} catch (Exception ex) {
						throw new IOException("Failed to retrieve the list of users", ex);
					}
					break;
				case "msg":
					try {
						Response responseOnMessage = client.target("http://localhost:8080/chat/server/parts[1]/message")
								.request(MediaType.APPLICATION_JSON_TYPE)
								.post(Entity.text(String.join(" ", partsout(parts, 2))));
						if (responseOnMessage.getStatus() >= 300)
							throw new IOException(String.format("%s: %s", responseOnMessage.getStatus(),
									responseOnMessage.getEntity()));
					} catch (IOException ex) {
						throw ex;
					} catch (Exception ex) {
						throw new IOException("Failed to send message", ex);
					}
					break;
				case "file":
					try {
						if (senderLogin != null) {
							File file=new File(parts[2]);
							FileInfo fileInfo = new FileInfo(senderLogin, file);
							Entity fileInfoEntity = Entity.entity(fileInfo, MediaType.APPLICATION_JSON_TYPE);
							Response responseOnFile = client.target("http://localhost:8080/chat/server/parts[1]/files")
									.request(MediaType.APPLICATION_JSON_TYPE).post(fileInfoEntity);
							if (responseOnFile.getStatus() >= 300)
								throw new IOException(String.format("%sFailed to send the file: %s",
										responseOnFile.getStatus(), responseOnFile.getEntity()));
						}
					} catch (IOException ex) {
						throw ex;
					} catch (Exception ex) {
						throw new IOException("Failed to send the file.", ex);
					}

					break;
				case "exit":
					if (client != null) {
						client.close();
						isClosed = true;
						isr.close();
						br.close();
					}
					break;
				default:
					System.out.println("Invalid command");
					break;
				}
			}

		} catch (UnknownHostException e) {
			System.out.println("Unknown host");
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
