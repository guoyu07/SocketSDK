package net.ldmf.socketsdk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import android.util.Log;

public class SocketHolder {

	private SocketListener listener = null;

	/**
	 * @category 检查Socket通道是否连通
	 * */
	public boolean isConnect() {
		return client != null && client.isConnected();
	}

	// 因为有可能会产生多个不同属性的Socket连接，所以这里SocketHolder不做单例模式
	public SocketHolder() {
	}

	/**
	 * @category socket client obj
	 * */
	private static Socket client = null;
	private String host;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	private int port;

	public void connect(String host, int port) {
		setHost(host);
		setPort(port);
		connect();
	}

	public void connect() {
		new Thread(holder).start();
	}

	boolean isRun = true;

	private Runnable holder = new Runnable() {

		@Override
		public void run() {
			try {

				client = new Socket(getHost(), getPort());

				if (isListenerNotNull()) {
					getListener().onConnect();
				}

				while (isRun) {

					Log.i("", "Looper");

					String reponse = "";
					BufferedReader in = new BufferedReader(
							new InputStreamReader(client.getInputStream()));

					StringBuffer sb = new StringBuffer();

					char[] buffer = new char[1024];
					int len = 0;
					for (; (len = in.read(buffer)) > 0;) {
						Log.i("", "len :" + len);
						sb.append(buffer, 0, len);

						Log.i("", "sb:" + sb.toString());

						String str = sb.toString();
						if (str.endsWith("</page>")) {
							break;
						}
					}

					Log.i("", "get in:" + sb.toString());

					reponse = sb.toString();

					Log.i("", "reponse = " + reponse);

					Log.w("sender", "receive hashCode :" + this.hashCode());
					if (isListenerNotNull()) {
						getListener().onReviceData(reponse);
					}

				}
			} catch (Exception e) {
				e.printStackTrace();

				if (isListenerNotNull()) {
					getListener().onError(SocketStatus.Unknow);
				}
			}

		}
	};

	public void send(String request) {

		Log.d("send", "sender hashCode:" + this.hashCode());

		// Log.i("SocketHolder", "Will send:" + request);

		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					client.getOutputStream()));
			// writer.write(request.replace("\n", " ") + "\n");

			writer.write(request + "\n");

			writer.flush();
		} catch (Exception e) {

			String msg = e.getMessage();
			System.out.println("error:" + e.getMessage());

			// null
			// 这种情况是由于socket通道没有建立成功就发送内容而导致的
			if ("null".equals(msg)) {
				if (isListenerNotNull()) {
					getListener().onError(SocketStatus.UnConnect);
				}
				return;
			}

			// java.net.SocketException: sendto failed: EPIPE (Broken pipe)
			// 服务器通道关闭，此种情况需要重连
			if ("java.net.SocketException: sendto failed: EPIPE (Broken pipe)"
					.equals(msg)) {
				if (isListenerNotNull()) {
					getListener().onError(SocketStatus.ServerClosed);
				}
				return;
			}

			if ("java.net.SocketException: recvfrom failed: ECONNRESET (Connection reset by peer)"
					.equals(msg)) {
				if (isListenerNotNull()) {
					getListener().onError(SocketStatus.ServerClosed);
				}
				return;
			}

			e.printStackTrace();

			if (isListenerNotNull()) {
				getListener().onError(SocketStatus.Unknow);
			}
		}

	}

	public boolean isListenerNotNull() {
		boolean isNull = listener == null;
		if (isNull) {
			Log.i("SocketHolder",
					"Socket listener is null, data can not callback to listener.");
		}
		return !isNull;
	}

	public SocketListener getListener() {
		return listener;
	}

	public void setListener(SocketListener listener) {
		this.listener = listener;
	}
}
