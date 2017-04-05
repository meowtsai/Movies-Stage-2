package com.meowroll.movies;

/**
 * Created by Meow on 2017/1/4.
 */

public class SortCriteria {
    private String _value;
    private String _displayText;


    public SortCriteria(){
        this._value = "popular";
        this._displayText = "Most Popular";
    }

    public void set_value(String value){
        this._value = value;
    }

    public String get_value(){
        return this._value;
    }

    public void set_displayText(String text){
        this._displayText = text;
    }

    public String get_displayText(){
        return this._displayText;
    }
}


