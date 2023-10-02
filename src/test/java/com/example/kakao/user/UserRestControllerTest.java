package com.example.kakao.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.kakao._core.advice.ValidAdvice;
import com.example.kakao._core.filter.JwtAuthorizationFilter;
import com.example.kakao._core.utils.JwtTokenUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@Import({ValidAdvice.class, JwtTokenUtils.class, JwtAuthorizationFilter.class}) // 내가 띄우고 싶은 클래스만 띄우기(webmvc가 ioc에 올려주지 않는 것을 직접 로딩하기)
@EnableAspectJAutoProxy // AOP 활성화
@WebMvcTest(UserRestController.class) // 메모리 : f - ds - uc (IoC 컨테이너에 띄어져있는 상태)
public class UserRestControllerTest {
    
    @Autowired
    private MockMvc mvc; // 컨트롤러 요청 객체(postman처럼 요청할 수 있다)

    @MockBean // 가짜로 메모리에 띄운다 (IoC 컨테이너에 띄어진다)
    private UserService userService;

    @Autowired
    private ObjectMapper om;

    @Test
    public void join_test() throws Exception{
        // given (데이터 준비)
        UserRequest.JoinDTO joinDTO = new UserRequest.JoinDTO();
        joinDTO.setEmail("cos@dddd.com");
        joinDTO.setPassword("meta1234@@");
        joinDTO.setUsername("cos");

        String requestBody = om.writeValueAsString(joinDTO); // 안전하게 dto를 만들어서 json 데이터 넣기
        System.out.println("테스트 : "+requestBody);

        // when (실행)
        ResultActions actions = mvc.perform(post("/join").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트" + responseBody);

        // then (상태 검증)
        // jsonPath : json데이터 검증시 사용
        actions.andExpect(jsonPath("$.success").value(true));
        actions.andExpect(jsonPath("$.response").value(nullValue()));
        actions.andExpect(jsonPath("$.error").value(nullValue()));
    }

    @Test
    public void login_test() throws Exception{
        // given (데이터 준비)
        UserRequest.LoginDTO loginDTO = new UserRequest.LoginDTO();
        loginDTO.setEmail("ssar@nate.com");
        loginDTO.setPassword("meta1234@@");

        String requestBody = om.writeValueAsString(loginDTO);
        System.out.println("테스트 : "+requestBody);

        // stub(가정)
        when(userService.login(any())).thenReturn("abcd"); // userService.login(any())를 실행하면 토큰을 리턴한다

        // when (실행)
        ResultActions actions = mvc.perform(post("/login").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        String autorization = actions.andReturn().getResponse().getHeader("Authorization");
        System.out.println("테스트 : "+responseBody);
        System.out.println("테스트 : "+autorization);

        // then (상태 검증)
        actions.andExpect(MockMvcResultMatchers.header().string("Authorization", "Brarer abcd"));
        actions.andExpect(jsonPath("$.success").value(true));
        actions.andExpect(jsonPath("$.response").value(nullValue()));
        actions.andExpect(jsonPath("$.error").value(nullValue()));
    }
}
