package io.exam.auth;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.util.Date;

public class JWTAuth {

  private final String sharedSecret;

  public JWTAuth(String sharedSecret) {
    this.sharedSecret = sharedSecret;
  }

  public String encode(JWTClaimsSet claimsSet) throws JOSEException {
    SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
    JWSSigner signer = new MACSigner(sharedSecret);
    signedJWT.sign(signer);
    return signedJWT.serialize();
  }

  public JWTClaimsSet decode(String token) throws ParseException, JOSEException {
    SignedJWT signedJWT = SignedJWT.parse(token);

    JWSVerifier verifier = new MACVerifier(sharedSecret);

    if (!signedJWT.verify(verifier)) {
      throw new RuntimeException("JWT_INVALID");
    }

    if (signedJWT.getJWTClaimsSet().getExpirationTime().before(new Date())) {
      throw new RuntimeException("JWT_EXPIRED");
    }

    return signedJWT.getJWTClaimsSet();
  }

}
