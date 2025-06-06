package boot.spring.listener;

import boot.spring.event.MessagePushEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


@Async
@Component
public class MessagePushEventListener {

    @EventListener
    public void pushEventListener(MessagePushEvent event) throws InterruptedException {
        System.out.println("--------开始执行推送"+ event.getMsg() + " " + event.getPhone());
        Thread.sleep(5000);
        System.out.println("--------结束执行推送");
    }  


}
