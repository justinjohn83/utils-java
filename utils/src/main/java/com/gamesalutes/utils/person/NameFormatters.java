/*
 * Copyright (c) 2013 Game Salutes.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * 
 * Contributors:
 *     Game Salutes - Repackaging and modifications of original work under University of Chicago and Apache License 2.0 shown below
 * 
 * Repackaging from edu.uchicago.nsit.iteco.utils to com.gamesalutes.utils
 * 
 * Copyright 2008 - 2011 University of Chicago
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.gamesalutes.utils.person;

import com.gamesalutes.utils.MiscUtils;
import com.gamesalutes.utils.person.PersonName.NameFormatter;

/**
 *
 * @author jmontgomery
 */
public final class NameFormatters
{
    private NameFormatters() {}

public static class LastFirstNameFormatter implements NameFormatter
{

            public String toNameString(PersonName name)
            {
                    if(name == null) return "";

                    StringBuilder s = new StringBuilder(128);

                    s.append(name.getLast());
                    boolean first = false;
                    if(!MiscUtils.isEmpty(name.getFirst()))
                    {
                            s.append(", ").append(name.getFirst());
                            first = true;
                    }
                    if(!MiscUtils.isEmpty(name.getMiddle()))
                    {
                            if(first) s.append(' ');
                            else s.append(", ");

                            s.append(name.getMiddle());
                    }

                    return s.toString();
            }

}

public static class FirstLastNameFormatter implements PersonName.NameFormatter
{

        public String toNameString(PersonName name)
        {
                if(name == null) return "";

                StringBuilder s = new StringBuilder(128);

                boolean first = false;
                if(!MiscUtils.isEmpty(name.getFirst()))
                {
                        s.append(name.getFirst());
                        first = true;
                }
                if(!MiscUtils.isEmpty(name.getMiddle()))
                {
                        if(first) s.append(' ');
                        s.append(name.getMiddle());
                        first = true;
                }

                if(first)
                    s.append(' ');
                s.append(MiscUtils.toString(name.getLast()));

                return s.toString();
        }

}

public static class UserDataNameFormatter implements PersonName.NameFormatter
{
        private final NameFormatter nameFmt;

        public UserDataNameFormatter(NameFormatter nameFmt)
        {
            if(nameFmt == null)
                throw new NullPointerException("nameFmt");
            this.nameFmt = nameFmt;
        }

        public String toNameString(PersonName name)
        {
            if(name == null)
                return "";

            StringBuilder s = new StringBuilder(128);
            s.append(nameFmt.toNameString(name));
            s.append(" (").append(MiscUtils.toString(name.getUserData())).append(")");
            return s.toString();
        }

}

public static class LastFirstUserDataNameFormatter implements PersonName.NameFormatter
{

    private static final NameFormatter nameFmt = new LastFirstNameFormatter();

    public String toNameString(PersonName name)
    {
        if(name == null)
            return "";
        
        StringBuilder s = new StringBuilder(128);
        s.append(nameFmt.toNameString(name));
        s.append(" (").append(MiscUtils.toString(name.getUserData())).append(")");
        return s.toString();
    }

}
}
