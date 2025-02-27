package br.unesp.fc.signer.web.recaptcha;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class Response {

    private Boolean success;
    @JsonProperty("challenge_ts")
    private LocalDateTime challengeTimestamp;
    private String hostname;

}
