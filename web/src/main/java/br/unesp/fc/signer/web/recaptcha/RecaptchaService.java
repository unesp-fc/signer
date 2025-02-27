package br.unesp.fc.signer.web.recaptcha;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

@Service
public class RecaptchaService {

    @Value("${google.recaptcha.secret}")
    private String key;

    private RestClient restClient = RestClient.create();

    public boolean verify(String response, String servername) {
        var map = new LinkedMultiValueMap<String, String>();
        map.add("secret", key);
        map.add("response", response);
        var res = restClient.post()
                .uri("https://www.google.com/recaptcha/api/siteverify")
                .body(map)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .retrieve()
                .body(Response.class);
        if (Boolean.TRUE.equals(res.getSuccess()) && servername.equals(res.getHostname())) {
            return true;
        }
        return false;
    }

}
