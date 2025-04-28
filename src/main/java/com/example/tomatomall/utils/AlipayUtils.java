package com.example.tomatomall.utils;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.example.tomatomall.Repository.*;
import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.*;
import com.example.tomatomall.vo.PayResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.alipay.api.internal.util.AlipaySignature;
import org.springframework.transaction.annotation.Transactional;

import static com.alipay.api.AlipayConstants.FORMAT;

@Configuration
@ConfigurationProperties(prefix = "alipay")
@Getter
@Setter
@Component
public class AlipayUtils {
    private String appId;
    private String privateKey;
    private String publicKey;
    private String charset;
    private String signType;
    private String notifyUrl;
    private String returnUrl;
    private String serverUrl;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private CartRepository cartRepository;


    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockpileRepository stockpileRepository;

    @Autowired
    private CartsOrdersRelationRepository cartsOrdersRelationRepository;


    public PayResponse pay(Integer orderId){
        Orders orders = ordersRepository.findById(orderId).orElseThrow(TomatoMallException::orderNotFound);


        Long currentTime = System.currentTimeMillis();

        Long createTime = orders.getCreateTime()
                .atZone(ZoneId.systemDefault()) // 使用系统默认时区
                .toInstant()
                .toEpochMilli();


        long diff = currentTime - createTime;

// 判断是否超过 30 分钟（30 * 60 * 1000 = 1,800,000 毫秒）
        if (diff > 30 * 60 * 1000L) {//释放库存，Frozen-，Amount+，order设为超时
            List<Cart> cartsOrdersRelation = cartsOrdersRelationRepository.findCartsOrdersRelationByOrders(orders);
            for (Cart cart : cartsOrdersRelation) {
                Stockpile stockpile = cart.getProduct().getStockpile();

                stockpile.setAmount(stockpile.getAmount() + cart.getQuantity());
                stockpile.setFrozen(stockpile.getFrozen() - cart.getQuantity());
                stockpileRepository.save(stockpile);
            }
            orders.setStatus("TIMEOUT");
            ordersRepository.save(orders);
//            ordersRepository.deleteById(orderId);
            return null;
        }

        AliPay aliPay = new AliPay();
        aliPay.setTraceNo(String.valueOf(orderId));
        aliPay.setTotalAmount(orders.getTotalAmount().doubleValue());

        // 1. 创建Client，通用SDK提供的Client，负责调用支付宝的API
        AlipayClient alipayClient = new DefaultAlipayClient(serverUrl, appId,
                privateKey, FORMAT, charset, publicKey, signType);
        // 2. 创建 Request并设置Request参数
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();  // 发送请求的 Request类
        request.setNotifyUrl(notifyUrl);
        request.setReturnUrl(returnUrl);
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", aliPay.getTraceNo());  // 我们自己生成的订单编号
        bizContent.put("total_amount", aliPay.getTotalAmount()); // 订单的总金额
        bizContent.put("subject", aliPay.getSubject());   // 支付的名称
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");  // 固定配置
        request.setBizContent(bizContent.toString());
        // 执行请求，拿到响应的结果，返回给浏览器
        String form = "";
        try {
            form = alipayClient.pageExecute(request).getBody(); // 调用SDK生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
//        httpResponse.setContentType("text/html;charset=" + charset);
//        httpResponse.getWriter().write(form);// 直接将完整的表单html输出到页面
//        httpResponse.getWriter().flush();
//        httpResponse.getWriter().close();

        PayResponse payResponse = new PayResponse();
        payResponse.setOrderId(orderId);
        payResponse.setTotalAmount(orders.getTotalAmount());
        payResponse.setPaymentMethod("ALIPAY");
        payResponse.setPaymentForm(form);
        return payResponse;
    }

    public String payNotify(HttpServletRequest request) throws Exception {

//        Map<String, String> params = new HashMap<>();
//        Map<String, String[]> parameterFromRequest = request.getParameterMap();
//        String tradeStatus = request.getParameter("trade_status");
//
//        for(String name:parameterFromRequest.keySet()){
//            params.put(name, request.getParameter(name));
//        }
//
//        String sign = request.getParameter("sign");
//
//        String content = AlipaySignature.rsac(params);
//        Map<String, String> params = request.getParameterMap().entrySet().stream()
//                .collect(Collectors.toMap(
//                        Map.Entry::getKey,
//                        e -> String.join(",", e.getValue())
//                ));
//
//        // 2. 验证签名（公钥从配置读取）
//        boolean isValid = AlipaySignature.rsaCheckV1(
//                params,
//                publicKey, // 已配置的支付宝公钥
//                "UTF-8",
//                "RSA2"
//        );
//        if(!isValid){
//            return "failure";
//        }
//        Integer outTradeNo = Integer.valueOf(request.getParameter("out_trade_no")); // 关键代码
//
//        Orders orders = ordersRepository.findById(outTradeNo).orElseThrow(TomatoMallException::orderNotFound);
//        orders.get
//
//
//        return "success";
        if (request.getParameter("trade_status").equals("TRADE_SUCCESS")) {
            System.out.println("=========支付宝异步回调========");
            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = request.getParameterMap();
            for (String name : requestParams.keySet()) {
                params.put(name, request.getParameter(name));
                // System.out.println(name + " = " + request.getParameter(name));
            }
            String sign = params.get("sign");
            String content = AlipaySignature.getSignCheckContentV1(params);
            boolean checkSignature = AlipaySignature.rsa256CheckContent(content, sign, publicKey, "UTF-8"); // 验证签名
            // 支付宝验签
            if (checkSignature) {
                // 验签通过 可做自己需要的操作
//                System.out.println("交易名称: " + params.get("subject"));
//                System.out.println("交易状态: " + params.get("trade_status"));
//                System.out.println("支付宝交易凭证号: " + params.get("trade_no"));
//                System.out.println("商户订单号: " + params.get("out_trade_no"));
//                System.out.println("交易金额: " + params.get("total_amount"));
//                System.out.println("买家在支付宝唯一id: " + params.get("buyer_id"));
//                System.out.println("买家付款时间: " + params.get("gmt_payment"));
//                System.out.println("买家付款金额: " + params.get("buyer_pay_amount"));

                Orders orders = ordersRepository.findById(Integer.valueOf(params.get("out_trade_no"))).orElseThrow(TomatoMallException::orderNotFound);

                orders.setStatus("SUCCESS");

                Account account = orders.getUser();

                List<Cart> cart = cartsOrdersRelationRepository.findCartsOrdersRelationByOrders(orders);

                if(cart == null){
                    return "SUCCESS";
                }
//                List<Cart> cart = cartRepository.findByAccount(account);
                for (Cart cart1 : cart) {
                    Product product = cart1.getProduct();
                    Stockpile stockpile = product.getStockpile();
                    stockpile.setFrozen(stockpile.getFrozen() - cart1.getQuantity());
                    stockpileRepository.save(stockpile);

                    //todo carts_orders_relation
//                    CartsOrdersRelation cartsOrdersRelation = new CartsOrdersRelation();
//                    cartsOrdersRelation.setCartItem(cart1);
//                    cartsOrdersRelation.setOrders(orders);
//                    cartsOrdersRelationRepository.save(cartsOrdersRelation);

                }

            }
        }else{
            return "failure";
        }
        return "success";
    }

}
