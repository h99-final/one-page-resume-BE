package com.f5.onepageresumebe.domain.git.repository.file;

import com.f5.onepageresumebe.domain.git.entity.GitFile;

import java.util.Optional;

public interface GitFileRepositoryCustom {

    Optional<GitFile> findFileByIdFetchAll(Integer fileId);
}
