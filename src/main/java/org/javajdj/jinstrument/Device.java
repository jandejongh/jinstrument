/* 
 * Copyright 2010-2019 Jan de Jongh <jfcmdejongh@gmail.com>.
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
package org.javajdj.jinstrument;

import org.javajdj.jservice.Service;

/** A communication endpoint on a {@link Bus} connected to a {@link Controller}.
 * 
 * <p>
 * A device is typically associated with (a physical interface on) an {@link Instrument}, but should be no more than
 * a precise description of the end point on the bus (in other words, instrument agnostic).
 *
 * <p>
 * A device acts as a bridge between {@link Instrument} and the protocol-specifics of {@link Bus} types and {@link Controller}s.
 * 
 * <p>
 * Note that for simplicity, currently, a device is associated with a single {@link Controller},
 * more or less skipping the notions of a {@link Bus} connecting them,
 * or of multi-controller (or no-controller) buses.
 * When needed, this will change in future releases.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public interface Device
  extends Service
{
  
  String getDeviceUrl ();
  
  BusAddress getBusAddress ();
  
  Controller getController ();
  
}
