package com.example.kakao.product;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Answers.values;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.ArgumentMatchers.notNull;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.kakao._core.advice.ValidAdvice;
import com.example.kakao._core.config.FilterConfig;
import com.example.kakao._core.filter.JwtAuthorizationFilter;
import com.example.kakao._core.utils.JwtTokenUtils;
import com.example.kakao.core.MockData;
import com.example.kakao.product.option.Option;
import com.example.kakao.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;

@Import({ValidAdvice.class, FilterConfig.class}) // 내가 띄우고 싶은 클래스만 띄우기(webmvc가 ioc에 올려주지 않는 것을 직접 로딩하기)
@EnableAspectJAutoProxy // AOP 활성화
@WebMvcTest(ProductRestController.class) // 메모리 : f - ds - uc (IoC 컨테이너에 띄어져있는 상태)
public class ProductRestControllerTest extends MockData{
    
    @Autowired
    private MockMvc mvc; // 컨트롤러 요청 객체(postman처럼 요청할 수 있다)

    @MockBean // 가짜로 메모리에 띄운다 (IoC 컨테이너에 띄어진다)
    private ProductService productService;

    @Autowired
    private ObjectMapper om;

    @Test
    public void findAll_test() throws Exception{
        // given (데이터 준비)
        String page = "0";
        String jwt = JwtTokenUtils.createMockUser();
        System.out.println("테스트 : "+jwt);

        // stub
        // db에서 만들어낸 데이터 조회
        Product p1 = Product.builder()
                        .id(1)
                        .productName("기본에 슬라이딩 지퍼백 크리스마스/플라워에디션 에디션 외 주방용품 특가전")
                        .price(1000)
                        .image("/images/1.jpg")
                        .build();
        Product p2 = Product.builder()
                        .id(2)
                        .productName("[황금약단밤 골드]2022년산 햇밤 칼집밤700g외/군밤용/생율")
                        .price(2000)
                        .image("/images/2.jpg")
                        .build();
        List<Product> productList = Arrays.asList(p1, p2);

        List<ProductResponse.FindAllDTO> responseDTOs = productList.stream()
            .map(p -> new ProductResponse.FindAllDTO(p))
            .collect(Collectors.toList());

        when(productService.findAll(anyInt())).thenReturn(responseDTOs);

        // when (실행)
        ResultActions actions = mvc.perform(MockMvcRequestBuilders.get("/products").param("page", page).header("Authorization", "Bearer "+jwt)); // param은 모두 string
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트" + responseBody);

        // then (상태 검증)
        actions.andExpect(jsonPath("$.success").value(true));
        actions.andExpect(jsonPath("$.response").value(notNullValue(ProductResponse.FindByIdDTO.class)));
        actions.andExpect(jsonPath("$.error").value(nullValue()));
    }

    @Test
    public void findById_test() throws Exception{
        // given (데이터 준비)
        Integer id = 1;
        String jwt = JwtTokenUtils.createMockUser();

        // stub
        Product p1 = getProduct(1, "상품1");
        Option o1 = getOption(1, "옵션1", p1);
        Option o2 = getOption(2, "옵션3", p1);
        p1.addOption(o1);
        p1.addOption(o2);

        ProductResponse.FindByIdDTO responseDTO = new ProductResponse.FindByIdDTO(p1);
        when(productService.findById(anyInt())).thenReturn(responseDTO);

        // when
        ResultActions actions = mvc.perform(MockMvcRequestBuilders.get("/products/"+id).header("Authorization", "Bearer "+jwt));
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        actions.andExpect(jsonPath("$.success").value(true));
        actions.andExpect(jsonPath("$.response").value(notNullValue(ProductResponse.FindByIdDTO.class)));
        actions.andExpect(jsonPath("$.error").value(nullValue()));
    }
}
