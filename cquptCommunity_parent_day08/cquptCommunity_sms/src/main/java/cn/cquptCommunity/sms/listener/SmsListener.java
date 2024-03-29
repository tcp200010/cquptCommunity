package cn.cquptCommunity.sms.listener;

import cn.cquptCommunity.sms.utils.SmsUtil;
import com.aliyuncs.exceptions.ClientException;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RabbitListener(queues = "sms") //rabbit中的监听器(消费方)，用于处理sms消息队列中的消息
public class SmsListener {

    @Autowired
    private SmsUtil smsUtil;

    @Value("{aliyun.sms.template_code}")
    private String template_code;//拿到配置文件中的模板和签名名称

    @Value("{aliyun.sms.sign_name}")
    private String sign_name;

    @RabbitHandler //定义处理消息方法
    public void executeSms(Map<String,String> map){
        //拿到消息队列中的信息
        String mobile = map.get("mobile");//手机号
        String checkCode = map.get("checkCode");//验证码
        //发送短信给用户
        try {
            smsUtil.sendSms(mobile,checkCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
