package ru.beeline.fdmnotificationsmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.beeline.fdmnotificationsmanagement.dto.auth.EmailResponseDTO;
import ru.beeline.fdmnotificationsmanagement.client.AuthClient;
import ru.beeline.fdmnotificationsmanagement.domain.User;
import ru.beeline.fdmnotificationsmanagement.repository.UserRepository;

import javax.transaction.Transactional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthClient authClient;

    public User findByUserId(Integer userId) {
        return userRepository.findByUserId(userId);
    }

    public User findByUserIdOrCreate(Integer userId) {
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            EmailResponseDTO emailResponseDTO = authClient.getEmailByUserID(userId);
            user = userRepository.save(User.builder()
                    .email(emailResponseDTO.getEmail())
                    .userId(userId)
                    .build());
        }
        return user;
    }
}