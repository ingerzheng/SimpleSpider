/**
 * @date 2017年3月25日
 * @version 1.0
 */
package junit.test;

import util.KeyUtils;

public class StaticVariableThreadSafe implements Runnable{
    
    public void run()  
    {  

        System.out.println("[" + Thread.currentThread().getName()  
                + "]获取static_i 的值:" + KeyUtils.getUrlId());  
    }  
      
    public static void main(String[] args)  
    {  
    	StaticVariableThreadSafe t = new StaticVariableThreadSafe();  
        //启动尽量多的线程才能很容易的模拟问题   
        for (int i = 0; i < 100; i++)  
        {  
            //t可以换成new Test(),保证每个线程都在不同的对象中执行，结果一样   
            new Thread(t, "线程" + i).start();  
        }  
    }  
}
