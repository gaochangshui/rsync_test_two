package com.trechina.planocycle.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class jwtUtils {
    private SecretKeySpec key;
    private final Logger logger= LoggerFactory.getLogger(this.getClass());
    public jwtUtils() {
        String systemKey=System.getenv("JWT_KEY");
        if(systemKey==null) {
            //throw new RuntimeException("JWT_KEY is not found");
            systemKey="3Ys7Oi3Ou7Tw7To9Vm5Zn2Sg7Jf2Tm3Z";
        }
        key = new SecretKeySpec(systemKey.getBytes(), SignatureAlgorithm.HS256.getJcaName());
        //key = new SecretKeySpec(ConfigUtil.getPros().getProperty("jwtSecretKey").getBytes(), SignatureAlgorithm.HS256.getJcaName());
    }
    public Claims parseJWT(String jwt) throws Exception{
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(jwt).getBody();
        return claims;
    }
    public JSONObject getJwtInfo(String jwt) {
        JSONObject json = null;
        Base64.Decoder decoder = Base64.getDecoder();
        String[] temp = jwt.split("\\.");
        if (temp.length != 3) {
            return null;
        }
        try {
            json = new JSONObject(new String(decoder.decode(temp[1]), "UTF-8"));
        } catch (Exception e) {
            logger.error("getJwtInto error ：" + e);
        }
        return json;
    }
    public static void main(String[] args) throws Exception{
        //生成JWT
        jwtUtils util= new jwtUtils();
        String jwt="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxNjMyMjcwOTUzMzQwIiwiaXNzIjoiU3BhY2Vnb3VyZC5KV1QiLCJzdWIiOiJvYXV0aCIsImF1ZCI6IjEwMTIwMjQyIiwiaWF0IjoxNjMyMjcwOTUzLCJleHAiOjE2MzIzMjI3OTMsIm5iZiI6MTYzMjI3MDk1Mywic3lzdGVtcyI6IkNST1NTQUJDLFNXSVRDSElORyxTRVZFTkRBWVNUUkVORCxDVVNUT01FUkdST1VQLFRSSUFMUkVQRUFULEhNTCxUQU5QSU5SRVBFQVQsQ1VTVE9NRVJTRUdNRU5ULFVSRUJVTktBSU5FVyxkZGQsQ1VTVE9NRVIsTURMSU5LTUFOQUdFLElEUE9TLENPTlRJTlVFUkFURSxNYXN0ZXIsVVJFQlVOS0FJLENEVCxIRUlCQUksVGFuYVdhcmksUmVwb3J0IiwiaW5jaGFyZ2UiOiIwcGs2ajQsZjE3b3J6LDg3YzZmNCxoazc1cmUsMmVhYTMxLGFhZmVjMyw2NDMzMmUsZHQzcjRzLHN2OTVyMyJ9.i2y7ExifY-llpMEP6h5DOuefjpW5C-Zjsffo5fXIR_g";
        JSONObject c=util.getJwtInfo(jwt);
        System.out.println(c);
        Integer a = Integer.valueOf((c.get("aud").toString()));
        System.out.println(c.get("aud"));

    }
}
