package com.herbst.vortexbank.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.herbst.vortexbank.exceptions.InvalidJWTAuthenticateException;
import com.herbst.vortexbank.repositories.AccountRepository;
import com.herbst.vortexbank.v1.dtos.TokenDTO;
import com.herbst.vortexbank.v1.services.AccountService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
public class TokenProvider {
    @Value("${security.jwt.token.secret_key:secret}")
    private String secretKey = "secret";
    @Value("${security.jwt.token.expire-length:3600000}")
    private Long validityInMilliSeconds = 3600000L;

    Algorithm algorithm = null;

    @Autowired
    private AccountRepository accountRepository;

    @PostConstruct
    public void init(){
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        algorithm = Algorithm.HMAC256(secretKey.getBytes());
    }

    public TokenDTO createAccessToken(String name, String CPF, String email, List<String> permissions){
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliSeconds);
        String accessToken = getAccessToken(name, CPF, email, permissions ,now, validity);
        String refreshToken = getRefreshToken(name, CPF, email, permissions, now);
        return new TokenDTO(name, now, validity, true, accessToken, refreshToken);
    }

    public TokenDTO refreshToken(String refreshToken){
        if(refreshToken.contains("bearer "))
            refreshToken = refreshToken.substring("bearer ".length());
        DecodedJWT decodedJWT = decodedToken(refreshToken);
        String email = decodedJWT.getSubject();
        String name = decodedJWT.getClaim("name").asString();
        String CPF = decodedJWT.getClaim("CPF").asString();
        List<String> permissions = decodedJWT.getClaim("permissions").asList(String.class);
        return createAccessToken(name, CPF, email, permissions);

    }

    public Authentication getAuthentication(String token){
        DecodedJWT decodedJWT = decodedToken(token);
        String email = decodedJWT.getSubject();
        String name = decodedJWT.getClaim("name").asString();
        String CPF = decodedJWT.getClaim("CPF").asString();
        UserDetails userDetails = accountRepository.findByNameAndEmailAndCPF(name, email, CPF);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getAccessToken(String name, String CPF, String email, List<String> permissions, Date now, Date validity){
        String issuesUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        String[] permissionsArray = permissions.toArray(new String[0]);
        return JWT.create()
                .withIssuedAt(now)
                .withExpiresAt(validity)
                .withSubject(email)
                .withClaim("CPF",CPF)
                .withClaim("name",name)
                .withArrayClaim("permissions", permissionsArray)
                .withIssuer(issuesUrl)
                .sign(algorithm)
                .strip();
    }

    public String getRefreshToken(String name, String CPF, String email, List<String> permissions, Date now){
        String issuesUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        Date validity = new Date(now.getTime() + validityInMilliSeconds * 2);
        String[] permissionsArray = permissions.toArray(new String[0]);
        return JWT.create()
                .withIssuedAt(now)
                .withExpiresAt(validity)
                .withSubject(email)
                .withClaim("CPF",CPF)
                .withClaim("name",name)
                .withArrayClaim("permissions", permissionsArray)
                .withIssuer(issuesUrl)
                .sign(algorithm)
                .strip();
    }

    private DecodedJWT decodedToken(String token){
        Algorithm alg = Algorithm.HMAC256(secretKey.getBytes());
        JWTVerifier jwtVerifier = JWT.require(alg).build();
        return jwtVerifier.verify(token);
    }

    public String resolveToken(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        if(token != null && token.startsWith("bearer ")){
            return token.substring("bearer ".length());
        }
        return token;
    }

    public boolean validateToken(String token){
        DecodedJWT decodedJWT = decodedToken(token);
        try{
            if(decodedJWT.getExpiresAt().before(new Date())){
                return false;
            }
            return true;

        }catch (Exception e){
            throw new InvalidJWTAuthenticateException("Token Invalid or Expired");
        }
    }

}
