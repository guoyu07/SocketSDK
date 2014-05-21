package net.ldmf.socketsdk;

public interface SocketListener {

	/**
	 * @category 接收到数据
	 * */
	public void onReviceData(String data);

	/**
	 * @category 产生错误
	 * */
	public void onError(SocketStatus status);

	/**
	 * @category 与服务器建立连接
	 * */
	public void onConnect();

	/**
	 * @category 与服务器断开
	 * */
	public void onDisConncet();
}
