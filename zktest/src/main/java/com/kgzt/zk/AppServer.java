/**
 * 
 */
package com.kgzt.zk;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

/**
 * @author Guo
 *
 */

public class AppServer
{
	private String	groupNode	= "sgroup";
	private String	subNode		= "sub";

	/**
	 * 连接zookeeper
	 * 
	 * @param address
	 *            server的地址
	 */
	public void connectZookeeper(String address) throws Exception
	{
		Watcher wh = new Watcher()
		{
			public void process(WatchedEvent event)
			{
				// 如果发生了"/sgroup"节点下的子节点变化事件, 更新server列表, 并重新注册监听
				if (event.getType() == EventType.NodeChildrenChanged && ("/" + groupNode).equals(event.getPath()))
				{
					// 不做处理
				}
			}
		};
		ZooKeeper zk = new ZooKeeper("127.0.0.1:2181,localhost:2182,localhost:2183", 5000, wh);
		// 在"/sgroup"下创建子节点
		// 子节点的类型设置为EPHEMERAL_SEQUENTIAL, 表明这是一个临时节点, 且在子节点的名称后面加上一串数字后缀
		// 将server的地址数据关联到新创建的子节点上
		String createdPath = zk.create("/" + groupNode + "/" + subNode, address.getBytes("utf-8"), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		System.out.println("create: " + createdPath);
	}

	/**
	 * server的工作逻辑写在这个方法中 此处不做任何处理, 只让server sleep
	 */
	public void handle() throws InterruptedException
	{
		Thread.sleep(Long.MAX_VALUE);
	}

	public static void main(String[] args) throws Exception
	{
		// 在参数中指定server的地址
		if (args.length == 0)
		{
			System.err.println("The first argument must be server address");
			System.exit(1);
		}
		AppServer as = new AppServer();
		as.connectZookeeper(args[0]);
		as.handle();
	}
}
