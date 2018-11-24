/**

 * NIO TCP 客户端

 *

 * @date    2010-2-3

 * @time    下午03:33:26

 * @version 1.00

 */

public class TCPClient{

// 信道选择器

private Selector selector;



// 与服务器通信的信道

  SocketChannel socketChannel;



// 要连接的服务器Ip地址

private String hostIp;



// 要连接的远程服务器在监听的端口

private int hostListenningPort;



/**

   * 构造函数

   * @param HostIp

   * @param HostListenningPort

   * @throws IOException

   */

public TCPClient(String HostIp,int HostListenningPort)throws IOException{

this.hostIp=HostIp;

this.hostListenningPort=HostListenningPort;



    initialize();

  }



/**

   * 初始化

   * @throws IOException

   */

private void initialize() throws IOException{

// 打开监听信道并设置为非阻塞模式

socketChannel=SocketChannel.open(new InetSocketAddress(hostIp, hostListenningPort));

socketChannel.configureBlocking(false);



// 打开并注册选择器到信道

selector = Selector.open();

socketChannel.register(selector, SelectionKey.OP_READ);



// 启动读取线程

new TCPClientReadThread(selector);

  }



/**

   * 发送字符串到服务器

   * @param message

   * @throws IOException

   */

public void sendMsg(String message) throws IOException{

    ByteBuffer writeBuffer=ByteBuffer.wrap(message.getBytes(“UTF-16”));

socketChannel.write(writeBuffer);

  }



publicstaticvoid main(String[] args) throws IOException{

    TCPClient client=new TCPClient(“127.0.0.1”,1978);

    String str = “”;

for(int i=0; i<1000; i++) {

    str+=i+“,”;

    }

client.sendMsg(str);

  }

}



客户端接收线程

public TCPClientReadThread(Selector selector){

this.selector=selector;



new Thread(this).start();

  }



public void run() {

try {

while (selector.select() > 0) {

// 遍历每个有可用IO操作Channel对应的SelectionKey

for (SelectionKey sk : selector.selectedKeys()) {



// 如果该SelectionKey对应的Channel中有可读的数据

if (sk.isReadable()) {

// 使用NIO读取Channel中的数据

            SocketChannel sc = (SocketChannel)sk.channel();

            ByteBuffer buffer = ByteBuffer.allocate(1024);

sc.read(buffer);

buffer.flip();



// 将字节转化为为UTF-16的字符串

            String receivedString=Charset.forName(“UTF-16”).newDecoder().decode(buffer).toString();



// 控制台打印出来

            System.out.println(”接收到来自服务器”+sc.socket().getRemoteSocketAddress()+”的信息:”+receivedString);



// 为下一次读取作准备

sk.interestOps(SelectionKey.OP_READ);

          }



// 删除正在处理的SelectionKey

selector.selectedKeys().remove(sk);

        }

      }

    } catch (IOException ex) {

ex.printStackTrace();

    }

  }

}
