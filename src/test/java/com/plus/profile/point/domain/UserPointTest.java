package com.plus.profile.point.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserPointTest {

    @Nested
    @DisplayName("결제")
    class UserPointPayOff {
        @Test
        @DisplayName("결제 성공")
        void payOff() {
            // given
            UserPoint userPoint = UserPoint.builder()
                    .point(1000L)
                    .build();

            // when
            userPoint.payOff(1000L);

            // then
            assertEquals(0L, userPoint.getPoint());
        }
    }

    @Nested
    @DisplayName("충전")
    class UserPointCharge{
        @Test
        @DisplayName("충전 성공")
        void charge() {
            // given
            UserPoint userPoint = UserPoint.builder()
                    .point(1000L)
                    .build();

            // when
            userPoint.charge(1000L);

            // then
            assertEquals(2000L, userPoint.getPoint());
        }
    }


}