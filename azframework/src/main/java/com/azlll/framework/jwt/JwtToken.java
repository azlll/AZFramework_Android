package com.azlll.framework.jwt;

import java.util.ArrayList;
import java.util.List;

public class JwtToken {

    private static final int JWT_SECTION_LENGTH = 3;


    /**
     * 原始字符串
     */
    private String rawString;
    /**
     * 解析出来的各个段的元素
     */
    private List<JwtTokenElement> elements;
    /**
     * 是否可用，即是否符合jwt的格式
     */
    private boolean isEnable = false;


    public String getRawString() {
        return rawString;
    }

    public List<JwtTokenElement> getElements() {
        return elements;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public JwtToken(String strJwtToken) {
        if (strJwtToken == null) {
            this.rawString = "";
            this.elements = new ArrayList<>();
            this.isEnable = false;
        }else{
            this.rawString = strJwtToken;
            this.elements = convertJwtTokenElementsFromOriginString(strJwtToken);
            if (this.elements != null && this.elements.size() == JWT_SECTION_LENGTH) {
                this.isEnable = true;
            }else{
                this.isEnable = false;
            }
        }
    }

    private List<JwtTokenElement> convertJwtTokenElementsFromOriginString(String strJwtToken) {
        if (strJwtToken == null) {
            return null;
        }
        String[] strings = strJwtToken.split("\\.");
        if (strings.length != JWT_SECTION_LENGTH) {
            return null;
        }else{

            List<JwtTokenElement> elements = new ArrayList<>();

            for (int i=0; i<JWT_SECTION_LENGTH; i++) {
                JwtTokenElement element = new JwtTokenElement(strings[i]);
                elements.add(element);
            }

            return elements;
        }
    }
}
