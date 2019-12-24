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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Default implementation of a {@link InstrumentRegistry}.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class DefaultInstrumentRegistry
  implements InstrumentRegistry
{

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (DefaultInstrumentRegistry.class.getName ());

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private DefaultInstrumentRegistry ()
  {
  }
  
  private volatile static DefaultInstrumentRegistry INSTANCE = null;
  
  private final static Object INSTANCE_LOCK = new Object ();
  
  public final static DefaultInstrumentRegistry getInstance ()
  {
    synchronized (DefaultInstrumentRegistry.INSTANCE_LOCK)
    {
      if (DefaultInstrumentRegistry.INSTANCE == null)
        DefaultInstrumentRegistry.INSTANCE = new DefaultInstrumentRegistry ();  
    }
    return DefaultInstrumentRegistry.INSTANCE;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // REGISTRY LISTENERS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Set<InstrumentRegistry.Listener> instrumentRegistryListeners = new LinkedHashSet<> ();
  
  private final Object instrumentRegistryListenersLock = new Object ();
  
  private volatile Set<InstrumentRegistry.Listener> instrumentRegistryListenersCopy = new LinkedHashSet<> ();
  
  @Override
  public final void addRegistryListener (final InstrumentRegistry.Listener l)
  {
    synchronized (this.instrumentRegistryListenersLock)
    {
      if (l != null && ! this.instrumentRegistryListeners.contains (l))
      {
        this.instrumentRegistryListeners.add (l);
        this.instrumentRegistryListenersCopy = new LinkedHashSet<> (this.instrumentRegistryListeners);
      }
    }
  }

  @Override
  public final void removeRegistryListener (final InstrumentRegistry.Listener l)
  {
    synchronized (this.instrumentRegistryListenersLock)
    {
      if (this.instrumentRegistryListeners.remove (l))
        this.instrumentRegistryListenersCopy = new LinkedHashSet<> (this.instrumentRegistryListeners);
    }
  }

  protected final void fireInstrumentRegistryChanged ()
  {
    // References are atomic.
    final Set<InstrumentRegistry.Listener> listeners = this.instrumentRegistryListenersCopy;
    for (final InstrumentRegistry.Listener l : listeners)
      l.instrumentRegistryChanged (this);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // BUS TYPES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Set<BusType> busTypes = new LinkedHashSet<> ();
  
  private final Object busTypesLock = new Object ();
  
  private volatile Set<BusType> busTypesCopy = new LinkedHashSet<> ();
  
  @Override
  public final void addBusType (final BusType busType)
  {
    if (busType == null)
      throw new IllegalArgumentException ();
    boolean changed = false;
    synchronized (this.busTypesLock)
    {
      if (! busTypes.contains (busType))
      {
        this.busTypes.add (busType);
        this.busTypesCopy = new LinkedHashSet<> (this.busTypes);
        changed = true;
      }
    }
    if (changed)
      fireInstrumentRegistryChanged ();
  }
  
  @Override
  public final void removeBusType (final BusType busType)
  {
    if (busType == null)
      throw new IllegalArgumentException ();
    boolean changed = false;
    synchronized (this.busTypesLock)
    {
      if (this.busTypes.remove (busType))
      {
        this.busTypesCopy = new LinkedHashSet<> (this.busTypes);
        changed = true;
      }
    }    
    if (changed)
      fireInstrumentRegistryChanged ();
  }
  
  @Override
  public final List<BusType> getBusTypes ()
  {
    return Collections.unmodifiableList (new ArrayList<> (this.busTypesCopy));
  }
  
  @Override
  public final BusType getBusTypeByUrl (final String url)
  {
    if (url == null || url.trim ().isEmpty ())
      return null;
    for (final BusType busType : getBusTypes ())
      if (busType.getBusTypeUrl ().equalsIgnoreCase (url))
        return busType;
    return null;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONTROLLER TYPES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Set<ControllerType> controllerTypes = new LinkedHashSet<> ();
  
  private final Object controllerTypesLock = new Object ();
  
  private volatile Set<ControllerType> controllerTypesCopy = new LinkedHashSet<> ();
  
  @Override
  public final void addControllerType (final ControllerType controllerType)
  {
    if (controllerType == null)
      throw new IllegalArgumentException ();
    boolean changed = false;
    synchronized (this.controllerTypesLock)
    {
      if (! controllerTypes.contains (controllerType))
      {
        if (controllerType.getControllerTypeUrl () == null || controllerType.getControllerTypeUrl ().trim ().isEmpty ())
        {
          LOG.log (Level.SEVERE, "Controller Type URL is null or empty!!");
          throw new IllegalArgumentException ();          
        }
        if (getControllerTypeByUrl (controllerType.getControllerTypeUrl ()) != null)
        {
          LOG.log (Level.SEVERE, "Controller Type URL {0} is already registered!", controllerType.getControllerTypeUrl ());
          throw new IllegalArgumentException ();
        }
        this.controllerTypes.add (controllerType);
        this.controllerTypesCopy = new LinkedHashSet<> (this.controllerTypes);
        changed = true;
      }
    }
    if (changed)
      fireInstrumentRegistryChanged ();
  }
  
  @Override
  public final void removeControllerType (final ControllerType controllerType)
  {
    if (controllerType == null)
      throw new IllegalArgumentException ();
    boolean changed = false;
    synchronized (this.controllerTypesLock)
    {
      if (this.controllerTypes.remove (controllerType))
      {
        this.controllerTypesCopy = new LinkedHashSet<> (this.controllerTypes);
        changed = true;
      }
    }    
    if (changed)
      fireInstrumentRegistryChanged ();
  }
  
  @Override
  public final List<ControllerType> getControllerTypes ()
  {
    return Collections.unmodifiableList (new ArrayList<> (this.controllerTypesCopy));
  }
  
  @Override
  public final ControllerType getControllerTypeByUrl (final String url)
  {
    if (url == null || url.trim ().isEmpty ())
      return null;
    for (final ControllerType controllerType : getControllerTypes ())
      if (controllerType.getControllerTypeUrl ().equalsIgnoreCase (url))
        return controllerType;
    return null;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DEVICE TYPES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Set<DeviceType> deviceTypes = new LinkedHashSet<> ();
  
  private final Object deviceTypesLock = new Object ();
  
  private volatile Set<DeviceType> deviceTypesCopy = new LinkedHashSet<> ();
  
  @Override
  public final void addDeviceType (final DeviceType deviceType)
  {
    if (deviceType == null)
      throw new IllegalArgumentException ();
    boolean changed = false;
    synchronized (this.deviceTypesLock)
    {
      if (! deviceTypes.contains (deviceType))
      {
        this.deviceTypes.add (deviceType);
        this.deviceTypesCopy = new LinkedHashSet<> (this.deviceTypes);
        changed = true;
      }
    }
    if (changed)
      fireInstrumentRegistryChanged ();
  }
  
  @Override
  public final void removeDeviceType (final DeviceType deviceType)
  {
    if (deviceType == null)
      throw new IllegalArgumentException ();
    boolean changed = false;
    synchronized (this.deviceTypesLock)
    {
      if (this.deviceTypes.remove (deviceType))
      {
        this.deviceTypesCopy = new LinkedHashSet<> (this.deviceTypes);
        changed = true;
      }
    }    
    if (changed)
      fireInstrumentRegistryChanged ();
  }
  
  @Override
  public final List<DeviceType> getDeviceTypes ()
  {
    return Collections.unmodifiableList (new ArrayList<> (this.deviceTypesCopy));
  }
  
  @Override
  public final DeviceType getDeviceTypeByUrl (final String url)
  {
    if (url == null || url.trim ().isEmpty ())
      return null;
    for (final DeviceType deviceType : getDeviceTypes ())
      if (deviceType.getDeviceTypeUrl ().equalsIgnoreCase (url))
        return deviceType;
    return null;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT TYPES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Set<InstrumentType> instrumentTypes = new LinkedHashSet<> ();
  
  private final Object instrumentTypesLock = new Object ();
  
  private volatile Set<InstrumentType> instrumentTypesCopy = new LinkedHashSet<> ();
  
  @Override
  public final void addInstrumentType (final InstrumentType instrumentType)
  {
    if (instrumentType == null)
      throw new IllegalArgumentException ();
    boolean changed = false;
    synchronized (this.instrumentTypesLock)
    {
      if (! instrumentTypes.contains (instrumentType))
      {
        this.instrumentTypes.add (instrumentType);
        this.instrumentTypesCopy = new LinkedHashSet<> (this.instrumentTypes);
        changed = true;
      }
    }
    if (changed)
      fireInstrumentRegistryChanged ();
  }
  
  @Override
  public final void removeInstrumentType (final InstrumentType instrumentType)
  {
    if (instrumentType == null)
      throw new IllegalArgumentException ();
    boolean changed = false;
    synchronized (this.instrumentTypesLock)
    {
      if (this.instrumentTypes.remove (instrumentType))
      {
        this.instrumentTypesCopy = new LinkedHashSet<> (this.instrumentTypes);
        changed = true;
      }
    }    
    if (changed)
      fireInstrumentRegistryChanged ();
  }
  
  @Override
  public final List<InstrumentType> getInstrumentTypes ()
  {
    return Collections.unmodifiableList (new ArrayList<> (this.instrumentTypesCopy));
  }
  
  @Override
  public final InstrumentType getInstrumentTypeByUrl (final String url)
  {
    if (url == null || url.trim ().isEmpty ())
      return null;
    for (final InstrumentType instrumentType : getInstrumentTypes ())
      if (instrumentType.getInstrumentTypeUrl ().equalsIgnoreCase (url))
        return instrumentType;
    return null;    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT VIEW TYPES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Set<InstrumentViewType> instrumentViewTypes = new LinkedHashSet<> ();
  
  private final Object instrumentViewTypesLock = new Object ();
  
  private volatile Set<InstrumentViewType> instrumentViewTypesCopy = new LinkedHashSet<> ();
  
  @Override
  public final void addInstrumentViewType (final InstrumentViewType instrumentViewType)
  {
    if (instrumentViewType == null)
      throw new IllegalArgumentException ();
    boolean changed = false;
    synchronized (this.instrumentViewTypesLock)
    {
      if (! instrumentViewTypes.contains (instrumentViewType))
      {
        this.instrumentViewTypes.add (instrumentViewType);
        this.instrumentViewTypesCopy = new LinkedHashSet<> (this.instrumentViewTypes);
        changed = true;
      }
    }
    if (changed)
      fireInstrumentRegistryChanged ();
  }
  
  @Override
  public final void removeInstrumentViewType (final InstrumentViewType instrumentViewType)
  {
    if (instrumentViewType == null)
      throw new IllegalArgumentException ();
    boolean changed = false;
    synchronized (this.instrumentViewTypesLock)
    {
      if (this.instrumentViewTypes.remove (instrumentViewType))
      {
        this.instrumentViewTypesCopy = new LinkedHashSet<> (this.instrumentViewTypes);
        changed = true;
      }
    }    
    if (changed)
      fireInstrumentRegistryChanged ();
  }
  
  @Override
  public final List<InstrumentViewType> getInstrumentViewTypes ()
  {
    return Collections.unmodifiableList (new ArrayList<> (this.instrumentViewTypesCopy));
  }
  
  @Override
  public final InstrumentViewType getInstrumentViewTypeByUrl (final String url)
  {
    if (url == null || url.trim ().isEmpty ())
      return null;
    for (final InstrumentViewType instrumentViewType : getInstrumentViewTypes ())
      if (instrumentViewType.getInstrumentViewTypeUrl ().equalsIgnoreCase (url))
        return instrumentViewType;
    return null;    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // BUSES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final Set<Bus> buses = new LinkedHashSet<> ();
  
  private final Object busesLock = new Object ();
  
  private volatile Set<Bus> busesCopy = new LinkedHashSet<> ();
  
  @Override
  public final void addBus (final Bus bus)
  {
    if (bus == null)
      throw new IllegalArgumentException ();
    boolean changed = false;
    synchronized (this.busesLock)
    {
      if (! this.buses.contains (bus))
      {        
        if (bus.getBusUrl () == null || bus.getBusUrl ().trim ().isEmpty ())
        {
          LOG.log (Level.SEVERE, "Bus URL is null or empty!!");
          throw new IllegalArgumentException ();          
        }
        if (getBusByUrl (bus.getBusUrl ()) != null)
        {
          LOG.log (Level.SEVERE, "Bus URL {0} is already registered!", bus.getBusUrl ());
          throw new IllegalArgumentException ();
        }
        this.buses.add (bus);
        this.busesCopy = new LinkedHashSet<> (this.buses);
        changed = true;
      }
    }
    if (changed)
      fireInstrumentRegistryChanged ();
  }

  @Override
  public final void removeBus (final Bus bus)
  {
    if (bus == null)
      throw new IllegalArgumentException ();
    boolean changed = false;
    synchronized (this.busesLock)
    {
      if (this.buses.remove (bus))
      {
        this.busesCopy = new LinkedHashSet<> (this.buses);
        changed = true;
      }
    }    
    if (changed)
      fireInstrumentRegistryChanged ();
  }

  @Override
  public final List<Bus> getBuses ()
  {
    return Collections.unmodifiableList (new ArrayList<> (this.busesCopy));
  }
  
  @Override
  public final Bus getBusByUrl (final String url)
  {
    if (url == null || url.trim ().isEmpty ())
      return null;
    for (final Bus bus : getBuses ())
      if (bus.getBusUrl ().equalsIgnoreCase (url))
        return bus;
    return null;    
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONTROLLERS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Set<Controller> controllers = new LinkedHashSet<> ();
  
  private final Object controllersLock = new Object ();
  
  private volatile Set<Controller> controllersCopy = new LinkedHashSet<> ();
  
  @Override
  public final void addController (final Controller controller)
  {
    if (controller == null)
      throw new IllegalArgumentException ();
    boolean changed = false;
    synchronized (this.controllersLock)
    {
      if (! this.controllers.contains (controller))
      {        
        if (controller.getControllerUrl () == null || controller.getControllerUrl ().trim ().isEmpty ())
        {
          LOG.log (Level.SEVERE, "Controller URL is null or empty!!");
          throw new IllegalArgumentException ();          
        }
        if (getControllerByUrl (controller.getControllerUrl ()) != null)
        {
          LOG.log (Level.SEVERE, "Controller URL {0} is already registered!", controller.getControllerUrl ());
          throw new IllegalArgumentException ();
        }
        this.controllers.add (controller);
        this.controllersCopy = new LinkedHashSet<> (this.controllers);
        changed = true;
      }
    }
    if (changed)
      for (final Bus bus : controller.getBuses ())
        addBus (bus);
    if (changed)
      fireInstrumentRegistryChanged ();
  }
  
  @Override
  public final void removeController (final Controller controller)
  {
    if (controller == null)
      throw new IllegalArgumentException ();
    boolean changed = false;
    synchronized (this.controllersLock)
    {
      if (this.controllers.remove (controller))
      {
        this.controllersCopy = new LinkedHashSet<> (this.controllers);
        changed = true;
      }
    }    
    if (changed)
      fireInstrumentRegistryChanged ();
  }
  
  @Override
  public final List<Controller> getControllers ()
  {
    return Collections.unmodifiableList (new ArrayList<> (this.controllersCopy));
  }
  
  @Override
  public final Controller getControllerByUrl (final String url)
  {
    if (url == null || url.trim ().isEmpty ())
      return null;
    for (final Controller controller : getControllers ())
      if (controller.getControllerUrl ().equalsIgnoreCase (url))
        return controller;
    return null;    
  }

  protected final Controller getController (final Bus bus)
  {
    if (bus == null)
    {
      LOG.log (Level.WARNING, "Bus is null!");
      return null;
    }
    if (! getBuses ().contains (bus))
    {
      LOG.log (Level.WARNING, "Bus unknown/unregistered: {0}!", bus.getBusUrl ());
      return null;      
    }
    final String busUrl = bus.getBusUrl ();
    final int lastSlashIndex = busUrl.lastIndexOf ("/");
    if (lastSlashIndex < 0)
    {
      LOG.log (Level.WARNING, "No controller URL found for Bus URL {0}!", bus.getBusUrl ());
      return null;
    }
    final String controllerUrl = busUrl.substring (0, lastSlashIndex);
    final Controller controller = getControllerByUrl (controllerUrl);
    if (controller == null)
    {
      LOG.log (Level.WARNING, "Controller URL invalid or controller unknown / unregistered: {0}!", controllerUrl);
      return null;      
    }
    return controller;
  }
  
  @Override
  public final Controller openController (final String controllerUrl)
  {
    if (controllerUrl == null || controllerUrl.trim ().isEmpty ())
    {
      LOG.log (Level.WARNING, "Controller URL is null or empty!");
      return null;
    }
    if (! controllerUrl.contains ("://"))
    {
      LOG.log (Level.WARNING, "Controller URL is invalid (does not contain ://): {0}!", controllerUrl);
      return null;
    }
    final String[] controllerUrlParts = controllerUrl.split ("://");
    if (controllerUrlParts.length < 2)
    {
      LOG.log (Level.WARNING, "Controller URL is invalid (does not contain relative URL part): {0}!", controllerUrl);
      return null;
    }
    Controller controller = getControllerByUrl (controllerUrl);
    if (controller != null)
      return controller;
    final String controllerTypeName = controllerUrlParts[0];
    final ControllerType controllerType = getControllerTypeByUrl (controllerTypeName);
    if (controllerType == null)
    {
      LOG.log (Level.WARNING, "Controller Type unknown/unregistered: {0}!", controllerTypeName);
      return null;
    }
    final String relativeControllerUrl = controllerUrlParts[1];
    controller = openController (controllerType, relativeControllerUrl); // Will add controller if applicable.
    if (controller == null)
      LOG.log (Level.WARNING, "Could not open Controller: {0}!", controllerUrl);
    return controller;
  }

  @Override
  public final Controller openController (final ControllerType controllerType, final String relativeControllerUrl)
  {
    if (controllerType == null)
      throw new IllegalArgumentException ();
    if (! getControllerTypes ().contains (controllerType))
    {
      LOG.log (Level.WARNING, "Controller Type named {0} is not registered!", controllerType.getControllerTypeUrl ());
      return null;
    }
    final String controllerUrl = controllerType.getControllerTypeUrl () + "://" + relativeControllerUrl;
    Controller controller = getControllerByUrl (controllerUrl);
    if (controller != null)
      return controller;
    controller = controllerType.openController (relativeControllerUrl);
    if (controller != null)
      addController (controller);
    else
      LOG.log (Level.WARNING, "Could not open Controller: {0}!", controllerUrl);
    return controller;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // DEVICES
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Set<Device> devices = new LinkedHashSet<> ();
  
  private final Object devicesLock = new Object ();
  
  private volatile Set<Device> devicesCopy = new LinkedHashSet<> ();
  
  @Override
  public final void addDevice (final Device device)
  {
    if (device == null)
      throw new IllegalArgumentException ();
    boolean changed = false;
    synchronized (this.devicesLock)
    {
      if (! this.devices.contains (device))
      {        
        if (device.getDeviceUrl () == null || device.getDeviceUrl ().trim ().isEmpty ())
        {
          LOG.log (Level.SEVERE, "Device URL is null or empty!!");
          throw new IllegalArgumentException ();          
        }
        if (getDeviceByUrl (device.getDeviceUrl ()) != null)
        {
          LOG.log (Level.SEVERE, "Device URL {0} is already registered!", device.getDeviceUrl ());
          throw new IllegalArgumentException ();
        }
        this.devices.add (device);
        this.devicesCopy = new LinkedHashSet<> (this.devices);
        changed = true;
      }
    }
    if (changed)
      fireInstrumentRegistryChanged ();
  }
  
  @Override
  public final void removeDevice (final Device device)
  {
    if (device == null)
      throw new IllegalArgumentException ();
    boolean changed = false;
    synchronized (this.devicesLock)
    {
      if (this.devices.remove (device))
      {
        this.devicesCopy = new LinkedHashSet<> (this.devices);
        changed = true;
      }
    }    
    if (changed)
      fireInstrumentRegistryChanged ();
  }
  
  @Override
  public final List<Device> getDevices ()
  {
    return Collections.unmodifiableList (new ArrayList<> (this.devicesCopy));
  }

  @Override
  public final Device getDeviceByUrl (final String url)
  {
    if (url == null || url.trim ().isEmpty ())
      return null;
    for (final Device device : getDevices ())
      if (device.getDeviceUrl ().equalsIgnoreCase (url))
        return device;
    return null;    
  }

  @Override
  public final Device openDevice (final String deviceUrl)
  {
    if (deviceUrl == null || deviceUrl.trim ().isEmpty ())
    {
      LOG.log (Level.WARNING, "Device URL is null or empty!");
      return null;
    }
    if (! deviceUrl.contains ("#"))
    {
      LOG.log (Level.WARNING, "Device URL is invalid (does not contain #): {0}!", deviceUrl);
      return null;
    }
    final String[] deviceUrlParts = deviceUrl.split ("#");
    if (deviceUrlParts.length < 2)
    {
      LOG.log (Level.WARNING, "Device URL is invalid (does not contain relative URL part): {0}!", deviceUrl);
      return null;
    }
    Device device = getDeviceByUrl (deviceUrl);
    if (device != null)
      return device;
    final String busOrControllerUrl = deviceUrlParts[0];
    final String busAddressUrl = deviceUrlParts[1];
    Bus bus = getBusByUrl (busOrControllerUrl);
    if (bus == null)
    {
      final Controller controller = getControllerByUrl (busOrControllerUrl);
      if (controller != null)
      {
        final Bus[] buses = controller.getBuses ();
        if (buses == null || buses.length == 0)
        {
          LOG.log (Level.WARNING, "Controller {0} found, but it does not have buses attached!", busOrControllerUrl);
          return null;
        }
        if (buses.length > 1)
        {
          LOG.log (Level.WARNING, "Controller {0} found, but it has multiple buses attached!", busOrControllerUrl);
          return null;
        }
        bus = buses[0];
        if (bus == null)
        {
          LOG.log (Level.WARNING, "Bus on Controller {0} has null value!", busOrControllerUrl);
          return null;
        }
      }
    }
    if (bus == null)
    {
      LOG.log (Level.WARNING, "Bus or Controller unknown/unregistered: {0}!", busOrControllerUrl);
      return null;
    }
    final BusType busType = bus.getBusType ();
    if (busType == null)
    {
      LOG.log (Level.WARNING, "Bus {0} has null Bus Type!", bus.getBusUrl ());
      return null;
    }
    if (! getBusTypes ().contains (busType))
    {
      LOG.log (Level.WARNING, "Bus Type {0} is unregistered!", busType.getBusTypeUrl ());
      return null;
    }
    final DeviceType deviceType = busType.getDeviceType ();
    if (deviceType == null)
    {
      LOG.log (Level.WARNING, "Bus Type {0} has null Device Type!", busType.getBusTypeUrl ());
      return null;
    }
    if (! getDeviceTypes ().contains (deviceType))
    {
      LOG.log (Level.WARNING, "Device Type {0} is unregistered!", deviceType.getDeviceTypeUrl ());
      return null;
    }
    return openDevice (deviceType, bus, busAddressUrl);
  }

  @Override
  public final Device openDevice (final DeviceType deviceType, final Bus bus, final String busAddressUrl)
  {
    if (deviceType == null)
    {
      LOG.log (Level.WARNING, "Device Type is null!");
      return null;
    }
    if (! getDeviceTypes ().contains (deviceType))
    {
      LOG.log (Level.WARNING, "Device Type {0} is unknown / unregistered!", deviceType.getDeviceTypeUrl ());
      return null;      
    }
    if (bus == null)
    {
      LOG.log (Level.WARNING, "Bus is null!");
      return null;
    }
    if (! getBuses ().contains (bus))
    {
      LOG.log (Level.WARNING, "Bus {0} is unknown / unregistered!", bus.getBusUrl ());
      return null;      
    }
    if (! deviceType.getBusType ().equals (bus.getBusType ()))
    {
      LOG.log (Level.WARNING, "Bus Types for Device ({0}) and Bus ({1}) do not match!",
        new Object[]{deviceType.getBusType ().getBusTypeUrl (), bus.getBusType ().getBusTypeUrl ()});
      return null;      
    }
    final Controller controller = getController (bus);
    if (controller == null)
    {
      LOG.log (Level.WARNING, "No Controller found for Bus {0}!", bus.getBusUrl ());
      return null;            
    }
    final Device device = deviceType.openDevice (controller, busAddressUrl);
    if (device == null)
    {
      LOG.log (Level.WARNING, "Could not open Device on Bus {0} with Controller {1} and Bus Address{2}!",
        new Object[]{bus.getBusUrl (), controller.getControllerUrl (), busAddressUrl});
      return null;
    }
    final Device registeredDevice = getDeviceByUrl (device.getDeviceUrl ());
    if (registeredDevice != null)
      return registeredDevice;
    else
    {
      addDevice (device);
      return device;
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENTS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Set<Instrument> instruments = new LinkedHashSet<> ();
  
  private final Object instrumentsLock = new Object ();
  
  private volatile Set<Instrument> instrumentsCopy = new LinkedHashSet<> ();
  
  @Override
  public final void addInstrument (final Instrument instrument)
  {
    if (instrument == null)
      throw new IllegalArgumentException ();
    boolean changed = false;
    synchronized (this.instrumentsLock)
    {
      if (! this.instruments.contains (instrument))
      {        
        if (instrument.getInstrumentUrl () == null || instrument.getInstrumentUrl ().trim ().isEmpty ())
        {
          LOG.log (Level.SEVERE, "Instrument URL is null or empty!!");
          throw new IllegalArgumentException ();          
        }
        if (getInstrumentByUrl (instrument.getInstrumentUrl ()) != null)
        {
          LOG.log (Level.SEVERE, "Instrument URL {0} is already registered!", instrument.getInstrumentUrl ());
          throw new IllegalArgumentException ();
        }
        this.instruments.add (instrument);
        this.instrumentsCopy = new LinkedHashSet<> (this.instruments);
        changed = true;
      }
    }
    if (changed)
      fireInstrumentRegistryChanged ();
  }
  
  @Override
  public final void removeInstrument (final Instrument instrument)
  {
    if (instrument == null)
      throw new IllegalArgumentException ();
    boolean changed = false;
    synchronized (this.instrumentsLock)
    {
      if (this.instruments.remove (instrument))
      {
        this.instrumentsCopy = new LinkedHashSet<> (this.instruments);
        changed = true;
      }
    }    
    if (changed)
      fireInstrumentRegistryChanged ();
  }
  
  @Override
  public final List<Instrument> getInstruments ()
  {
    return Collections.unmodifiableList (new ArrayList<> (this.instrumentsCopy));
  }

  @Override
  public final Instrument getInstrumentByUrl (final String url)
  {
    if (url == null || url.trim ().isEmpty ())
      return null;
    for (final Instrument instrument: getInstruments ())
      if (instrument.getInstrumentUrl ().equalsIgnoreCase (url))
        return instrument;
    return null;    
  }

  @Override
  public Instrument openInstrument (final String instrumentUrl)
  {
    if (instrumentUrl == null || instrumentUrl.trim ().isEmpty ())
    {
      LOG.log (Level.SEVERE, "Instrument URL is null or empty!!");
      return null;
    }
    Instrument instrument = getInstrumentByUrl (instrumentUrl);
    if (instrument != null)
      return instrument;
    final String[] instrumentUrlParts = instrumentUrl.split ("@");
    if (instrumentUrlParts == null || instrumentUrlParts.length > 2)
    {
      LOG.log (Level.WARNING, "Instrument URL is invalid (contains no or more that one @): {0}!", instrumentUrl);
      return null;      
    }
    final String instrumentTypeUrl = instrumentUrlParts[0];
    final String deviceUrl = instrumentUrlParts[1];
    final InstrumentType instrumentType = getInstrumentTypeByUrl (instrumentTypeUrl);
    if (instrumentType == null)
    {
      LOG.log (Level.WARNING, "Instrument Type {0} unknown/unregistered!", instrumentTypeUrl);
      return null;
    }
    final Device device = getDeviceByUrl (deviceUrl);
    if (device == null)
    {
      LOG.log (Level.WARNING, "Device {0} unknown/unregistered!", deviceUrl);
      return null;
    }
    instrument = instrumentType.openInstrument (device);
    if (instrument == null)
    {
      LOG.log (Level.WARNING, "Could not open Instrument with Type {0} !", instrumentTypeUrl);
      return null;      
    }
    else
      addInstrument (instrument);
    return instrument;
  }

  @Override
  public final Instrument openInstrument (final InstrumentType instrumentType, final Device device)
  {
    if (instrumentType == null)
    {
      LOG.log (Level.WARNING, "Instrument Type is null!");
      return null;
    }
    if (! getInstrumentTypes ().contains (instrumentType))
    {
      LOG.log (Level.WARNING, "Instrument Type {0} is unknown / unregistered!", instrumentType.getInstrumentTypeUrl ());
      return null;
    }
    if (device == null)
    {
      LOG.log (Level.WARNING, "Device is null!");
      return null;
    }
    if (! getDevices ().contains (device))
    {
      LOG.log (Level.WARNING, "Device {0} is unknown / unregistered!", device.getDeviceUrl ());
      return null;      
    }
    final Instrument instrument = instrumentType.openInstrument (device);
    if (instrument == null)
    {
      LOG.log (Level.WARNING, "Could not open Instrument with Type {0} on Device {1}!",
        new Object[]{instrumentType.getInstrumentTypeUrl (), device.getDeviceUrl ()});
      return null;
    }
    else
      addInstrument (instrument);
    return instrument;
  }
  
  @Override
  public final InstrumentType getInstrumentType (final Instrument instrument)
  {
    if (instrument == null)
    {
      LOG.log (Level.WARNING, "Instrument is null!");
      return null;
    }
    if (! getInstruments ().contains (instrument))
    {
      LOG.log (Level.WARNING, "Instrument unknown/unregistered: {0}!", instrument.getInstrumentUrl ());
      return null;      
    }
    final String[] instrumentUrlParts = instrument.getInstrumentUrl ().split ("@");
    if (instrumentUrlParts == null || instrumentUrlParts.length != 2)
    {
      LOG.log (Level.SEVERE, "INTERNAL ERROR: Instrument URL is invalid (does not contain exactly one @): {0}!",
        instrument.getInstrumentUrl ());
      return null;      
    }
    final String instrumentTypeUrl = instrumentUrlParts[0];
    final InstrumentType instrumentType = getInstrumentTypeByUrl (instrumentTypeUrl);
    if (instrumentType == null)
    {
      LOG.log (Level.SEVERE, "INTERNAL ERROR: Could not retrieve Instrument Type for registered Instrument {0}!",
        instrument.getInstrumentUrl ());
      return null;            
    }
    return instrumentType;
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT VIEWS
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final Set<InstrumentView> instrumentViews = new LinkedHashSet<> ();
  
  private final Object instrumentViewsLock = new Object ();
  
  private volatile Set<InstrumentView> instrumentViewsCopy = new LinkedHashSet<> ();
  
  @Override
  public final void addInstrumentView (final InstrumentView instrumentView)
  {
    if (instrumentView == null)
      throw new IllegalArgumentException ();
    boolean changed = false;
    synchronized (this.instrumentViewsLock)
    {
      if (! this.instrumentViews.contains (instrumentView))
      {        
        if (instrumentView.getInstrumentViewUrl () == null || instrumentView.getInstrumentViewUrl ().trim ().isEmpty ())
        {
          LOG.log (Level.SEVERE, "Instrument View URL is null or empty!!");
          throw new IllegalArgumentException ();          
        }
        if (getInstrumentViewByUrl (instrumentView.getInstrumentViewUrl ()) != null)
        {
          LOG.log (Level.SEVERE, "Instrument View URL {0} is already registered!", instrumentView.getInstrumentViewUrl ());
          throw new IllegalArgumentException ();
        }
        this.instrumentViews.add (instrumentView);
        this.instrumentViewsCopy = new LinkedHashSet<> (this.instrumentViews);
        changed = true;
      }
    }
    if (changed)
      fireInstrumentRegistryChanged ();
  }
  
  @Override
  public final void removeInstrumentView (final InstrumentView instrumentView)
  {
    if (instrumentView == null)
      throw new IllegalArgumentException ();
    boolean changed = false;
    synchronized (this.instrumentViewsLock)
    {
      if (this.instrumentViews.remove (instrumentView))
      {
        this.instrumentViewsCopy = new LinkedHashSet<> (this.instrumentViews);
        changed = true;
      }
    }    
    if (changed)
      fireInstrumentRegistryChanged ();
  }
  
  @Override
  public final List<InstrumentView> getInstrumentViews ()
  {
    return Collections.unmodifiableList (new ArrayList<> (this.instrumentViewsCopy));
  }

  @Override
  public final InstrumentView getInstrumentViewByUrl (final String url)
  {
    if (url == null || url.trim ().isEmpty ())
      return null;
    for (final InstrumentView instrumentView: getInstrumentViews ())
      if (instrumentView.getInstrumentViewUrl ().equalsIgnoreCase (url))
        return instrumentView;
    return null;
  }

  @Override
  public final InstrumentView openInstrumentView (String instrumentViewUrl)
  {
    if (instrumentViewUrl == null || instrumentViewUrl.trim ().isEmpty ())
    {
      LOG.log (Level.SEVERE, "Instrument View URL is null or empty!!");
      return null;
    }
    InstrumentView instrumentView = getInstrumentViewByUrl (instrumentViewUrl);
    if (instrumentView != null)
      return instrumentView;
    final String[] instrumentViewUrlParts = instrumentViewUrl.split ("<>");
    if (instrumentViewUrlParts == null || instrumentViewUrlParts.length != 2)
    {
      LOG.log (Level.WARNING, "Instrument View URL is invalid (does not contain exactly one <>): {0}!", instrumentViewUrl);
      return null;      
    }
    final String instrumentViewTypeUrl = instrumentViewUrlParts[0];
    final String instrumentUrl = instrumentViewUrlParts[1];
    final InstrumentViewType instrumentViewType = getInstrumentViewTypeByUrl (instrumentViewTypeUrl);
    if (instrumentViewType == null)
    {
      LOG.log (Level.WARNING, "Instrument View Type {0} unknown/unregistered!", instrumentViewTypeUrl);
      return null;
    }
    final Instrument instrument = getInstrumentByUrl (instrumentUrl);
    if (instrument == null)
    {
      LOG.log (Level.WARNING, "Instrument {0} unknown/unregistered!", instrumentUrl);
      return null;
    }
    instrumentView = instrumentViewType.openInstrumentView (instrument);
    if (instrumentView == null)
    {
      LOG.log (Level.WARNING, "Could not open Instrument View {0}!", instrumentViewTypeUrl);
      return null;      
    }
    else
      addInstrumentView (instrumentView);
    return instrumentView;
  }

  @Override
  public final InstrumentView openInstrumentView (final InstrumentViewType instrumentViewType, final Instrument instrument)
  {
    if (instrumentViewType == null)
    {
      LOG.log (Level.WARNING, "Instrument View Type is null!");
      return null;
    }
    if (! getInstrumentViewTypes ().contains (instrumentViewType))
    {
      LOG.log (Level.WARNING, "Instrument View Type {0} is unknown / unregistered!",
        instrumentViewType.getInstrumentViewTypeUrl ());
      return null;      
    }
    if (instrument == null)
    {
      LOG.log (Level.WARNING, "Instrument is null!");
      return null;
    }
    if (! getInstruments ().contains (instrument))
    {
      LOG.log (Level.WARNING, "Instrument {0} is unknown / unregistered!", instrument.getInstrumentUrl ());
      return null;      
    }
    return openInstrumentView (instrumentViewType.getInstrumentViewTypeUrl () + "<>" + instrument.getInstrumentUrl ());
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
