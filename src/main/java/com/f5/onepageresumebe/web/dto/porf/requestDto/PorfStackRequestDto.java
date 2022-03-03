package com.f5.onepageresumebe.web.dto.porf.requestDto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PorfStackRequestDto {

    private Integer id;

    List<String> stackContents;

}
