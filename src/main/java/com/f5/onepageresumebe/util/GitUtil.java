package com.f5.onepageresumebe.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GitUtil {

    //String -> List
    public static List<String> parsePatchCode(String patchCode) {
        List<String> res = new ArrayList<>();
        String[] temp = patchCode.split("\n");

        Collections.addAll(res, temp);
        return res;
    }
    //List -> String
    public static String combinePatchCode(List<String> patchCode) {
        StringBuilder res = new StringBuilder();

        for(String curPatch : patchCode) res.append(curPatch).append("\n");
        // 맨 뒤 \n 삭제
        res = new StringBuilder(res.substring(0, res.length() - 1));

        return res.toString();
    }

    //repository 주인
    public static String getOwner(String gitUrl) {
        int idx = gitUrl.indexOf(".com/");
        return gitUrl.substring(idx + 5);
    }
}
