package com.opencloud.common.mybatis.binder;

import org.springframework.beans.propertyeditors.PropertiesEditor;

public class IntegerEditor extends PropertiesEditor {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (text == null || "".equals(text) || "off".equals(text)) {
            text = "0";
        }
        if ("on".equals(text)) {
            text = "1";
        }
        setValue(Integer.parseInt(text));
    }

    @Override
    public String getAsText() {
        return getValue().toString();
    }
}
