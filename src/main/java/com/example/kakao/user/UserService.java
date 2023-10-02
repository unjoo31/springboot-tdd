package com.example.kakao.user;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.kakao._core.errors.exception.Exception400;
import com.example.kakao._core.errors.exception.Exception500;
import com.example.kakao._core.utils.JwtTokenUtils;

import lombok.RequiredArgsConstructor;

public interface UserService {
    public void join(UserRequest.JoinDTO requestDTO);
    public String login(UserRequest.LoginDTO requestDTO);
}
