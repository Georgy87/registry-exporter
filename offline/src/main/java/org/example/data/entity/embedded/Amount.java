package org.example.data.entity.embedded;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.data.constants.CurrencyCode;
import org.example.serializer.AmountSerializer;

import java.math.BigDecimal;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "${amount.name}")
public class Amount {
    @JsonSerialize(using = AmountSerializer.class)
    private BigDecimal amount;

    @NotEmpty
    private String currency;

    public static Amount ofCurrencyCode(BigDecimal amount, CurrencyCode currencyCode) {
        return new Amount(amount, currencyCode.toString());
    }
}
