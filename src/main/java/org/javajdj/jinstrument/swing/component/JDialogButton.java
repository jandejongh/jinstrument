/*
 * Copyright 2010-2021 Jan de Jongh <jfcmdejongh@gmail.com>, TNO.
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
package org.javajdj.jinstrument.swing.component;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.javajdj.jinstrument.Instrument;
import org.javajdj.jinstrument.swing.base.JInstrumentPanel;
import org.javajdj.jswing.jcenter.JCenter;
import org.javajdj.jswing.jcolorcheckbox.JColorCheckBox;

/** A {@link JInstrumentPanel} holding a {@link JColorCheckBox.JBoolean} that pops up a user provided dialog.
 *
 * @author {@literal Jan de Jongh <jfcmdejongh@gmail.com>}
 * 
 */
public class JDialogButton
  extends JInstrumentPanel
{

  public JDialogButton (
    final Instrument instrument,
    final Color boxColor,
    final String boxTitle,
    final Dimension boxPreferredSize,
    final String dialogTitle,
    final Dimension dialogSize,
    final Container contentPane)
  {
    super (instrument, boxTitle, 2, boxColor == null ? DEFAULT_MANAGEMENT_COLOR : boxColor);
    if (contentPane == null)
      throw new IllegalArgumentException ();
    setLayout (new GridLayout (1, 1));
    if (boxPreferredSize != null)
      setPreferredSize (boxPreferredSize);
    final JColorCheckBox jDisplayButton = new JColorCheckBox.JBoolean (getBackground ().darker ());
    jDisplayButton.setDisplayedValue (true);
    final JDialog jDialog = new JOptionPane ().createDialog (dialogTitle);
    if (dialogSize != null)
      jDialog.setSize (dialogSize);
    jDialog.setLocationRelativeTo (null);
    jDialog.setContentPane (contentPane);
    jDisplayButton.addActionListener ((ae) ->
    {
      jDialog.setVisible (true);
    });
    add (JCenter.XY (jDisplayButton));
  }

  public JDialogButton (
    final Instrument instrument,
    final String boxTitle,
    final Dimension boxPreferredSize,
    final String dialogTitle,
    final Dimension dialogSize,
    final Container contentPane)
  {
    this (
      instrument,
      DEFAULT_MANAGEMENT_COLOR,
      boxTitle,
      boxPreferredSize,
      dialogTitle,
      dialogSize,
      contentPane);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //
  // END OF FILE
  //
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  
}  
