package com.f5.onepageresumebe.web.dto.MGit.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MGitRequestDto {

    // github.com/guswls847
    private String gitUrl;

    // ourwiki
    private String repoName;

    public String getAccessRepoName() {

        String AccessRepoName = getOwner() + "/" + this.repoName;
        return AccessRepoName;
    }
    public String getOwner() {
        int idx = this.gitUrl.indexOf(".com/");
        return this.gitUrl.substring(idx+5);
    }
}
