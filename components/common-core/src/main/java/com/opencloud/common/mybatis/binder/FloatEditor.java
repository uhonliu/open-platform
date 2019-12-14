package com.opencloud.common.mybatis.binder;

import org.springframework.beans.propertyeditors.PropertiesEditor;

public class FloatEditor extends PropertiesEditor {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (text == null || "".equals(text)) {
            text = "-1";
        }
        setValue(Float.parseFloat(text));
    }

    @Override
    public String getAsText() {
        return getValue().toString();
    }
}  
