package com.f5.onepageresumebe.util;

import com.f5.onepageresumebe.security.SecurityUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.kohsuke.github.GitHub;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GitUtil {

    //String -> List
    public static List<String> parsePatchCode(String patchCode) {
        List<String> res = new ArrayList<>();
        String[] temp = patchCode.split("\n");

        for(int i = 0; i < temp.length; ++i) {
            res.add(temp[i]);
        }
        return res;
    }
    //List -> String
    public static String combinePatchCode(List<String> patchCode) {
        String res = "";

        for(String curPatch : patchCode) {
            res += (curPatch + "\n");
        }
        // 맨 뒤 \n 삭제
        res = res.substring(0, res.length() - 1);

        return res;
    }

    //repository 주인
    public static String getOwner(String gitUrl) {
        int idx = gitUrl.indexOf(".com/");
        return gitUrl.substring(idx + 5);
    }
}
