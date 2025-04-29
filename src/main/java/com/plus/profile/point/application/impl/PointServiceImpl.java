package com.plus.profile.point.application.impl;

import com.plus.profile.global.client.PointClientService;
import com.plus.profile.global.dto.payment.*;
import com.plus.profile.global.dto.point.PayOffPointRequest;
import com.plus.profile.global.dto.point.PayOffPointResponse;
import com.plus.profile.global.dto.point.PayOffPointWithCouponRequest;
import com.plus.profile.global.dto.point.PayOffResultType;
import com.plus.profile.global.exception.BusinessException;
import com.plus.profile.global.exception.GlobalPaymentException;
import com.plus.profile.point.application.PointPaymentClientService;
import com.plus.profile.point.application.PointService;
import com.plus.profile.point.domain.UserCoupon;
import com.plus.profile.point.domain.UserPoint;
import com.plus.profile.point.domain.UserPointLog;
import com.plus.profile.point.domain.repository.UserCouponRepository;
import com.plus.profile.point.domain.repository.UserPointLogRepository;
import com.plus.profile.point.domain.repository.UserPointRepository;
import com.plus.profile.point.presentation.dto.PointChargeRequest;
import com.plus.profile.point.util.Calculator;
import com.plus.profile.user.exception.UserExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService, PointClientService {
    private final PointPaymentClientService paymentService;
    private final UserPointRepository userPointRepository;
    private final UserPointLogRepository userPointLogRepository;
    private final UserCouponRepository userCouponRepository;
    @Override
    public CreatePaymentResponse chargePoint(UUID userId, PointChargeRequest request) {
        CreatePaymentRequest paymentRequest = new CreatePaymentRequest(userId, request.amount(), request.pgType(), request.supportKey());
        return paymentService.createTransaction(paymentRequest);
    }

    @Override
    @Transactional
    public void confirmPointCharge(UUID userId, UUID orderId) {
        ConfirmPaymentResponse response = paymentService.confirmPointCharge(new ConfirmPaymentRequest(userId, orderId));

        if (response.status().equals(ConfirmPaymentResult.NOT_FOUND))
            throw new BusinessException(GlobalPaymentException.PAYMENT_CONFIRM_NOT_FOUND);

        if (response.status().equals(ConfirmPaymentResult.PENDING))
            throw new BusinessException(GlobalPaymentException.PAYMENT_CONFIRM_PENDING);

        if (response.status().equals(ConfirmPaymentResult.ALREADY_PROCESSED))
            throw new BusinessException(GlobalPaymentException.PAYMENT_CONFIRM_ALREADY);

        UserPoint userPoint = userPointRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(UserExceptionCode.USER_NOT_FOUND));

        long before = userPoint.getPoint();
        long after = before + response.transactionAmount();
        userPoint.charge(response.transactionAmount());

        UserPointLog chargeLog = UserPointLog.createChargeLog(
                userId,
                before,
                after,
                response.transactionAmount()
        );
        userPointLogRepository.save(chargeLog);
    }

    @Override
    @Transactional
    public PayOffPointResponse payOffPoint(PayOffPointRequest request) {
        Optional<UserPoint> byUserId = userPointRepository.findByUserId(request.userId());
        if(byUserId.isEmpty())
            return new PayOffPointResponse(false, PayOffResultType.USER_NOT_FOUND.getMessage(), PayOffResultType.USER_NOT_FOUND, 0, 0);
        UserPoint userPoint = byUserId.get();
        if(userPoint.getPoint() < request.productPrice())
            return new PayOffPointResponse(false, PayOffResultType.NOT_ENOUGH_POINTS.getMessage(), PayOffResultType.NOT_ENOUGH_POINTS, 0, 0);

        long before = userPoint.getPoint();

        userPoint.payOff(request.productPrice());
        long after = userPoint.getPoint();
        UserPointLog paidLog = UserPointLog.createPaidLog(before, after, request);
        userPointLogRepository.save(paidLog);
        return new PayOffPointResponse(true, PayOffResultType.SUCCESS.getMessage(), PayOffResultType.SUCCESS, after, 0);

    }

    @Override
    @Transactional
    public PayOffPointResponse payOffPointWithCoupon(PayOffPointWithCouponRequest request) {
        Optional<UserPoint> optionalUserPoint = userPointRepository.findByUserId(request.userId());
        if(optionalUserPoint.isEmpty())
            return new PayOffPointResponse(false, PayOffResultType.USER_NOT_FOUND.getMessage(), PayOffResultType.USER_NOT_FOUND, 0, 0);
        Optional<UserCoupon> optionalUserCoupon = userCouponRepository.findById(request.userCouponId());
        if(optionalUserCoupon.isEmpty())
            return new PayOffPointResponse(false, PayOffResultType.COUPON_NOT_FOUND.getMessage(), PayOffResultType.COUPON_NOT_FOUND, 0, 0);


        UserPoint userPoint = optionalUserPoint.get();
        UserCoupon userCoupon = optionalUserCoupon.get();

        if (userCoupon.isUsed()) {
            return new PayOffPointResponse(false, PayOffResultType.COUPON_ALREADY_USED.getMessage(), PayOffResultType.COUPON_ALREADY_USED, userPoint.getPoint(), 0);
        }
        if (userCoupon.getExpirationDate().isBefore(java.time.LocalDateTime.now())) {
            return new PayOffPointResponse(false, PayOffResultType.COUPON_EXPIRED.getMessage(), PayOffResultType.COUPON_EXPIRED, userPoint.getPoint(), 0);
        }

        long discount = Calculator.calculateDiscount(userCoupon, request.productPrice());
        long finalPrice = Math.max(request.productPrice() - discount, 0);

        if (userPoint.getPoint() < finalPrice) {
            return new PayOffPointResponse(false, PayOffResultType.NOT_ENOUGH_POINTS.getMessage(), PayOffResultType.NOT_ENOUGH_POINTS, userPoint.getPoint(), 0);

        }

        long before = userPoint.getPoint();
        userPoint.payOff(finalPrice);
        long after = userPoint.getPoint();
        userCoupon.useCoupon();

        UserPointLog paidLog = UserPointLog.createPaidWhitCouponLog(
                before, after, discount, request, userCoupon
        );
        userPointLogRepository.save(paidLog);
        return new PayOffPointResponse(true, PayOffResultType.SUCCESS.getMessage(), PayOffResultType.SUCCESS, after, finalPrice);

    }
}
