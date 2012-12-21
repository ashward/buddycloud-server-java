/*
 * Buddycloud Channel Server
 * http://buddycloud.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.buddycloud.channelserver.utils;

/**
 * Evil utils class for String type stuff.
 */
public class StringUtils {
	/**
	 * Returns a count of the number of bytes the given string would take when represented
	 * in UTF-8.
	 * <p>Based on code from <a href="http://stackoverflow.com/users/304/mcdowell">McDowell</a>
	 * on <a href="http://stackoverflow.com/questions/8511490/calculating-length-in-utf-8-of-java-string-without-actually-encoding-it">StackOverflow</a>
	 * @param input the string to count the bytes for
	 * @return the number of bytes
	 */
	public static int countUTF8Bytes(final String input) {
		int count = 0;
	    for (int i = 0, len = input.length(); i < len; i++) {
	      char ch = input.charAt(i);
	      if (ch <= 0x7F) {
	        count++;
	      } else if (ch <= 0x7FF) {
	        count += 2;
	      } else if (Character.isHighSurrogate(ch)) {
	        count += 4;
	        ++i;
	      } else {
	        count += 3;
	      }
	    }
	    return count;
	}
}
