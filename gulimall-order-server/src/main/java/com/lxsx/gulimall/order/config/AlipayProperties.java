package com.lxsx.gulimall.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class AlipayProperties {

    /**
     *  在支付宝创建的应用的id
     */
    private String app_id = "2021000121677277";
    // 沙箱商户PID 2088621993663755
    private String pid = "2088621993663755";

    /**
     * 商户私钥，您的PKCS8格式RSA2私钥
     */
    private  String merchant_private_key ="MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCiaxeHAx1A7n8jBTqvo0y296LNpyXIZk+Ss6qu/c9TNtnal2GkbDw3O+d41kaNQ7xwYxMB4U1bT+tmLplPVyWrDECV8rQZq8wrpF2NOC22UKj5RlB2Q91vb5yZCsFXyMF4TQSNRR57MTSOdhH5XFlrT1ZRI5jvQ9E7zapjIFIghqHr1F8ohFtHrwl2fHGt8xQ6Hbh3vOAZCsHBfw6vbkSsjtbgJJpUWqGJAyXGBviX0PCbd1ynCnOF493IxjDttO2+41tlgeZgH1ETCUmMpLoDMPxqkSEwMAxVx0PgmiWbzJyBxboTmU5LUAwUaAf+vvbzgIBoF09N2Fz7ZOX9MIgzAgMBAAECggEBAIosZUWc5qQ4wbw/VJ/bYwY9QimGy2PjdkqtwsVAc8KtZUTLiGvjEI9EzTVFxZukRomuH5t32uGV6gNyHUccMpWDxOZMJd+5AecW2+eqJWl20WnP4d/a+GU3JJgmSDmwNrdrfjXY2YegYr/Xvo7Pe9sJEbvRdugGX9hqrUvLUVMNEzLCtZUvXHjsN9p8goIRYll8PSGiq7HbTVhsT1PkRketkurgI7VoeMAYxqS+eJR9w6i2RhEMvdijg/9rT6QAXREZ70bbCraENw+8N48oSiLJzDS6GpsE0p1nm9t0VNa7HOGy0hiTdT4F3+E+FpX/Gb6nKsW1cMHxLBpa3TmkPHECgYEA/Z+wf44hd1z+Md65F3fctjl64c/gwov05MtGJjXM1IMqevY2c0LYR8lZPBYfWwQ6uL10SbQk8fCrMdzSeAsOVyPI4OwaKziMcAh5oIC2uRawGvGRDQaTn0nSqDPCNoW0VQxjzE559Se+k6QAD15GcBKbrOfjg6EpvG2o7+zLoBcCgYEAo/Cl+rsQoPhOab/WPzEQjWSaB2tHeFfMxxkORYvAOoXZYLbsBPknjjphhwjD83scqzJjNZdH/erECsLvEhW8oduVT6Y8cYaWiTHsdbTKgS48sOHHd6hHCtJ6EibjJ4orsbatKux3Hz3DRXn08XkFb17iFUH1J6WoRa3MKYrv7kUCgYAeiaLDyQZIi/7jXFDm/XyqyEL+e3wxkI59GeipYyL1l5F+2rR1rl/CTQEclgFSgTDc25rr4O/t3a+PQmppH9OrNFjUlDkWaSmnt62FYNofcRblN57idZ9Ct63QqOLCk9jBwMsnZ2+R0CZHOqDzOMO8bNEnwYgIalhPItkLgnSGIwKBgQCHewiv/Ifxf0LOAct8zX9wWgMwvmeB/I0uvHYe4y8pLhRG6OSD96PI1lZi0cueG8/Faw6x6yODo8//VLldqLkKyfzKpgatixw8pRVU8OOpfhyJ0QyWxmpvdyGKfriapIUXiG/zWRfYqaIGeJMEO49SSodsKeX/rdcyPj7K1hcSQQKBgForbTJPMWid3YzQcO+j230U+xitL2ilYU9tyBCbNmYjcQpoCDUZ0cpE8ncUreUbWOeYiHNmcuc+8up7xCLAssXw4Y/nXVIyIAJroT/JYojLN38VIDJUgFWLOyB5aqaNLc7b9/Q8osu88fkjkifALFNkX7KzgBvJMla1WFjLsHui";

    /**
     *  支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
     */
    private  String alipay_public_key ="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyjSfmLRvE6WpF0sSCw1UNuGY1GLfWxI/ZP7XcEu04HcQsQREz6hzCpX3Gv2Wl0ILWJvIcQdboUWlulJbRW7SI5v3yy2bhDHzqPQcuCNDAh6k1xu6Iv815h9hB/MqarnPXbwkZN5JL0C4xBrzYuyDedLyPS+yPkDA9p8rJKRjEewV/BOipSV8icbsuOrn0M3SRqTtrPfGXWUoRzsaWoPNqxXrbkOm1bnk79+tH/+EB51J5kSqyCWhAcnLLDHHEaPi+5R8MQQuB561W8u0B/bA2exHTxl/oPEgYA3sqoBM2Yl6aCbaWynH6WlrHuBUTIhQFhHe5y6cQiYQaFXRNaICgwIDAQAB";

    /**
     * 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
     * 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
     */
    private  String notify_url="https://607252vu57.goho.co/order/pay/alipay/success";

    /**
     * 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
     * 同步通知，支付成功，一般跳转到成功页
     */
    private  String return_url="http://member.gulimall.com/memberOrder.html";

    /**
     * 签名方式
     */
    private  String sign_type = "RSA2";

    /**
     * 字符编码格式
     */
    private  String charset = "utf-8";

    /**
     *支付宝网关； https://openapi.alipaydev.com/gateway.do
     */
    private  String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    //订单超时时间
    private String timeout = "1m";

}
