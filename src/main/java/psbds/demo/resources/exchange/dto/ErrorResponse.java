package psbds.demo.resources.exchange.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Error response DTO for exchange rate endpoints.
 * Used when the service is unavailable or errors occur.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    @JsonProperty("error")
    private String error;

    @JsonProperty("message")
    private String message;
}
