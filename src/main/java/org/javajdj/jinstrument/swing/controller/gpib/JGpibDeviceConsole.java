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
package org.javajdj.jinstrument.swing.controller.gpib;

import org.javajdj.jinstrument.swing.base.JInstrumentPanel;
import org.javajdj.jinstrument.swing.default_view.JDefaultInstrumentManagementUrlsView;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import org.javajdj.jinstrument.Controller;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.controller.gpib.GpibDevice;
import org.javajdj.jinstrument.controller.gpib.ReadlineTerminationMode;
import org.javajdj.jinstrument.swing.JTinyCDIStatusAndControl;
import org.javajdj.jswing.jcolorcheckbox.JColorCheckBox;

/** A console for text-oriented communication with a {@link GpibDevice}.
 * 
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 *
 */
public class JGpibDeviceConsole
  extends JInstrumentPanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JGpibDeviceConsole.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JGpibDeviceConsole (
    final Controller controller,
    final GpibDevice device,
    final Instrument instrument,
    final int level,
    final Color subPanelColor)
  {
    super (instrument, level);
    if (controller == null || device == null || instrument == null || subPanelColor == null)
      throw new IllegalArgumentException ();
    this.controller = controller;
    this.device = device;
    this.instrument = instrument;
    setOpaque (true);
    //
    setPanelBorder (Color.black, "GPIB Console");
    setLayout (new BorderLayout ());
    //
    final JPanel northPanel = new JPanel ();
    northPanel.setLayout (new BorderLayout ());
    //
    final JPanel northNorthPanel = new JPanel ();
    northNorthPanel.setLayout (new FlowLayout (FlowLayout.LEFT));
    //
    final JPanel jTinyCDI = new JTinyCDIStatusAndControl (controller, device, instrument, level + 1, subPanelColor);
    jTinyCDI.setMinimumSize (new Dimension (40, 100));
    jTinyCDI.setPreferredSize (new Dimension (40, 100));
    northNorthPanel.add (jTinyCDI);
    //
    final JDefaultInstrumentManagementUrlsView jurls = new JDefaultInstrumentManagementUrlsView (instrument);
    jurls.setMinimumSize (new Dimension (600, 100));
    jurls.setPreferredSize (new Dimension (600, 100));
    northNorthPanel.add (jurls);
    //
    final JPanel terminationModesPanel = new JPanel ();
    terminationModesPanel.setLayout (new GridLayout (2, 1));
    terminationModesPanel.setMinimumSize (new Dimension (250, 100));
    terminationModesPanel.setPreferredSize (new Dimension (250, 100));
    this.jCTermMode = new JComboBox<> (CommandTerminationMode.values ());
    this.jCTermMode.setSelectedItem (DEFAULT_COMMAND_TERMINATION_MODE);
    setPanelBorder (this.jCTermMode, level + 2, subPanelColor, "Command Termination Mode");
    terminationModesPanel.add (this.jCTermMode);
    this.jRlTermMode = new JComboBox<> (ReadlineTerminationMode.values ());
    this.jRlTermMode.setSelectedItem (DEFAULT_READLINE_TERMINATION_MODE);
    setPanelBorder (this.jRlTermMode, level + 2, subPanelColor, "Readline Termination Mode");
    terminationModesPanel.add (this.jRlTermMode);
    northNorthPanel.add (terminationModesPanel);
    //
    final JPanel jTimeoutPanel = new JPanel ();
    jTimeoutPanel.setLayout (new GridLayout (2, 1));
    jTimeoutPanel.setMinimumSize (new Dimension (150, 100));
    jTimeoutPanel.setPreferredSize (new Dimension (150, 100));
    setPanelBorder (jTimeoutPanel, level + 2, subPanelColor, "Read Timeout [s]");
    this.jTimeoutLabel = new JLabel (Integer.toString (DEFAULT_READ_TIMEOUT_S));
    this.jTimeoutLabel.setHorizontalAlignment (SwingConstants.CENTER);
    jTimeoutPanel.add (this.jTimeoutLabel);
    this.jTimeout = new JSlider (1, 30);
    this.jTimeout.setValue (DEFAULT_READ_TIMEOUT_S);
    jTimeoutPanel.add (this.jTimeout);
    northNorthPanel.add (jTimeoutPanel);
    //
    final JPanel jBusyPanel = new JPanel ();
    setPanelBorder (jBusyPanel, level + 2, subPanelColor, "Busy");
    jBusyPanel.setLayout (new GridLayout (1, 1));
    jBusyPanel.setMinimumSize (new Dimension (100, 100));
    jBusyPanel.setPreferredSize (new Dimension (100, 100));
    this.jBusy = new JColorCheckBox<> ((Boolean t) -> t != null && t ? Color.red : null);
    jBusyPanel.add (this.jBusy);
    this.jBusy.setHorizontalAlignment (SwingConstants.CENTER);
    this.jBusy.setVerticalAlignment (SwingConstants.CENTER);
    northNorthPanel.add (jBusyPanel);
    //
    northPanel.add (northNorthPanel, BorderLayout.NORTH);    
    //
    final JTextField jTextField = new JTextField (80);
    setPanelBorder (jTextField, level + 2, subPanelColor, "Input");
    //
    northPanel.add (jTextField, BorderLayout.SOUTH);
    //
    add (northPanel, BorderLayout.NORTH);
    //
    final JTextArea jTextArea = new JTextArea (21, 80);
    jTextArea.setEditable (false);
    this.jTimeout.addChangeListener ((ChangeEvent ce) ->
    {
      this.jTimeoutLabel.setText (Integer.toString (this.jTimeout.getValue ()));
    });
    jTextField.addActionListener ((ActionEvent ae) ->
    {
      final String commandString = jTextField.getText () + getCommandTerminationString ();
      if (commandString != null && ! commandString.trim ().isEmpty ())
      {
        jTextField.setEditable (false);
        JGpibDeviceConsole.this.jBusy.setDisplayedValue (true);
        SwingUtilities.invokeLater (() ->
        {
          try
          {
            final byte[] returnBytes = JGpibDeviceConsole.this.device.writeAndReadlnSync (
              commandString.getBytes (Charset.forName ("US-ASCII")),
              (ReadlineTerminationMode) JGpibDeviceConsole.this.jRlTermMode.getSelectedItem (),
              1000L * JGpibDeviceConsole.this.jTimeout.getValue ());
            jTextArea.append (new String (returnBytes, Charset.forName ("US-ASCII")) + "\n");
            jTextField.setText ("");
          }
          catch (TimeoutException te)
          {
            jTextArea.append ("Timeout!\n");
            jTextField.setText ("");
          }
          catch (Exception e)
          {
            jTextArea.append ("Exception: " + Arrays.toString (e.getStackTrace ()) + "\n");
            jTextField.setText ("");
          }
          JGpibDeviceConsole.this.jBusy.setDisplayedValue (false);
          jTextField.setEditable (true);
        });
      }
    });
    setPanelBorder (jTextArea, level + 1, subPanelColor, "Output");
    add (new JScrollPane (jTextArea), BorderLayout.CENTER);
  }
  
  public JGpibDeviceConsole (
    final Instrument instrument,
    final int level,
    final Color subPanelColor)
  {
    this (instrument.getDevice ().getController (),
      (GpibDevice) instrument.getDevice (),
      instrument,
      level,
      subPanelColor);
  }
  
  public JGpibDeviceConsole (
    final Instrument instrument,
    final int level)
  {
    this (instrument.getDevice ().getController (),
      (GpibDevice) instrument.getDevice (),
      instrument,
      level,
      JInstrumentPanel.DEFAULT_MANAGEMENT_COLOR);
  }
  
  public JGpibDeviceConsole (final Instrument instrument)
  {
    this (instrument.getDevice ().getController (),
      (GpibDevice) instrument.getDevice (),
      instrument,
      1,
      JInstrumentPanel.DEFAULT_MANAGEMENT_COLOR);
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // INSTRUMENT VIEW TYPE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public final static InstrumentViewType INSTRUMENT_VIEW_TYPE = new InstrumentViewType ()
  {
    
    @Override
    public final String getInstrumentViewTypeUrl ()
    {
      return "GPIB Console";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null)
        return new JGpibDeviceConsole (instrument);
      else
        return null;
    }
    
  };
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // InstrumentView
  // URL / NAME / toString
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  @Override
  public String getInstrumentViewUrl ()
  {
    return JGpibDeviceConsole.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
      + "<>"
      + getInstrument ().getInstrumentUrl ();
  }
  
  @Override
  public String toString ()
  {
    return getInstrumentViewUrl ();
  }
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // TIMEOUT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  public final static int DEFAULT_READ_TIMEOUT_S = 5;
  
  private final JSlider jTimeout;
  
  private final JLabel jTimeoutLabel;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // BUSY
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private final JColorCheckBox<Boolean> jBusy;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONTROLLER
  // GPIB DEVICE
  // INSTRUMENT
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
  private final Controller controller;
  
  private final GpibDevice device;
  
  private final Instrument instrument;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // COMMAND TERMINATION MODE
  // READLINE TERMINATION MODE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public enum CommandTerminationMode
  {
    NONE,
    CR,
    LF,
    CR_LF,
    LF_CR,
    ESC;
  }
  
  public static final CommandTerminationMode DEFAULT_COMMAND_TERMINATION_MODE = CommandTerminationMode.LF;
  
  private final JComboBox<CommandTerminationMode> jCTermMode;
  
  private String getCommandTerminationString ()
  {
    switch ((CommandTerminationMode) this.jCTermMode.getSelectedItem ())
    {
      case NONE: return "";
      case CR: return "\r";
      case LF: return "\n";
      case CR_LF: return "\r\n";
      case LF_CR: return "\n\r";
      case ESC: return "u001b";
      default: throw new RuntimeException ();
    }
  }
  
  private final JComboBox<ReadlineTerminationMode> jRlTermMode;
  
  public static final ReadlineTerminationMode DEFAULT_READLINE_TERMINATION_MODE =
    ReadlineTerminationMode.CR_LF;
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
