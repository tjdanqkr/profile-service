package com.plus.profile.init;



import com.plus.profile.point.domain.UserCoupon;
import com.plus.profile.point.domain.repository.UserCouponRepository;
import com.plus.profile.point.domain.repository.UserPointLogRepository;
import com.plus.profile.product.domain.Product;
import com.plus.profile.product.domain.repository.ProductRepository;
import com.plus.profile.profile.domain.MyProfile;
import com.plus.profile.profile.domain.repository.ProfileRepository;
import com.plus.profile.profile.domain.repository.ProfileViewRepository;
import com.plus.profile.user.domain.User;
import com.plus.profile.point.domain.UserPoint;
import com.plus.profile.point.domain.repository.UserPointRepository;
import com.plus.profile.user.domain.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;


@Configuration
@RequiredArgsConstructor
@Transactional
@Profile("DEV")
public class DataInitialize {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final ProfileViewRepository profileViewRepository;
    private final UserPointRepository userPointRepository;
    private final UserCouponRepository userCouponRepository;
    private final ProductRepository productRepository;
    private final UserPointLogRepository userPointLogRepository;

    private final Random random = new Random();
    private final String[] names = {
            "김민준", "이서준", "박예린", "최지우", "정하준",
            "강서연", "윤도윤", "조은우", "한나윤", "오지민",
            "서지후", "홍시우", "안도현", "유지아", "백서윤",
            "심준호", "문하은", "권민재", "남지안", "임지호",
            "곽하람", "배주원", "천이준", "양유나", "노도연",
            "구채원", "민서진", "하도훈", "방예진", "서다온"
    };


    @PostConstruct
    void init() {
        resetDatabase();
        List<User> users = createUserTotal30();
        createProfile(users);
        createProduct();

    }

    private void createProfile(List<User> users) {
        users.forEach(user -> {
            MyProfile myProfile = MyProfile.builder()
                    .userId(user.getId())
                    .username(user.getUsername())
                    .title("제목 입니다."+ user.getUsername())
                    .content("내용 입니다."+ user.getUsername())
                    .viewCount(random.nextLong(10))
                    .build();
            profileRepository.save(myProfile);
        });
    }

    private void resetDatabase(){
        userPointLogRepository.deleteAll();
        userPointRepository.deleteAll();
        userCouponRepository.deleteAll();
        userRepository.deleteAll();
        profileRepository.deleteAll();
        profileViewRepository.deleteAll();
        productRepository.deleteAll();
    }

    private List<User> createUserTotal30() {

        List<User> users = new ArrayList<>();
        for(int i = 0; i < 30; i++) {
            String name = names[i];
            User tmp = User.builder()
                    .username(name)
                    .encodedPassword("password")
                    .build();

            User user = userRepository.save(tmp);
            users.add(user);

            UserPoint point = UserPoint.builder().user(user).point(10_000L).build();
            userPointRepository.save(point);

            UserCoupon couponPercent20 = UserCoupon.builder()
                    .user(user)
                    .couponId(UUID.randomUUID())
                    .couponCode("COUPON_CODE")
                    .couponIsPercentage(true)
                    .discountAmount(20)
                    .description("20% 할인 쿠폰")
                    .expirationDate(LocalDateTime.now().plusDays(30))
                    .build();
            userCouponRepository.save(couponPercent20);

            UserCoupon couponAmount3000 = UserCoupon.builder()
                    .user(user)
                    .couponId(UUID.randomUUID())
                    .couponCode("COUPON_CODE")
                    .couponIsPercentage(false)
                    .discountAmount(3_000)
                    .description("3000원 할인 쿠폰")
                    .expirationDate(LocalDateTime.now().plusDays(30))
                    .build();
            userCouponRepository.save(couponAmount3000);

            UserCoupon couponPercent70 = UserCoupon.builder()
                    .user(user)
                    .couponId(UUID.randomUUID())
                    .couponCode("COUPON_CODE")
                    .couponIsPercentage(true)
                    .discountAmount(70)
                    .description("70% 할인 쿠폰")
                    .expirationDate(LocalDateTime.now().plusDays(30))
                    .build();
            userCouponRepository.save(couponPercent70);
        }

        return users;
    }
    private List<Product> createProduct() {
        List<Product> products = new ArrayList<>();
        long[] prices = {1_000_000, 10_000, 5_000};
        for (int i = 1; i <= prices.length; i++) {
            long price = prices[i - 1];
            Product product = Product.builder()
                    .name("상품" + i)
                    .price(price)
                    .build();
            products.add(product);
        }
        productRepository.saveAll(products);
        return products;
    }

}
