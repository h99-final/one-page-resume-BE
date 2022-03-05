package com.f5.onepageresumebe.util;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GitPatchCodeUtil {
    //String -> List
    public List<String> parsePatchCode(String patchCode) {
        List<String> res = new ArrayList<>();
        String[] temp = patchCode.split("\n");

        for(int i = 0; i < temp.length; ++i) {
            res.add(temp[i]);
        }
        return res;
    }
    //List -> String
    public String combinePatchCode(List<String> patchCode) {
        String res = "";

        for(String curPatch : patchCode) {
            res += (curPatch + "\n");
        }
        // 맨 뒤 \n 삭제
        res = res.substring(0, res.length() - 2);

        return res;
    }
}
