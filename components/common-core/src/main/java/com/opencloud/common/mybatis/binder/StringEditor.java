package com.opencloud.common.mybatis.binder;

import org.springframework.beans.propertyeditors.PropertiesEditor;

public class StringEditor extends PropertiesEditor {
    @Override
    public String getAsText() {
        return getValue().toString();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if ("off".equals(text)) {
            text = "0";
            setValue(Integer.parseInt(text));
        } else if ("on".equals(text)) {
            text = "1";
            setValue(Integer.parseInt(text));
        } else {
            setValue(text);
        }
    }
}
