package net.ldmf.socketsdk;

public enum SocketStatus {
	ServerClosed, // 服务器关闭了Socket通道
	UnConnect, // 服务器没有成功连接
	Unknow, // 未知错误
	UnknownHost, // 未知主机
	IOExcetion// 读写管道异常
}
