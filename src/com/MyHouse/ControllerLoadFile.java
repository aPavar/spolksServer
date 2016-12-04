package com.MyHouse;

import com.MyHouse.StructureClientLoad.StructureClientLoad;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public  class ControllerLoadFile {
    private static HashMap<String,StructureClientLoad> base;

    public HashMap<String, StructureClientLoad> getBase() {
        return base;
    }

    public static void setBase(HashMap<String, StructureClientLoad> baseArgument) {
        base = baseArgument;
    }


    public ControllerLoadFile(){
        base = new HashMap<>();
    }
}
