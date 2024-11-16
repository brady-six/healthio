package com.bsix.healthio;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.web.ErrorResponseException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MainTest {

  public static final ErrorResponseException DEFAULT_BAD_REQUEST =
      new ErrorResponseException(
          HttpStatusCode.valueOf(400),
          ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), "Bad Request"),
          new IllegalArgumentException());

  public static final Jwt DEFAULT_JWT =
      new Jwt(
          "value",
          Instant.now(),
          Instant.now().plusSeconds(3600),
          Map.of("alg", "HS256", "typ", "JWT"),
          Map.of("sub", "1"));
  @Autowired private TestRestTemplate http;

  public static final ResultMatcher matchPagedModel() {
    return result -> {
      jsonPath("$.page").isNumber().match(result);
      jsonPath("$.size").isNumber().match(result);
      jsonPath("$.totalPages").isNumber().match(result);
      jsonPath("$.totalElements").isNumber().match(result);
      jsonPath("$._links.self.href").isString().match(result);
    };
  }

  public static final ResultMatcher matchProblemDetail() {
    return result -> {
      jsonPath("$.type").isString();
      jsonPath("$.title").isString().match(result);
      jsonPath("$.status").isNumber().match(result);
      jsonPath("$.detail").isString().match(result);
      jsonPath("$.instance").isString().match(result);
    };
  }

  @Test
  void contextLoads() {
    assert http != null;
  }
}
