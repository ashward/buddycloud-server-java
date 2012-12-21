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

import static org.junit.Assert.assertEquals;

import java.nio.charset.Charset;

import org.junit.Test;

public class StringUtilsTest {

	/**
	 * Based on code from <a href="http://stackoverflow.com/users/304/mcdowell">McDowell</a>
	 * on <a href="http://stackoverflow.com/questions/8511490/calculating-length-in-utf-8-of-java-string-without-actually-encoding-it">StackOverflow</a>
	 */
	@Test
	public void testCountUTF8Bytes() {
		Charset utf8 = Charset.forName("UTF-8");
		AllCodepointsIterator iterator = new AllCodepointsIterator();
		while (iterator.hasNext()) {
			String test = new String(Character.toChars(iterator.next()));
			assertEquals(test.getBytes(utf8).length,
					StringUtils.countUTF8Bytes(test));
		}
	}

	private static class AllCodepointsIterator {
		private static final int MAX = 0x10FFFF; // see
													// http://unicode.org/glossary/
		private static final int SURROGATE_FIRST = 0xD800;
		private static final int SURROGATE_LAST = 0xDFFF;
		private int codepoint = 0;

		public boolean hasNext() {
			return codepoint < MAX;
		}

		public int next() {
			int ret = codepoint;
			codepoint = next(codepoint);
			return ret;
		}

		private int next(int codepoint) {
			while (codepoint++ < MAX) {
				if (codepoint == SURROGATE_FIRST) {
					codepoint = SURROGATE_LAST + 1;
				}
				if (!Character.isDefined(codepoint)) {
					continue;
				}
				return codepoint;
			}
			return MAX;
		}
	}

}
