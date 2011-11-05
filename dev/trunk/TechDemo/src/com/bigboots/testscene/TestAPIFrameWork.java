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
package com.bigboots.testscene;

//import com.bigboots.BBApplication;
import com.bigboots.components.BBComponent;
import com.bigboots.components.BBEntity;

/**
 *
 * @author @author Ulrich Nzuzi <ulrichnz@code.google.com>
 */
/*
public class TestAPIFrameWork extends BBApplication{
    public static void main(String[] args) {
        TestAPIFrameWork app = new TestAPIFrameWork();
        app.run();
    }
}
*/
public class TestAPIFrameWork {
    public static void main(String[] args) {
        
        BBComponent cmp1 = new BBComponent("Light");
        BBComponent cmp2 = new BBComponent("Flame");
        BBEntity armor = new BBEntity("Armor");
        armor.add(cmp1);
        armor.add(cmp2);
        
        BBComponent comp1 = new BBComponent("Sword");
        BBComponent comp2 = new BBComponent("Shield");
        BBComponent comp3 = new BBComponent("Helmet");
        BBComponent comp4 = new BBComponent("Boots");
          
        BBEntity myPlayer = new BBEntity("Player");
        
        myPlayer.add(comp1);
        myPlayer.add(comp2);
        myPlayer.add(comp3);
        myPlayer.add(comp4);
        myPlayer.add(armor);
        
        myPlayer.display();
    }
}