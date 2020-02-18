package me.ztiany.safekb;

class KeyModel {

    KeyModel(int code, String label){
        this.code = code;
        this.label = label;
    }

    private String label;
    private int code;

    String getLabel() {
        return label;
    }

    int getCode() {
        return code;
    }

}
