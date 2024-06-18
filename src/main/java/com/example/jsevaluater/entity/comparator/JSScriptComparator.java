package com.example.jsevaluater.entity.comparator;

import com.example.jsevaluater.entity.JSScript;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class JSScriptComparator implements Comparator<JSScript> {

    @Override
    public int compare(JSScript jsScript1, JSScript jsScript2) {
        return Integer.compare(Math.toIntExact(jsScript1.getId()), Math.toIntExact(jsScript2.getId()));
    }

}
