package com.f5.onepageresumebe.web.dto.porf.responseDto;

import com.f5.onepageresumebe.domain.mysql.entity.Portfolio;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PorfreadResponseDto {

    List<Portfolio> portfolios;
}
