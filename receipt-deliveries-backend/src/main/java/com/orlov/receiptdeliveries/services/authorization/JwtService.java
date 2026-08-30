package com.orlov.receiptdeliveries.services.authorization;

import com.orlov.receiptdeliveries.entities.Organization;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Сервис создания JWT токена для авторизованных организаций.
 */
@Service
@RequiredArgsConstructor
public class JwtService implements IJwtService {

    private final JwtEncoder jwtEncoder;

    @Value("${security.jwt.issuer}")
    private String issuer;

    @Value("${security.jwt.access-token-ttl}")
    private Duration accessTokenTtl;

    @Override
    public GeneratedToken generateAccessToken(Organization organization) {
        if(organization == null)
            throw new IllegalArgumentException("Организация не указана.");

        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(accessTokenTtl);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .subject(organization.getId()
                        .toString())
                .claim(
                        "organizationId",
                        organization.getId()
                                .toString())
                .claim(
                        "organizationName",
                        organization.getName())
                .claim(
                        "login",
                        organization.getLogin())
                .claim(
                        "role",
                        organization.getRole()
                                .name())
                .claim(
                        "roles",
                        List.of(organization.getRole()
                                .name()))
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256)
                .type("JWT")
                .build();

        Jwt jwt = jwtEncoder.encode(JwtEncoderParameters.from(
                header,
                claims));

        return new GeneratedToken(
                jwt.getTokenValue(),
                expiresAt);
    }
}
