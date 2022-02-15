/*
 * Copyright 2010-2022 Jan de Jongh <jfcmdejongh@gmail.com>.
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
package org.javajdj.jinstrument.swing.instrument.slm.rs_esh3;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.InstrumentView;
import org.javajdj.jinstrument.InstrumentViewType;
import org.javajdj.jinstrument.SelectiveLevelMeter;
import org.javajdj.jinstrument.gpib.slm.rs_esh3.RS_ESH3_GPIB_Instrument;
import org.javajdj.jinstrument.swing.base.JInstrumentPanel;
import org.javajdj.jinstrument.swing.base.JSelectiveLevelMeterPanel;
import org.javajdj.jswing.jcenter.JCenter;
import org.javajdj.jswing.jcolorcheckbox.JColorCheckBox;

/** A Swing panel for the Store/Recall commands
 *    of a {@link RS_ESH3_GPIB_Instrument} Selective Level Meter.
 *
 * @author Jan de Jongh {@literal <jfcmdejongh@gmail.com>}
 * 
 */
public class JRS_ESH3_GPIB_StoreRecall
  extends JSelectiveLevelMeterPanel
  implements InstrumentView
{
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // LOGGER
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private static final Logger LOG = Logger.getLogger (JRS_ESH3_GPIB_StoreRecall.class.getName ());
  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // CONSTRUCTOR(S) / FACTORY / CLONING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  public JRS_ESH3_GPIB_StoreRecall (
    final SelectiveLevelMeter selectiveLevelMeter,
    final String title,
    final int level,
    final Color panelColor)
  {

    super (selectiveLevelMeter, title, level, panelColor);
    if (! (selectiveLevelMeter instanceof RS_ESH3_GPIB_Instrument))
      throw new IllegalArgumentException ();
    final RS_ESH3_GPIB_Instrument rs_esh3 = (RS_ESH3_GPIB_Instrument) selectiveLevelMeter;

    removeAll ();
    setLayout (new GridLayout (2, 1));
    
    final JInstrumentPanel storePanel = new JInstrumentPanel (
      rs_esh3,
      "Store",
      level + 1,
      getGuiPreferencesManagementColor ());
    add (storePanel);
    storePanel.setLayout (new GridLayout (2, 10));
    storePanel.add (new JPanel ());
    for (int i = 1; i <= 9; i++)
    {
      final JColorCheckBox jStoreButton = new JColorCheckBox ((t) -> Color.blue);
      jStoreButton.addActionListener (new StoreButtonListener (i));
      storePanel.add (JCenter.XY (jStoreButton));
    }
    storePanel.add (new JPanel ());
    for (int i = 1; i <= 9; i++)
      storePanel.add (JCenter.XY (new JLabel (Integer.toString (i))));
    
    final JInstrumentPanel recallPanel = new JInstrumentPanel (
      rs_esh3,
      "Recall",
      level + 1,
      getGuiPreferencesManagementColor ());
    add (recallPanel);
    recallPanel.setLayout (new GridLayout (2, 10));
    for (int i = 0; i <= 9; i++)
    {
      final JColorCheckBox jRecallButton = new JColorCheckBox ((t) -> Color.blue);
      jRecallButton.addActionListener (new RecallButtonListener (i));
      recallPanel.add (JCenter.XY (jRecallButton));
    }
    for (int i = 0; i <= 9; i++)
      recallPanel.add (JCenter.XY (new JLabel (Integer.toString (i))));
    
  }

  public JRS_ESH3_GPIB_StoreRecall (final RS_ESH3_GPIB_Instrument selectiveLevelMeter, final int level)
  {
    this (selectiveLevelMeter, null, level, null);
  }
  
  public JRS_ESH3_GPIB_StoreRecall (final RS_ESH3_GPIB_Instrument selectiveLevelMeter)
  {
    this (selectiveLevelMeter, 0);
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
      return "R&S ESH-3 [GPIB] Store/Recall Commands";
    }

    @Override
    public final InstrumentView openInstrumentView (final Instrument instrument)
    {
      if (instrument != null && (instrument instanceof RS_ESH3_GPIB_Instrument))
        return new JRS_ESH3_GPIB_StoreRecall ((RS_ESH3_GPIB_Instrument) instrument);
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
    return JRS_ESH3_GPIB_StoreRecall.INSTRUMENT_VIEW_TYPE.getInstrumentViewTypeUrl ()
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
  // SWING
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
  private final class StoreButtonListener
    implements ActionListener
  {

    private StoreButtonListener (final int slot)
    {
      this.slot = slot;
    }
    
    private final int slot;

    @Override
    public final void actionPerformed (final ActionEvent ae)
    {
      try
      {
        ((RS_ESH3_GPIB_Instrument) getSelectiveLevelMeter ()).store (StoreButtonListener.this.slot);
      }
      catch (Exception e)
      {
        LOG.log (Level.WARNING, "Caught exception while running Store {0} on instrument {1}: {2}.",        
          new Object[]
            {StoreButtonListener.this.slot, getInstrument (), Arrays.toString (e.getStackTrace ())});
      }
    }
        
  }
    
  private final class RecallButtonListener
    implements ActionListener
  {

    private RecallButtonListener (final int slot)
    {
      this.slot = slot;
    }
    
    private final int slot;

    @Override
    public final void actionPerformed (final ActionEvent ae)
    {
      try
      {
        ((RS_ESH3_GPIB_Instrument) getSelectiveLevelMeter ()).recall (RecallButtonListener.this.slot);
      }
      catch (Exception e)
      {
        LOG.log (Level.WARNING, "Caught exception while running Recall {0} on instrument {1}: {2}.",        
          new Object[]
            {RecallButtonListener.this.slot, getInstrument (), Arrays.toString (e.getStackTrace ())});
      }
    }
        
  }
    
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}
