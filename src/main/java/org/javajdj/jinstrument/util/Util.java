/*
 * Copyright 2010-2020 Jan de Jongh <jfcmdejongh@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.javajdj.jinstrument.util;

/** Placeholder for (non-Swing) utility methods that probably should move somewhere else.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class Util
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // BYTES TO HEX
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray ();

  /** Returns the unsigned hexadecimal representation of an array of {@code byte}s as a {@code String}.
   * 
   * @param bytes The byte array, non-{@code null}.
   * @return The string representing the values in the byte array.
   * 
   * @throws IllegalArgumentException If {@code bytes == null}.
   * 
   */ 
  public static String bytesToHex (final byte[] bytes)
  {
    if (bytes == null)
      throw new IllegalArgumentException ();
    final char[] hexChars = new char[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++)
    {
      final int v = bytes[j] & 0xFF;
      hexChars[j * 2] = Util.HEX_ARRAY[v >>> 4];
      hexChars[j * 2 + 1] = Util.HEX_ARRAY[v & 0x0F];
    }
    return new String (hexChars);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
