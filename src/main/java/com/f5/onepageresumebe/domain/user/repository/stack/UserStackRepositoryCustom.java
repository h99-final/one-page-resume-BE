package com.f5.onepageresumebe.domain.user.repository.stack;

import java.util.List;

public interface UserStackRepositoryCustom {

    List<String> findStackNamesByPorfId(Integer porfId);

    List<String> findStackNamesByUserId(Integer userId);
}
