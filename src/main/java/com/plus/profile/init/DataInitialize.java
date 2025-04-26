package com.plus.profile.init;


import com.plus.profile.profile.domain.Profile;
import com.plus.profile.profile.domain.repository.ProfileRepository;
import com.plus.profile.profile.domain.repository.ProfileViewRepository;
import com.plus.profile.user.domain.User;
import com.plus.profile.user.domain.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@org.springframework.context.annotation.Profile({"dev"})
@Configuration
@RequiredArgsConstructor
public class DataInitialize {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final ProfileViewRepository profileViewRepository;
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
    @Transactional
    void init() {

        resetDatabase();
        List<User> users = createUserTotal30();
        createProfile(users);
    }

    private void createProfile(List<User> users) {
        users.forEach(user -> {
            Profile profile = Profile.builder()
                    .userId(user.getId())
                    .username(user.getUsername())
                    .title("제목 입니다."+ user.getUsername())
                    .content("내용 입니다."+ user.getUsername())
                    .viewCount(random.nextLong(10))
                    .build();
            profileRepository.save(profile);
        });
    }

    private void resetDatabase(){
        userRepository.deleteAll();
        profileRepository.deleteAll();
        profileViewRepository.deleteAll();
    }

    private List<User> createUserTotal30() {

        List<User> users = new ArrayList<>();
        for(String name: names){
            User user = User.builder()
                    .username(name)
                    .encodedPassword("password")
                    .build();
            users.add(user);
        }
        userRepository.saveAll(users);
        return users;
    }

}
