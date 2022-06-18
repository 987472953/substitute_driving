package com.pzhu.substitute.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayFundTransPayModel;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.domain.Participant;
import com.alipay.api.request.AlipayFundTransUniTransferRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayFundTransUniTransferResponse;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pzhu.substitute.common.BizException;
import com.pzhu.substitute.common.CommonConstants;
import com.pzhu.substitute.common.Result;
import com.pzhu.substitute.common.status.ResultCode;
import com.pzhu.substitute.config.AlipayConfig;
import com.pzhu.substitute.entity.*;
import com.pzhu.substitute.entity.dto.LoginDTO;
import com.pzhu.substitute.entity.dto.PaymentDTO;
import com.pzhu.substitute.mapper.DriverBalanceMapper;
import com.pzhu.substitute.mapper.TransferMapper;
import com.pzhu.substitute.service.AdminService;
import com.pzhu.substitute.service.CommonService;
import com.pzhu.substitute.service.OrderService;
import com.pzhu.substitute.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * @author dengyiqing
 * @description 管理员Controller
 * @date 2022/3/21
 */
@RestController
@RequestMapping("admin")
@Api(value = "管理员管理", tags = {"管理员操作接口"})
@Slf4j
public class AdminController {

    private final AlipayClient alipayClient;
    private final UserService userService;
    private final OrderService orderService;
    private final AlipayClient certAlipayClient;
    private final TransferMapper transferMapper;
    private final DriverBalanceMapper driverBalanceMapper;
    private final CommonService commonService;
    private final AdminService adminService;

    public AdminController(AlipayClient alipayClient, UserService userService, OrderService orderService, AlipayClient certAlipayClient, TransferMapper transferMapper, DriverBalanceMapper driverBalanceMapper, CommonService commonService, AdminService adminService) {
        this.alipayClient = alipayClient;
        this.userService = userService;
        this.orderService = orderService;
        this.certAlipayClient = certAlipayClient;
        this.transferMapper = transferMapper;
        this.driverBalanceMapper = driverBalanceMapper;
        this.commonService = commonService;
        this.adminService = adminService;
    }

    @PostMapping("login")
    @ApiOperation("管理员登录")
    public Result login(@RequestBody LoginDTO loginDTO) {
        log.debug(loginDTO.getUsername(), loginDTO.getPassword());
        commonService.checkLoginCode(loginDTO.getUuid(), loginDTO.getCode());
        return adminService.login(loginDTO);
    }

    @GetMapping("drivers")
    @ApiOperation("查询驾驶员信息")
    public Result drivers(Integer page, Integer size) {
        Page<DriverInfo> driverInfoPage = adminService.queryPageDrivers(page, size);
        return Result.ok().data("driverList", driverInfoPage.getRecords()).data("total", driverInfoPage.getTotal());
    }

    @GetMapping("orders")
    @ApiOperation("查询订单信息")
    public Result orders(Integer page, Integer size) {
        Page<Order> orderPage = adminService.queryPageOrders(page, size);
        long total = orderPage.getTotal();
        return Result.ok().data("orderList", orderPage.getRecords()).data("total", total);
    }

    @GetMapping("transfers")
    @ApiOperation("查询转账信息")
    public Result transfers(Integer page, Integer size) {
        Page<Transfer> transferPage = adminService.queryPageTransfer(page, size);
        long total = transferPage.getTotal();
        return Result.ok().data("transferList", transferPage.getRecords()).data("total", total);
    }

    @GetMapping("driverCertify/{driverId}")
    @ApiOperation("查询验证信息")
    public Result driverCertify(@PathVariable String driverId) {
        List<DriverCertify> driverCertifyList = adminService.queryDriverCertify(driverId);
        return Result.ok().data("driverCertifyList", driverCertifyList);
    }

    @PostMapping("driverCertify/{id}")
    @ApiOperation("通过该验证")
    public Result driverCertifySuccess(@PathVariable Integer id) {
        DriverCertify driverCertify = adminService.driverCertifySuccess(id);
        return Result.ok().data("driverCertify", driverCertify);
    }

    @DeleteMapping("driverCertify/{id}")
    @ApiOperation("拒绝该验证")
    public Result driverCertifyFail(@PathVariable Integer id) {
        DriverCertify driverCertify = adminService.driverCertifyFail(id);
        return Result.ok().data("driverCertify", driverCertify);
    }

    @GetMapping("rate")
    @ApiOperation("查询订单汇率")
    public Result rete() {
        return Result.ok().data("rate", CommonConstants.ORDER_RATE);
    }

    @PostMapping("rate")
    @ApiOperation("修改订单汇率")
    public Result rete(Double rate) {
        if (rate != null && rate >= 0 && rate <= 1) {
            CommonConstants.ORDER_RATE = BigDecimal.valueOf(rate);
        }
        return Result.ok().data("rate", CommonConstants.ORDER_RATE);
    }


    @PostMapping("alipay/{orderId}")
    public Result alipay(@PathVariable Long orderId, Authentication authentication) {
        if (Objects.isNull(authentication)) {
            return Result.error(ResultCode.UNAUTHENTICATED);
        }
        LoginUser principal = (LoginUser) authentication.getPrincipal();
        UserInfo userInfo = principal.getUserInfo();

        Order order = userService.queryUserOrderById(orderId, userInfo);
        String price = order.getPaymentAmount().toString();
        orderId = order.getId();
        String subject = userInfo.getNickname() + "的代驾订单";

        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();
        AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
        alipayRequest.setBizModel(model);
        model.setTotalAmount(price);
        model.setOutTradeNo(orderId.toString());
        model.setSubject(subject);
        alipayRequest.setReturnUrl(AlipayConfig.return_payment_url);
        alipayRequest.setNotifyUrl(AlipayConfig.notify_payment_url);
        //在公共参数中设置回跳和通知地址
        String form = "";
        try {
            form = certAlipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return Result.ok().data("from", form);
    }

    @PostMapping("notify")
    public void alipayNotify(PaymentDTO paymentDTO) {
        log.info("支付接口异步回调, orderId: {}", paymentDTO.getOut_trade_no());
        orderService.completeOrderPayment(paymentDTO);
    }

    @PostMapping("refund/{orderId}")
    public Result refund(@PathVariable Long orderId, Authentication authentication) throws AlipayApiException {
        if (Objects.isNull(authentication)) {
            return Result.error(ResultCode.UNAUTHENTICATED);
        }
        LoginUser principal = (LoginUser) authentication.getPrincipal();
        UserInfo userInfo = principal.getUserInfo();
        Double refundFee = orderService.refundOrder(orderId, userInfo);
        return Result.ok().data("refundFee", refundFee);
    }

    @PostMapping("transUniTransfer")
    @Transactional
    public Result transUniTransfer(Authentication authentication, Double amount) throws AlipayApiException {
        if (Objects.isNull(authentication)) {
            return Result.error(ResultCode.UNAUTHENTICATED);
        }
        LoginDriver principal = (LoginDriver) authentication.getPrincipal();
        DriverInfo driverInfo = principal.getDriverInfo();

        // 创建转账订单
        Transfer transfer = new Transfer(null, "", driverInfo.getId(), amount, "ALIPAY_USER_ID", driverInfo.getIdentity(), null);
        transferMapper.insert(transfer);

        // 更新订余额
        DriverBalance driverBalance = driverBalanceMapper.selectById(driverInfo.getId());
        if (Objects.isNull(driverBalance) || driverBalance.getBalance() < 0) {
            throw new BizException(ResultCode.ORDER_ERROR).setErrorMsg("用户没有余额");
        }
        BigDecimal oldBalance = BigDecimal.valueOf(driverBalance.getBalance());
        BigDecimal withdrawalAmount = BigDecimal.valueOf(amount);
        BigDecimal newBalance = oldBalance.subtract(withdrawalAmount);

        driverBalance.setBalance(newBalance.doubleValue());
        driverBalanceMapper.updateById(driverBalance);

        // 执行转账
        AlipayFundTransUniTransferRequest request = new AlipayFundTransUniTransferRequest();
        AlipayFundTransPayModel model = new AlipayFundTransPayModel();
        model.setOutBizNo(transfer.getId().toString());
        model.setTransAmount(transfer.getTransAmount().toString());
        model.setProductCode("TRANS_ACCOUNT_NO_PWD");
        model.setBizScene("DIRECT_TRANSFER");
        model.setOrderTitle(driverInfo.getPhoneNum() + "的提现订单");

        Participant payeeInfo = new Participant();
        // ALIPAY_USER_ID 用户登录标识, 不需要传入NAME
        // ALIPAY_LOGON_ID 用户登录标识, 需要传入NAME进行校验
        payeeInfo.setIdentityType("ALIPAY_LOGON_ID");
        payeeInfo.setIdentity(driverInfo.getIdentity());
        payeeInfo.setName(driverInfo.getDriverName());

        model.setPayeeInfo(payeeInfo);
        model.setRemark("用户提现");
        model.setBusinessParams("{\"payer_show_name_use_alias\":\"true\"}");

        request.setBizModel(model);
//        request.setBizContent("{" +
//                "\"out_biz_no\":\"201806300001\"," +
//                "\"trans_amount\":1.68," +
//                "\"product_code\":\"TRANS_ACCOUNT_NO_PWD\"," +
//                "\"biz_scene\":\"DIRECT_TRANSFER\"," +
//                "\"order_title\":\"201905代发\"," +
//                "\"payee_info\":{" +
//                "\"identity_type\":\"ALIPAY_USER_ID\"," +
//                "\"identity\":\"2088123412341234\"," +
//                "\"name\":\"黄龙国际有限公司\"," +
//                "}," +
//                "\"remark\":\"201905代发\"," +
//                "\"business_params\":\"{\\\"payer_show_name_use_alias\\\":\\\"true\\\"}\"" +
//                "}");
        AlipayFundTransUniTransferResponse response = certAlipayClient.certificateExecute(request);
        if (response.isSuccess()) {
            // 可以交给消息队列
            Transfer updateTransfer = new Transfer();
            updateTransfer.setId(Long.parseLong(response.getOutBizNo()));
            updateTransfer.setTransferOrderId(response.getOrderId());
            updateTransfer.updateById();
            return Result.ok();
        } else {
            log.error("提现失败, 错误resp:{}", response);
            return Result.error().data("resp", response).message(response.getSubMsg());
        }
    }
}
