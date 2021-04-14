package com.ahnu.server.helper;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
public class JwtTokenUtils {
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String SECRET = "jwtsecret";
    public static final String ISS = "echisan";
    //过期时间3小时
    private static final Long EXPIRATION = 60 * 60 * 3L;

    private static final String ROLE = "role";

    //创建token
    // public static String createToken(String username, String role){
    //     return JWT.create()
    //             .withClaim(ROLE,role)
    //             .withClaim("name",username)
    //             .withIssuer(ISS)
    //             .withIssuedAt(new Date())
    //             .sign(Algorithm.HMAC256(SECRET));
    //             // .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION * 1000))
    // }
    public static String createToken(int uid, String name,String role){
        return JWT.create()
                .withClaim(ROLE,role)
                .withClaim("uid",uid)
                .withClaim("name",name)
                .withIssuer(ISS)
                .withIssuedAt(new Date())
                .sign(Algorithm.HMAC256(SECRET));
        // .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION * 1000))
    }

    public static Map<String, Claim> verifyToken(String token)throws Exception {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
        return verifier.verify(token).getClaims();
    }
}