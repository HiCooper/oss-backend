package com.berry.oss.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.berry.oss.common.ResultCode;
import com.berry.oss.common.exceptions.BaseException;
import com.berry.oss.security.dto.UserInfoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Berry_Cooper.
 * Description:
 * Date: 2018-03-31
 * Time: 14:42
 */
@Component
public class TokenProvider {

    private final static Logger logger = LoggerFactory.getLogger(TokenProvider.class);

    /**
     * token中用户权限key
     */
    private static final String AUTHORITIES_KEY = "auth_com";

    private static final String USER_ID_KEY = "userId";

    private static final String SECRET = "com.berry.secret";

    private static final String ISSUER = "okmnji123";

    /**
     * token有效期时长  12 小时
     */
    public static final long TOKEN_VALIDITY_IN_MILLISECONDS = 12 * 3600 * 1000;

    /**
     * 记住我后 token有效时长 7 天
     */
    public static final long TOKEN_VALIDITY_IN_MILLISECONDS_FOR_REMEMBER_ME = 7 * 24 * 3600 * 1000;

    /**
     * Create and Sign a Token
     *
     * @param authentication 权限信息
     * @param rememberMe     是否记住我
     * @return 加密token
     */
    public String createAndSignToken(Authentication authentication, Integer userId, boolean rememberMe) {

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity;
        if (rememberMe) {
            validity = new Date(now + TOKEN_VALIDITY_IN_MILLISECONDS_FOR_REMEMBER_ME);
        } else {
            validity = new Date(now + TOKEN_VALIDITY_IN_MILLISECONDS);
        }

        String token;
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            token = JWT.create()
                    .withSubject(authentication.getName())
                    .withClaim(AUTHORITIES_KEY, authorities)
                    .withClaim(USER_ID_KEY, userId)
                    .withIssuer(ISSUER)
                    .withExpiresAt(validity)
                    .sign(algorithm);
        } catch (UnsupportedEncodingException exception) {
            //UTF-8 encoding not supported
            logger.error("jwt创建token失败，encoding not supported");
            throw new BaseException(ResultCode.ERROR_SERVE_REQUEST);
        } catch (JWTVerificationException exception) {
            //Invalid signature/claims
            logger.error("jwt创建token失败，Invalid signature/claims");
            throw new BaseException(ResultCode.ERROR_SERVE_REQUEST);
        }
        return token;
    }

    /**
     * Verify a Token
     *
     * @param token 待验证token
     * @return boolean
     */
    boolean validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build(); //Reusable verifier instance
            verifier.verify(token);
            return true;
        } catch (Exception e) {
            logger.error("JWT 验证失败");
        }
        return false;
    }

    /**
     * 从 jwt 获取权限信息
     *
     * @param jwt
     * @return 授权凭证
     */
    Authentication getAuthentication(String jwt) throws UnsupportedEncodingException {
        Map<String, Claim> claimMap = getClaimMap(jwt);
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claimMap.get(AUTHORITIES_KEY).asString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        UserInfoDTO principal = new UserInfoDTO();
        principal.setUsername(claimMap.get("sub").asString());
        principal.setId(claimMap.get(USER_ID_KEY).asInt());
        return new UsernamePasswordAuthenticationToken(principal, jwt, authorities);
    }

    private static Map<String, Claim> getClaimMap(String jwt) throws UnsupportedEncodingException {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build(); //Reusable verifier instance
        return verifier.verify(jwt).getClaims();
    }
}
