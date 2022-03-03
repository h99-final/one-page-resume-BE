package com.f5.onepageresumebe.dto;


import com.f5.onepageresumebe.domain.entity.Stack;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringStack;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
public class PorfStackRequestDto {

    private Integer id;

    List<String> stackContents;

}
