package boot.spring.event;

import org.springframework.context.ApplicationEvent;

public class MessagePushEvent extends ApplicationEvent {

    String msg;

    String phone;

    public MessagePushEvent(Object source, String msg, String phone) {
        super(source);
        this.msg = msg;
        this.phone = phone;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
