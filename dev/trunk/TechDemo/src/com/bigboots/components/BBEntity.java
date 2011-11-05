/*
 * Copyright (C) 2011  BigBoots Team
 *  
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  
 * See <http://www.gnu.org/licenses/>.
 */
package com.bigboots.components;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author @author Ulrich Nzuzi <ulrichnz@code.google.com>
 * 
 * Composite of our Object component Design
 */
public class BBEntity extends BBObject{
    
    public static StringBuffer strBuff = new StringBuffer();

    //Collection of child graphics.
    private List<BBObject> mChildComponents = new ArrayList<BBObject>();
    
    public BBEntity(String name){
        super(name + "_Entity");
        super.setType("ENTITY");
    }
    
    //Prints the BBObject.
    public void display() {
        super.print(strBuff);
        strBuff.append("   ");

        for (BBObject obj : mChildComponents) {
            if(obj.getType().equals("ENTITY")){
                ((BBEntity)obj).display();
            }
            else{
                obj.print(strBuff);
            }
        }
        strBuff.setLength(strBuff.length() - 3);

    }

    
    //Adds the BBObject to the composition.
    public void add(BBObject obj) {
        mChildComponents.add(obj);
    }
 
    //Removes the BBObject from the composition.
    public void remove(BBObject obj) {
        mChildComponents.remove(obj);
    }

}
